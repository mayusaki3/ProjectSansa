package com.sansa.auth.service.port.impl;

import com.sansa.auth.dto.token.LoginTokens;
import com.sansa.auth.jwt.JwtProviderConfig;
import com.sansa.auth.service.port.SessionService;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.TokenIssuer;
import com.sansa.auth.facade.TokenFacade;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * TokenFacade の正式実装。
 *
 * 主な責務：
 *  - JWT 発行／更新／検証
 *  - セッション生成と紐付け
 *  - リフレッシュトークン処理
 *  - TokenVersion 管理（強制失効用）
 *
 * Store / TokenIssuer / SessionService に依存。
 */
@Component
@Transactional
public class TokenFacadeImpl implements TokenFacade {

    private final Store store;
    private final TokenIssuer tokenIssuer;
    private final SessionService sessionService;
    private final JwtProviderConfig jwtProviderConfig;

    public TokenFacadeImpl(Store store,
                           TokenIssuer tokenIssuer,
                           SessionService sessionService,
                           JwtProviderConfig jwtProviderConfig) {
        this.store = store;
        this.tokenIssuer = tokenIssuer;
        this.sessionService = sessionService;
        this.jwtProviderConfig = jwtProviderConfig;
    }

    // ==========================================================
    // JWT・セッション生成
    // ==========================================================

    /**
     * 新しいセッションIDを生成する。
     *
     * @return UUID形式の新規セッションID
     */
    @Override
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * ログインまたはMFA後にJWTトークンを発行。
     *
     * @param accountId アカウントID
     * @param sessionId セッションID
     * @param ipAddress IPアドレス
     * @param userAgent UA
     * @return アクセストークン／リフレッシュトークン
     */
    @Override
    public Tokens issue(String accountId, String sessionId,
                        String ipAddress, String userAgent) {
        Instant now = Instant.now();
        Instant accessExp = now.plusSeconds(jwtProviderConfig.getAccessTokenTtlSec());
        Instant refreshExp = now.plusSeconds(jwtProviderConfig.getRefreshTokenTtlSec());

        // TokenVersion 取得
        long tokenVersion = store.getTokenVersion(accountId);

        // JWT生成
        String accessToken = tokenIssuer.issueAccessToken(accountId, sessionId, tokenVersion, accessExp);
        String refreshToken = tokenIssuer.issueRefreshToken(accountId, sessionId, tokenVersion, refreshExp);

        // セッション登録
        sessionService.upsertSession(accountId, sessionId, now, refreshExp, ipAddress, userAgent);

        return new Tokens(accessToken, refreshToken);
    }

    /**
     * MFA完了後の再発行。
     *
     * @param accountId アカウントID
     * @param sessionId セッションID
     * @param ipAddress IPアドレス
     * @param userAgent UA
     * @return 新しいトークンペア
     */
    @Override
    public Tokens issueAfterMfa(String accountId, String sessionId,
                                String ipAddress, String userAgent) {
        // MFA成功時は同じ処理フローを再利用
        return issue(accountId, sessionId, ipAddress, userAgent);
    }

    // ==========================================================
    // トークンリフレッシュ
    // ==========================================================

    /**
     * リフレッシュトークンを用いてアクセストークンを再発行する。
     *
     * @param refreshToken クライアントから送信されたトークン
     * @return 新トークンペア
     */
    @Override
    public Tokens refresh(String refreshToken) {
        // トークン解析
        var parsed = tokenIssuer.parseRefreshToken(refreshToken);
        if (!parsed.isValid()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String accountId = parsed.getAccountId();
        String sessionId = parsed.getSessionId();
        long versionInToken = parsed.getTokenVersion();

        // TokenVersionを検証（失効確認）
        long currentVersion = store.getTokenVersion(accountId);
        if (currentVersion != versionInToken) {
            throw new IllegalStateException("Token version mismatch (token invalidated)");
        }

        Instant now = Instant.now();
        Instant accessExp = now.plusSeconds(jwtProviderConfig.getAccessTokenTtlSec());
        Instant refreshExp = now.plusSeconds(jwtProviderConfig.getRefreshTokenTtlSec());

        // 新トークン発行
        String newAccess = tokenIssuer.issueAccessToken(accountId, sessionId, currentVersion, accessExp);
        String newRefresh = tokenIssuer.issueRefreshToken(accountId, sessionId, currentVersion, refreshExp);

        // セッション延長
        sessionService.upsertSession(accountId, sessionId, now, refreshExp, null, null);

        return new Tokens(newAccess, newRefresh);
    }

    // ==========================================================
    // トークン検証
    // ==========================================================

    /**
     * アクセストークンを検証し、ユーザー情報を返す。
     *
     * @param accessToken クライアントトークン
     * @return アカウントID
     */
    @Override
    public String verifyAccessToken(String accessToken) {
        var parsed = tokenIssuer.parseAccessToken(accessToken);
        if (!parsed.isValid()) {
            throw new IllegalArgumentException("Invalid access token");
        }

        long currentVersion = store.getTokenVersion(parsed.getAccountId());
        if (currentVersion != parsed.getTokenVersion()) {
            throw new IllegalStateException("Access token invalidated");
        }

        return parsed.getAccountId();
    }

    // ==========================================================
    // Logout / Token Version Invalidation
    // ==========================================================

    /**
     * トークンを完全無効化する（全セッション失効）。
     *
     * @param accountId 対象アカウント
     */
    @Override
    public void invalidateAll(String accountId) {
        store.incrementTokenVersion(accountId);
        store.deleteAllSessions(accountId);
    }

    // ==========================================================
    // DTO変換
    // ==========================================================

    /**
     * Tokens → LoginTokens DTO に変換。
     */
    @Override
    public LoginTokens toLoginTokens(Tokens t) {
        return LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
    }
}
