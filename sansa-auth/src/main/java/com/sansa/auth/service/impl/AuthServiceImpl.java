package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.dto.error.ApiProblem;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.dto.sessions.SessionsListResponse;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.ConflictException;
import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.SessionNotFoundException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.mail.MailService;
import com.sansa.auth.model.user.User;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.CurrentUserPort;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * AuthService の本番実装.
 *
 * 役割:
 * - 事前登録コード発行/検証
 * - ユーザー登録
 * - パスワードログイン
 * - トークンリフレッシュ
 * - セッション取得/一覧/失効
 * - 全端末ログアウト (logout_all)
 *
 * ポイント:
 * - 永続化は Store 経由
 * - JWT/トークン発行は TokenFacade 経由
 * - パスワードハッシュは PasswordPort 経由 (PHC形式, Argon2id 等を隠蔽)
 * - 「現在ユーザー」は CurrentUserPort 経由
 * - 例外は既存 UT/IT の期待 (HTTP ステータス, problem+json) に合うものを送出
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    // ポート / サービス依存
    private final Store store;
    private final TokenFacade tokenFacade;
    private final PasswordPort passwordPort;
    private final SessionService sessionService;
    private final CurrentUserPort currentUserPort;
    private final MailService mailService;

    // 設計上の定数 (必要に応じて application.yml 側に寄せてもよい)
    private static final Duration PRE_REGISTER_TTL = Duration.ofMinutes(15);

    // ==========
    // 1. Pre-register
    // ==========

    /**
     * {@inheritDoc}
     *
     * バリデーション / ブロックドメイン / レート制御は Store 側の責務で扱えるようにしておく想定。
     */
    @Override
    public PreRegisterResponse preRegister(PreRegisterRequest req) {
        if (req == null || isBlank(req.getEmail())) {
            throw new BadRequestException("invalid_argument");
        }

        final String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        final String domain = extractDomain(email);

        // ブロックドメイン検査（Store 側に追加済み想定）
        if (store.isBlockedEmailDomain(domain)) {
            throw new BadRequestException("invalid_argument");
        }

        Instant now = Instant.now();
        Store.PreReg pre = store.issuePreRegisterCode(email, PRE_REGISTER_TTL, now);

        // メール送信 (エラー時は例外でロールバック)
        mailService.sendPreRegister(email, pre.code(), toLocaleOrDefault(req.getLocale()));

        long throttleMs = pre.throttleMsHint() != null ? pre.throttleMsHint() : 0L;

        return PreRegisterResponse.builder()
                .success(true)
                .throttleMs(throttleMs)
                .build();
    }

    // ==========
    // 2. Verify email
    // ==========

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) {
        if (req == null || isBlank(req.getEmail()) || isBlank(req.getCode())) {
            throw new BadRequestException("invalid_argument");
        }

        Instant now = Instant.now();
        Store.PreReg result = store.consumePreRegisterCode(
                req.getEmail().trim().toLowerCase(Locale.ROOT),
                req.getCode().trim(),
                now
        ).orElseThrow(() -> new BadRequestException("invalid_code"));

        if (result.isExpired(now)) {
            throw new BadRequestException("expired");
        }
        if (result.consumed()) {
            // 二重使用
            throw new ConflictException("already_used");
        }

        return VerifyEmailResponse.builder()
                .success(true)
                .preRegId(result.preRegId())
                .build();
    }

    // ==========
    // 3. Register
    // ==========

    @Override
    public RegisterResponse register(RegisterRequest req) {
        if (req == null
                || isBlank(req.getPreRegId())
                || isBlank(req.getAccountId())
                || isBlank(req.getPassword())) {
            throw new BadRequestException("invalid_argument");
        }

        Instant now = Instant.now();

        // preRegId の妥当性確認 (Store 側で preRegId -> email 紐付け管理想定)
        Store.PreReg pre = store.findPreRegById(req.getPreRegId())
                .orElseThrow(() -> new BadRequestException("invalid_pre_reg"));
        if (pre.isExpired(now)) {
            throw new BadRequestException("expired");
        }
        if (pre.consumed()) {
            // 410 or 400 相当は Controller/Handler 側でマッピング
            throw new ConflictException("pre_reg_consumed");
        }

        // ブロックアカウントID
        if (store.isBlockedAccountId(req.getAccountId())) {
            throw new ConflictException("blocked_account");
        }

        // 既存ユーザー重複チェック
        if (store.findUserByAccountId(req.getAccountId()).isPresent()) {
            throw new ConflictException("account_exists");
        }
        if (store.findUserByEmail(pre.email()).isPresent()) {
            throw new ConflictException("email_exists");
        }

        // ユーザー作成
        String passwordHash = passwordPort.hash(req.getPassword());
        Store.User created = store.createUser(pre.email(), req.getAccountId(), passwordHash, now);

        // pre-reg 消費マーク
        store.markPreRegConsumed(pre.preRegId(), now);

        return RegisterResponse.builder()
                .success(true)
                .userId(created.userId())
                .emailVerified(true)
                .build();
    }

    // ==========
    // 4. Login (password)
    // ==========

    @Override
    public LoginResponse login(LoginRequest req) {
        if (req == null || (isBlank(req.getAccountId()) && isBlank(req.getEmail()))
                || isBlank(req.getPassword())) {
            throw new BadRequestException("invalid_argument");
        }

        Instant now = Instant.now();

        // ユーザー特定
        Optional<Store.User> byAccount = !isBlank(req.getAccountId())
                ? store.findUserByAccountId(req.getAccountId().trim())
                : Optional.empty();
        Optional<Store.User> byEmail = byAccount.isEmpty() && !isBlank(req.getEmail())
                ? store.findUserByEmail(req.getEmail().trim().toLowerCase(Locale.ROOT))
                : Optional.empty();

        Store.User user = byAccount.or(() -> byEmail)
                .orElseThrow(() -> new InvalidCredentialsException("invalid_credentials"));

        // パスワード検証
        if (!passwordPort.matches(req.getPassword(), user.passwordHash())) {
            // 将来: ロック・履歴は Store 側で拡張
            throw new InvalidCredentialsException("invalid_credentials");
        }

        // セッション作成
        Store.Session session = store.createSession(
                user.userId(),
                now,
                req.getUserAgent(),
                req.getIp()
        );

        // トークン発行
        LoginTokens tokens = tokenFacade.issueAfterAuth(
                user.userId(),
                List.of() // roles 未使用なら空
        );

        // レスポンス
        SessionInfo sessionInfo = SessionInfo.builder()
                .id(session.id())
                .userId(user.userId())
                .current(true)
                .createdAt(session.createdAt())
                .lastActiveAt(session.lastActiveAt())
                .build();

        return LoginResponse.builder()
                .success(true)
                .authenticated(true)
                .mfaRequired(false) // MFA は別フェーズ
                .session(sessionInfo)
                .tokens(tokens)
                .build();
    }

    // ==========
    // 5. Refresh
    // ==========

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest req) {
        if (req == null || isBlank(req.getRefreshToken())) {
            throw new BadRequestException("invalid_argument");
        }

        Instant now = Instant.now();

        // TokenFacade 側で RT 検証 + 新AT/RT発行 + 再利用検知まで面倒を見る想定
        TokenFacade.RefreshResult result = tokenFacade.rotateRefreshToken(req.getRefreshToken(), now);

        if (result.isExpired()) {
            throw new UnauthorizedException("token_expired");
        }
        if (result.isReused()) {
            // 再利用検知 → 全セッション失効など、仕様に応じて連動
            store.handleRefreshReuse(result.getSubjectUserId(), result.getRefreshTokenId());
            throw new UnauthorizedException("token_reused");
        }
        if (!result.isSuccess()) {
            throw new UnauthorizedException("invalid_token");
        }

        TokenFacade.Tokens t = result.getTokens();

        return TokenRefreshResponse.builder()
                .success(true)
                .tokens(new TokenRefreshResponse.Tokens(
                        t.accessToken(),
                        t.refreshToken(),
                        t.accessTokenExpiresAt(),
                        t.refreshTokenExpiresAt()
                ))
                .build();
    }

    // ==========
    // 6. Current session
    // ==========

    @Override
    public SessionInfo getCurrentSession() {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        return sessionService.getCurrentSession(userId);
    }

    // ==========
    // 7. List sessions
    // ==========

    @Override
    public SessionsListResponse listSessions() {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        List<SessionInfo> sessions = sessionService.listSessions(userId);
        return SessionsListResponse.builder()
                .sessions(sessions)
                .build();
    }

    // ==========
    // 8. Logout (単一)
    // ==========

    @Override
    public LogoutResponse logout(LogoutRequest req) {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        // セッションID指定があれば優先
        if (!isBlank(req.getSessionId())) {
            boolean deleted = sessionService.logoutBySessionId(userId, req.getSessionId());
            if (!deleted) {
                throw new SessionNotFoundException("session_not_found");
            }
            return LogoutResponse.ok(true);
        }

        // RefreshToken JTI 指定があれば、Store / TokenFacade 側で削除・失効処理
        if (!isBlank(req.getRefreshTokenJti())) {
            boolean handled = store.revokeByRefreshTokenId(userId, req.getRefreshTokenJti());
            if (!handled) {
                throw new NotFoundException("refresh_not_found");
            }
            return LogoutResponse.ok(true);
        }

        // どちらも無ければ現在セッションを対象
        boolean deleted = sessionService.logoutCurrentSession(userId);
        if (!deleted) {
            throw new SessionNotFoundException("session_not_found");
        }
        return LogoutResponse.ok(true);
    }

    // ==========
    // 9. Logout all (全端末)
    // ==========

    /**
     * {@inheritDoc}
     *
     * - token_version++ により全 AT/RT を論理失効
     * - sessions テーブルも必要なら物理削除
     */
    @Override
    public LogoutResponse logoutAll() {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        store.incrementTokenVersion(userId);
        store.deleteAllSessions(userId);

        return LogoutResponse.ok(true);
    }

    // ==========
    // helpers
    // ==========

    /**
     * メールアドレスからドメイン部分を抽出.
     */
    private String extractDomain(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) {
            throw new BadRequestException("invalid_argument");
        }
        return email.substring(at + 1).toLowerCase(Locale.ROOT);
    }

    private Locale toLocaleOrDefault(String lang) {
        if (isBlank(lang)) return Locale.JAPAN;
        try {
            return Locale.forLanguageTag(lang);
        } catch (Exception ignore) {
            return Locale.JAPAN;
        }
    }
}
