package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
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
import com.sansa.auth.model.user.User;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.CurrentUserPort;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;

import org.springframework.stereotype.Service;

import java.net.IDN;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 役割: 認証ユースケースの本番ロジック（Core）。
 * 注意点:
 *   - DTO/Store/TokenFacade の“現物”に厳密準拠（未定義APIは使用しない）
 *   - レート制限や監査は Store/他サービスへ委譲（本クラスは順序制御のみ）
 *   - パスワードは PasswordPort でPHC形式にハッシュし、Storeへ保存
 * 主要依存:
 *   - Store: 永続化・レート制限・セッション・WebAuthn等の集約
 *   - TokenFacade: AT/RTの発行・ローテーション・Refresh JTI抽出
 *   - PasswordPort: Argon2id でのハッシュ/検証/リハッシュ判定
 * 例外:
 *   - バリデーション失敗→ BadRequestException
 *   - 競合（重複等）→ ConflictException
 *   - 資格不正→ InvalidCredentialsException / UnauthorizedException
 *   - セッション未検出→ SessionNotFoundException / NotFoundException
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final Store store;
    private final TokenFacade tokenFacade;
    private final PasswordPort passwordPort;
    private final SessionService sessionService;
    private final CurrentUserPort currentUserPort;

    public AuthServiceImpl(Store store,
                           TokenFacade tokenFacade,
                           PasswordPort passwordPort,
                           SessionService sessionService,
                           CurrentUserPort currentUserPort) {
        this.store = store;
        this.tokenFacade = tokenFacade;
        this.passwordPort = passwordPort;
        this.sessionService = sessionService;
        this.currentUserPort = currentUserPort;
    }

    // ========================
    // 事前登録 / メール認証
    // ========================

    /**
     * 事前登録コードの発行。
     * 引数:
     *   - req.email: 対象メール
     *   - req.language: 言語コード（ロケール変換はコントローラ側責務）
     * 戻り:
     *   - PreRegisterResponse(accepted, throttleMs)
     * 挙動:
     *   - ドメインブロック判定（Store側）
     *   - レート制限（必要ならStore側にキー設計して呼び出し）
     *   - メール認証コードの発行（Store現行APIに合わせる）
     */
    @Override
    public PreRegisterResponse preRegister(PreRegisterRequest req) {
        if (req == null || isBlank(req.getEmail())) {
            throw new BadRequestException("invalid-argument");
        }
        final String email = req.getEmail().trim();
        final String domain = normalizeDomain(extractDomain(email));

        // 禁止ドメイン（Bで承認済み: Store集約）
        if (store.isBlockedEmailDomain(domain)) {
            throw new BadRequestException("blocked-domain");
        }

        // レート制限（存在するなら使用。無ければスキップ）
        // 例: store.tryConsumeRateLimit("pre-reg:email:"+emailNormalized, burst, refill)
        // 今回はロジック無変更方針のため未呼出しでも可

        // 仕様準拠: 現行Store APIに合わせる
        // 例: issueEmailVerificationCode(email, ttl)
        // TTLは仕様ファイルの推奨値に合わせる（仮: 10分）
        final Duration codeTtl = Duration.ofMinutes(10);
        store.issueEmailVerificationCode(email, codeTtl);

        // レスポンスは (success, throttleMs)。骨格時点はスロットリング0
        return new PreRegisterResponse(true, 0L);
    }

    /**
     * メール認証→preRegId付与。
     * 引数:
     *   - req.email, req.code
     * 戻り:
     *   - VerifyEmailResponse(preRegId, expiresInSeconds)
     * 挙動:
     *   - verifyEmailAndIssuePreReg(...) に準拠
     */
    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) {
        if (req == null || isBlank(req.getEmail()) || isBlank(req.getCode())) {
            throw new BadRequestException("invalid-argument");
        }
        final Instant now = Instant.now();

        // Storeの仕様に合わせる（事実としてこの名前が存在）
        var result = store.verifyEmailAndIssuePreReg(req.getEmail().trim(), req.getCode().trim(),
                Duration.ofMinutes(30), // preRegの有効期限（仕様準拠・仮30分）
                now);

        // result から preRegId と expiresIn を取得できる想定
        // もし型が Optional 等なら、未検証はBadRequest相当
        if (result == null || isBlank(result.preRegId()) || result.expiresInSeconds() < 1) {
            throw new BadRequestException("invalid_code");
        }
        return new VerifyEmailResponse(result.preRegId(), result.expiresInSeconds());
    }

    // ================
    // 本登録 / ログイン
    // ================

    /**
     * 本登録。
     * 引数:
     *   - req.preRegId, req.accountId, req.password, req.language
     * 戻り:
     *   - RegisterResponse(success, userId, emailVerified=true)
     * 手順:
     *   1) preReg 消費（Store.consumePreReg）
     *   2) アカウントIDブロック判定・重複判定
     *   3) ユーザー作成
     *   4) PHCハッシュ保存（PasswordPort）
     */
    @Override
    public RegisterResponse register(RegisterRequest req) {
        if (req == null || isBlank(req.getPreRegId())
                || isBlank(req.getAccountId()) || isBlank(req.getPassword())) {
            throw new BadRequestException("invalid-argument");
        }
        final Instant now = Instant.now();

        // 1) preReg 消費
        var pre = store.consumePreReg(req.getPreRegId().trim(), now);
        if (pre == null || isBlank(pre.email())) {
            // 410/400 は Controller 側マッピングに従う
            throw new BadRequestException("preReg-invalid");
        }

        // 2) ブロック＆重複
        if (store.isBlockedAccountId(req.getAccountId().trim())) {
            throw new ConflictException("account-blocked");
        }
        if (store.isAccountIdTaken(req.getAccountId().trim())) {
            throw new ConflictException("accountId-duplicated");
        }

        // 3) ユーザー作成（メール検証済み）
        User user = store.createUser(
                req.getAccountId().trim(),
                pre.email(),
                /* displayName */ req.getAccountId().trim(),
                /* language    */ Optional.ofNullable(req.getLanguage()).orElse("ja"),
                /* emailVerified */ true
        );

        // 4) パスワード保存（PHC）
        String phc = passwordPort.hash(req.getPassword());
        store.setPasswordHash(user.getId(), phc);

        return new RegisterResponse(true, user.getId(), true);
    }

    /**
     * ログイン。
     * 引数:
     *   - req.identifier（メール or アカウントID）, req.password
     * 戻り:
     *   - LoginResponse(success, tokens...)
     * 手順:
     *   1) 識別子からユーザー検索（メール→無ければアカウントID）
     *   2) パスワード検証（PasswordPort.verify）
     *   3) AT/RT発行（TokenFacade.issueAfterAuth）
     *   4) needsRehash ならPHC更新
     *   5) セッション作成（SessionService）
     */
    @Override
    public LoginResponse login(LoginRequest req) {
        if (req == null || isBlank(req.getIdentifier()) || isBlank(req.getPassword())) {
            throw new BadRequestException("invalid-argument");
        }
        final Instant now = Instant.now();

        User user = resolveUserByIdentifier(req.getIdentifier().trim())
                .orElseThrow(() -> new InvalidCredentialsException("invalid-credentials"));

        String phc = store.getPasswordHash(user.getId())
                .orElseThrow(() -> new InvalidCredentialsException("invalid-credentials"));

        if (!passwordPort.verify(req.getPassword(), phc)) {
            throw new InvalidCredentialsException("invalid-credentials");
        }

        // リハッシュ（コスト変更や方式変更時）
        if (passwordPort.needsRehash(phc)) {
            store.setPasswordHash(user.getId(), passwordPort.hash(req.getPassword()));
        }

        // 2要素/生体などのAMRは要件に合わせて拡張。ここではパスワード認証のみ
        List<String> amr = List.of("pwd");

        LoginTokens tokens = tokenFacade.issueAfterAuth(user.getId(), amr);

        // セッションのUA/IPは Controller で抽出する方針（D）。DTOに無ければ空で格納可
        String ua = safe(req.getUserAgent()); // DTOに無い場合は空
        String ip = safe(req.getIp());        // DTOに無い場合は空
        sessionService.createSession(user.getId(),
                tokenFacade.extractRefreshId(tokens.getRefreshToken()),
                ua, ip, now);

        return new LoginResponse(true, tokens);
    }

    // ================
    // トークン更新
    // ================

    /**
     * リフレッシュ。
     * 手順:
     *   1) oldRtId 抽出
     *   2) rotate 実行（新AT/RT）
     *   3) newRtId 抽出
     *   4) Store.rotateRefreshToken 反映
     */
    @Override
    public TokenRefreshResponse refresh(String userId, TokenRefreshRequest req) {
        if (isBlank(userId) || req == null || isBlank(req.getRefreshToken())) {
            throw new BadRequestException("invalid-argument");
        }
        final Instant now = Instant.now();

        String oldRtId = tokenFacade.extractRefreshId(req.getRefreshToken());
        LoginTokens tokens = tokenFacade.rotate(userId, oldRtId);
        String newRtId = tokenFacade.extractRefreshId(tokens.getRefreshToken());

        store.rotateRefreshToken(userId, oldRtId, newRtId, now);

        return new TokenRefreshResponse(true, tokens);
    }

    // ================
    // セッション操作
    // ================

    @Override
    public SessionInfo getCurrentSession(String userId) {
        if (isBlank(userId)) throw new UnauthorizedException("unauthorized");
        return sessionService.getCurrentSession(userId)
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));
    }

    @Override
    public SessionsListResponse listSessions(String userId) {
        if (isBlank(userId)) throw new UnauthorizedException("unauthorized");
        var list = store.listSessions(userId);
        return new SessionsListResponse(list);
    }

    /**
     * ログアウト。req は sessionId または refreshToken のどちらか任意。
     * 優先順位:
     *   1) refreshToken 指定 → RT-JTIで削除
     *   2) sessionId 指定 → そのセッション削除
     * どちらも無い場合は BadRequest。
     */
    @Override
    public LogoutResponse logout(String userId, LogoutRequest req) {
        if (isBlank(userId) || req == null) {
            throw new BadRequestException("invalid-argument");
        }
        boolean changed = false;

        if (!isBlank(req.getRefreshToken())) {
            String rtId = tokenFacade.extractRefreshId(req.getRefreshToken());
            changed = store.deleteSessionByRefreshToken(userId, rtId);
        } else if (!isBlank(req.getSessionId())) {
            changed = store.deleteSession(userId, req.getSessionId());
        } else {
            throw new BadRequestException("invalid-argument");
        }

        // LogoutResponse の可視性が package-private の場合があるため注意
        return LogoutResponse.ok(changed);
    }

    @Override
    public LogoutResponse logoutAll() {
        // 1. 現在ユーザー取得（未認証なら UnauthorizedException）
        String userId = currentUserPort.getCurrentUserId();

        // 2. 該当ユーザーの全セッション終了 + token_version++
        sessionService.logoutAll(userId);

        // 3. API仕様上は常に success=true でよい（対象不在でも冪等とみなす）
        return LogoutResponse.builder()
                .success(true)
                .build();
    }

    // ================
    // 内部ユーティリティ
    // ================

    private Optional<User> resolveUserByIdentifier(String identifier) {
        // まずメールとして検索。見つからなければaccountIdで検索
        Optional<User> byEmail = store.findUserByEmail(identifier);
        if (byEmail.isPresent()) return byEmail;
        return store.findUserByAccountId(identifier);
    }

    private static String extractDomain(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return "";
        return email.substring(at + 1);
    }

    private static String normalizeDomain(String domain) {
        if (isBlank(domain)) return "";
        // 小文字化 + 末尾ドット除去 + IDNA変換
        String d = domain.trim().toLowerCase(Locale.ROOT);
        while (d.endsWith(".")) d = d.substring(0, d.length() - 1);
        try {
            return IDN.toASCII(d);
        } catch (Exception e) {
            return d; // 不正でもそのまま返す（Store側で判定）
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
