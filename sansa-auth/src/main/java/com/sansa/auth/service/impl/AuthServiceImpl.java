package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.token.LoginTokens;
import com.sansa.auth.dto.token.LogoutResponse;
import com.sansa.auth.dto.token.TokenRefreshResponse;
import com.sansa.auth.facade.TokenFacade;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.port.SessionService;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * 認証・登録・セッション管理の中核サービス。
 *
 * 主な責務：
 *  - 新規登録・事前登録確認
 *  - メール検証（verifyEmail）
 *  - ログイン（パスワード＋MFA統合）
 *  - トークンリフレッシュ / ログアウト
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final Store store;
    private final TokenFacade tokenFacade;
    private final SessionService sessionService;
    private final PasswordHasher passwordHasher;

    public AuthServiceImpl(Store store, TokenFacade tokenFacade,
                           SessionService sessionService, PasswordHasher passwordHasher) {
        this.store = store;
        this.tokenFacade = tokenFacade;
        this.sessionService = sessionService;
        this.passwordHasher = passwordHasher;
    }

    // ==========================================================
    // ユーザー登録・メール検証
    // ==========================================================

    /**
     * 事前登録コードを検証し、本登録を完了する。
     *
     * @param req 検証リクエスト
     */
    @Override
    public void verifyEmail(VerifyEmailRequest req) {
        var pre = store.consumePreRegCode(req.getPreRegId(), req.getCode());
        if (pre == null) {
            throw new IllegalArgumentException("Invalid or expired pre-registration code");
        }
        // 本登録ユーザーを作成
        String passwordHash = passwordHasher.hash(req.getPassword());
        store.createUser(pre.getAccountId(), pre.getEmail(), passwordHash, false);
    }

    /**
     * 新規ユーザー登録（メール事前登録コードを発行）。
     *
     * @param req 登録リクエスト
     * @return preRegId（検証時に使用）
     */
    @Override
    public String register(RegisterRequest req) {
        return store.issuePreRegCode(req.getEmail(), req.getLanguage());
    }

    // ==========================================================
    // ログイン・ログアウト / トークン管理
    // ==========================================================

    /**
     * ログイン処理。
     * パスワード認証を実行し、成功時にトークンを発行。
     *
     * @param accountId アカウントID
     * @param password パスワード平文
     * @param ip IPアドレス
     * @param userAgent UA文字列
     * @return LoginTokens
     */
    @Override
    public LoginTokens login(String accountId, String password, String ip, String userAgent) {
        var user = store.findUserByAccountId(accountId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!passwordHasher.verify(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // セッション生成
        Instant now = Instant.now();
        var session = sessionService.upsertSession(accountId,
                tokenFacade.generateSessionId(),
                now,
                now.plusSeconds(86400),
                ip,
                userAgent);

        // トークン発行
        var t = tokenFacade.issue(user.getAccountId(), session.getSessionId(), ip, userAgent);
        return LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
    }

    /**
     * リフレッシュトークンによる再発行。
     *
     * @param refreshToken リフレッシュトークン
     * @return 新しいトークンペア
     */
    @Override
    public TokenRefreshResponse refresh(String refreshToken) {
        var r = tokenFacade.refresh(refreshToken);
        return new TokenRefreshResponse(r.accessToken(), r.refreshToken());
    }

    /**
     * ログアウト。
     * セッション削除＋トークンバージョン更新。
     *
     * @param sessionId セッションID
     * @param accountId アカウントID
     * @return ログアウト結果
     */
    @Override
    public LogoutResponse logout(String sessionId, String accountId) {
        store.deleteSessionById(sessionId);
        store.incrementTokenVersion(accountId);
        return new LogoutResponse(true);
    }

    // ==========================================================
    // MFA対応 / TOTP / WebAuthn等（後段拡張用）
    // ==========================================================

    /**
     * 現時点ではMFA対応はMfaService側で実施。
     * AuthService側ではフックのみ定義。
     */
    @Override
    public void onMfaCompleted(String accountId) {
        store.markTotpEnabled(accountId);
    }
}
