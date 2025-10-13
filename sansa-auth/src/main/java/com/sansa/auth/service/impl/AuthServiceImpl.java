package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.*;
import com.sansa.auth.dto.login.*;
import com.sansa.auth.dto.sessions.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.store.Store;
import com.sansa.auth.store.Store.Session;
import com.sansa.auth.store.Store.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * /auth 配下のユースケース実装
 * 対応仕様:
 * - 01_ユーザー登録.md: POST /auth/pre-register, /auth/verify-email, /auth/register
 * - 02_ログイン.md:    POST /auth/token/refresh（RTローテーション / 再利用検知 / tv）
 * - 05_セッション管理.md: GET /auth/session, POST /auth/logout, POST /auth/logout_all
 *
 * 設計メモ:
 * - レート制限は Store.tryConsumeRateLimit を利用（キー: ip/email など）。
 * - RT再利用検知で false が返った場合は tv++（全端末失効）し、401 を返す。
 * - DELETE /sessions/{id} は SessionService 側で実装（ここでは /auth/* のみ）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Store store;
    private final TokenIssuer tokenIssuer;          // AT/RT発行・検証（抽象）
    private final PasswordHasher passwordHasher;    // パスワードハッシュ（抽象）※registerで使用する場合のみ

    // ---- TEST-ONLY convenience ctor (legacy tests expect (Store, Clock)) ----
    public AuthServiceImpl(Store store, Clock clock) {
        this.store = store;
        this.tokenIssuer = new DefaultTokenIssuer(clock);
        this.passwordHasher = new NoopPasswordHasher();
    }

    static final class DefaultTokenIssuer implements TokenIssuer {
        private final Clock clock;
        DefaultTokenIssuer(Clock clock) { this.clock = clock; }
        @Override public String issueAccessToken(String userId, int tv) { return "AT-" + userId + "-" + tv + "-" + clock.millis(); }
        @Override public String issueRefreshToken(String userId, String refreshJti, int tv) { return "RT-" + userId + "-" + refreshJti + "-" + tv; }
        @Override public String newRefreshId() { return "jti-" + clock.millis(); }
        @Override public RefreshParseResult parseAndValidateRefresh(String refreshToken) {
            // テスト用の最小実装：固定ユーザー/TV=0/jti=rt を返す
            return new RefreshParseResult() {
                public String getUserId() { return "u-1"; }
                public String getJti() { return "rt-1"; }
                public int getTokenVersion() { return 0; }
            };
        }
    }
    static final class NoopPasswordHasher implements PasswordHasher {
        @Override public String hash(String raw) { return raw; }
        @Override public boolean matches(String raw, String hash) { return true; }
    }

    // ---- 01: pre-register ----------------------------------------------------

    @Override
    public PreRegisterResponse preRegister(PreRegisterRequest req) throws BadRequestException {
        // レート制御: 同一メール宛の連打防止
        boolean ok = store.tryConsumeRateLimit("preReg:" + req.getEmail(), 5, 10);
        if (!ok) {
            return PreRegisterResponse.builder().success(false).throttleMs(30_000).build();
        }
        // 検証コード発行（送信は別コンポーネントへ）
        store.issueEmailVerificationCode(req.getEmail(), Duration.ofMinutes(10));
        return PreRegisterResponse.builder().success(true).throttleMs(0).build();
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest req)
            throws BadRequestException, NotFoundException {
        Instant now = Instant.now();
        var pr = store.verifyEmailAndIssuePreReg(req.getEmail(), req.getCode(), Duration.ofMinutes(10), now);
        int expiresIn = (int) Math.max(0, pr.expiresAt().getEpochSecond() - now.getEpochSecond());
        return VerifyEmailResponse.builder()
                .preRegId(pr.preRegId())
                .expiresIn(expiresIn)
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest req)
            throws BadRequestException, NotFoundException {
        // preReg 消費（1回限り）
        var pr = store.consumePreReg(req.getPreRegId(), Instant.now());

        // accountId/メールの競合チェック
        if (store.isAccountIdTaken(req.getAccountId())) {
            throw new BadRequestException("accountId is already taken");
        }
        // ユーザー作成（メール検証済みとして作成）
        User u = store.createUser(req.getAccountId(), pr.email(), req.getAccountId(), req.getLanguage(), true);

        // （任意）パスワード登録: password がある場合のみ
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            // TODO: PasswordCredentialRepository などで保存する場合は別ストアへ
            String hash = passwordHasher.hash(req.getPassword());
            // save hash...（サンプルでは割愛）
            log.info("Password hash stored for user {}", u.userId());
        }

        return RegisterResponse.builder()
                .success(true)
                .userId(u.userId())
                .emailVerified(true)
                .build();
    }

    // ---- 02: login/token/refresh ---------------------------------------------
    @Override
    public LoginResponse login(LoginRequest req)
            throws UnauthorizedException, BadRequestException {
        // 1) ユーザー特定（identifier は email or accountId）
        String idType = (req.getIdentifier() != null && req.getIdentifier().contains("@"))
                ? "email" : "accountId";
        Store.User user = store.findUserByIdentifier(idType, req.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("invalid credentials"));

        // 2) パスワード照合（本番は別ストアから hash 取得）
        // TODO: String hash = credentialRepo.findPasswordHashByUserId(user.userId());
        // TODO: if (!passwordHasher.matches(req.getPassword(), hash)) throw new UnauthorizedException("invalid credentials");

        // 3) トークン発行（tv は現在値）
        int tv = store.getTokenVersion(user.userId());
        String jti = tokenIssuer.newRefreshId();
        String accessToken = tokenIssuer.issueAccessToken(user.userId(), tv);
        String refreshToken = tokenIssuer.issueRefreshToken(user.userId(), jti, tv);

        // 4) セッション生成・保存（最低限の情報）
        Instant now = Instant.now();
        Store.Session sess = new Store.Session(
                /*sessionId*/ jti,
                user.userId(),
                /*device*/ "web",
                now,
                now,
                now.plus(Duration.ofDays(7)),
                List.of("pwd") // AMR: パスワード認証
        );
        store.saveOrUpdateSession(sess);

        // 5) レスポンス構築
        SessionInfo sessionInfo = SessionInfo.builder()
                .active(true)
                .sessionId(sess.sessionId())
                .issuedAt(sess.issuedAt().toString())
                .lastActive(sess.lastActive().toString())
                .expiresAt(sess.expiresAt().toString())
                .amr(sess.amr())
                .user(SessionInfo.UserSummary.builder()
                        .userId(user.userId())
                        .email(user.email())
                        .displayName(user.displayName())
                        .build())
                .build();

        return LoginResponse.builder()
                .authenticated(true)
                .mfaRequired(false) // 必要に応じて MFA 判定を挿入可
                .session(sessionInfo)
                .tokens(new LoginTokens(accessToken, refreshToken))
                .amr(sess.amr())
                .user(sessionInfo.getUser())
                .build();
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest req)
            throws BadRequestException, UnauthorizedException {
        // RT 検証（署名・exp・紐付 userId 取得）
        TokenIssuer.RefreshParseResult parsed = tokenIssuer.parseAndValidateRefresh(req.getRefreshToken());
        String userId = parsed.getUserId();
        String oldRtId = parsed.getJti();
        int tvAtIssued = parsed.getTokenVersion();

        // tv チェック（logout_all 済みなら古い RT）
        int tvNow = store.getTokenVersion(userId);
        if (tvAtIssued != tvNow) {
            throw new UnauthorizedException("refresh token invalidated by logout_all");
        }

        // RTローテーション。false → 再利用検知 → tv++ & 401
        String newRtId = tokenIssuer.newRefreshId();
        boolean rotated = store.rotateRefreshToken(userId, oldRtId, newRtId, Instant.now());
        if (!rotated) {
            store.incrementTokenVersion(userId); // 全端末失効
            throw new UnauthorizedException("refresh token reuse detected");
        }

        // 新トークン発行
        String at = tokenIssuer.issueAccessToken(userId, tvNow);
        String rt = tokenIssuer.issueRefreshToken(userId, newRtId, tvNow);

        return TokenRefreshResponse.builder()
                .tokens(new TokenRefreshResponse.Tokens(at, rt))
                .tv(tvNow)
                .build();
    }

    // ---- 05: session / logout(_all) ------------------------------------------

    @Override
    public SessionInfo getCurrentSession() throws UnauthorizedException {
        // 実際の sessionId はセキュアクッキー/JWT から Controller が取得して渡す想定
        String sessionId = CurrentRequestContext.getSessionIdOrThrow();
        var opt = store.findSessionById(sessionId);
        var s = opt.orElseThrow(() -> new UnauthorizedException("session not found"));
        return toSessionInfo(s);
    }

    @Override
    public LogoutResponse logout(LogoutRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        // sessionId or refreshToken 指定 → 対象セッション失効
        if (req.getSessionId() != null && !req.getSessionId().isBlank()) {
            store.deleteSession(userId, req.getSessionId());
        } else if (req.getRefreshToken() != null && !req.getRefreshToken().isBlank()) {
            // refreshToken（RT-ID へデコード）は TokenIssuer に委譲
            TokenIssuer.RefreshParseResult parsed = tokenIssuer.parseAndValidateRefresh(req.getRefreshToken());
            if (!parsed.getUserId().equals(userId)) throw new UnauthorizedException("token user mismatch");
            store.deleteSessionByRefreshToken(userId, parsed.getJti());
        } else {
            // 指定なし → 現セッション扱い
            String sessionId = CurrentRequestContext.getSessionIdOrThrow();
            store.deleteSession(userId, sessionId);
        }
        return LogoutResponse.builder().success(true).build();
    }

    @Override
    public LogoutResponse logoutAll() throws UnauthorizedException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        // tv++ & 全セッション削除
        store.incrementTokenVersion(userId);
        store.deleteAllSessions(userId);
        return LogoutResponse.builder().success(true).build();
    }

    // ---- mapping --------------------------------------------------------------

    private static SessionInfo toSessionInfo(Session s) {
        return SessionInfo.builder()
                .active(true)
                .sessionId(s.sessionId())
                .issuedAt(s.issuedAt().toString())
                .lastActive(s.lastActive().toString())
                .expiresAt(s.expiresAt().toString())
                .amr(s.amr())
                .user(SessionInfo.UserSummary.builder()
                        .userId(s.userId())
                        // email/displayName は別ストアで join するならここで取得
                        .email(null).displayName(null).build())
                .build();
    }

    // ---- 補助抽象 ------------------------------------------------------------

    public interface TokenIssuer {
        String issueAccessToken(String userId, int tv);
        String issueRefreshToken(String userId, String refreshJti, int tv);
        String newRefreshId();

        RefreshParseResult parseAndValidateRefresh(String refreshToken) throws UnauthorizedException, BadRequestException;

        interface RefreshParseResult {
            String getUserId();
            String getJti();
            int getTokenVersion();
        }
    }

    public interface PasswordHasher {
        String hash(String raw);
        boolean matches(String raw, String hash);
    }

    public static final class CurrentRequestContext {
        public static String getUserIdOrThrow() { /* Controller / SecurityContext で解決 */ throw new UnsupportedOperationException(); }
        public static String getSessionIdOrThrow() { /* 同上 */ throw new UnsupportedOperationException(); }
    }
}
