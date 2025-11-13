package com.sansa.auth.service.impl;

import com.sansa.auth.dto.session.SessionInfo;
import com.sansa.auth.dto.token.LogoutResponse;
import com.sansa.auth.dto.token.TokenRefreshResponse;
import com.sansa.auth.facade.TokenFacade;
import com.sansa.auth.store.Store;
import com.sansa.auth.service.SessionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * セッション管理サービスの実装。
 *
 * 主な責務：
 *  - 現在のユーザーセッション一覧取得
 *  - セッション削除（単体 / 全削除）
 *  - トークンリフレッシュ
 *  - セッション作成 / 更新
 *
 * AuthService, MfaService, WebAuthnService などから呼び出される。
 */
@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final Store store;
    private final TokenFacade tokenFacade;

    public SessionServiceImpl(Store store, TokenFacade tokenFacade) {
        this.store = store;
        this.tokenFacade = tokenFacade;
    }

    // ==========================================================
    // セッション一覧 / 詳細取得
    // ==========================================================

    /**
     * 指定ユーザーのすべてのセッションを取得。
     *
     * @param accountId 対象アカウントID
     * @return セッション情報一覧
     */
    @Override
    public List<SessionInfo> listSessions(String accountId) {
        return store.listSessionsByAccountId(accountId).stream()
                .map(s -> new SessionInfo(
                        s.getSessionId(),
                        s.getAccountId(),
                        s.getCreatedAt(),
                        s.getExpiresAt(),
                        s.getIpAddress(),
                        s.getUserAgent()))
                .collect(Collectors.toList());
    }

    /**
     * 特定のセッション詳細を取得。
     *
     * @param sessionId セッションID
     * @return 該当セッション情報、存在しない場合はnull
     */
    @Override
    public SessionInfo findSession(String sessionId) {
        var s = store.findSessionById(sessionId);
        if (s == null) return null;
        return new SessionInfo(
                s.getSessionId(),
                s.getAccountId(),
                s.getCreatedAt(),
                s.getExpiresAt(),
                s.getIpAddress(),
                s.getUserAgent());
    }

    // ==========================================================
    // セッション作成・更新
    // ==========================================================

    /**
     * 新しいセッションを作成または更新。
     *
     * @param accountId アカウントID
     * @param sessionId セッションID
     * @param createdAt 作成時刻
     * @param expiresAt 有効期限
     * @param ipAddress クライアントIP
     * @param userAgent UA文字列
     * @return 作成または更新後のSessionInfo
     */
    @Override
    public SessionInfo upsertSession(String accountId, String sessionId,
                                     Instant createdAt, Instant expiresAt,
                                     String ipAddress, String userAgent) {
        var s = store.createSession(accountId, sessionId, createdAt, expiresAt, ipAddress, userAgent);
        return new SessionInfo(
                s.getSessionId(),
                s.getAccountId(),
                s.getCreatedAt(),
                s.getExpiresAt(),
                s.getIpAddress(),
                s.getUserAgent());
    }

    // ==========================================================
    // セッション削除（単体 / 全削除）
    // ==========================================================

    /**
     * 特定セッションを削除。
     *
     * @param sessionId セッションID
     * @return 成功時true
     */
    @Override
    public boolean deleteSession(String sessionId) {
        return store.deleteSessionById(sessionId);
    }

    /**
     * 指定アカウントの全セッションを削除。
     *
     * @param accountId 対象アカウントID
     * @return 削除件数
     */
    @Override
    public int deleteAllSessions(String accountId) {
        return store.deleteAllSessions(accountId);
    }

    // ==========================================================
    // トークンリフレッシュ / ログアウト
    // ==========================================================

    /**
     * リフレッシュトークンを使用してアクセストークンを再発行する。
     *
     * @param refreshToken リフレッシュトークン
     * @return 新しいアクセストークン
     */
    @Override
    public TokenRefreshResponse refresh(String refreshToken) {
        var result = tokenFacade.refresh(refreshToken);
        return new TokenRefreshResponse(result.accessToken(), result.refreshToken());
    }

    /**
     * ログアウト処理。
     * 該当セッションを削除し、トークンバージョンをインクリメント。
     *
     * @param sessionId セッションID
     * @param accountId アカウントID
     * @return 成功レスポンス
     */
    @Override
    public LogoutResponse logout(String sessionId, String accountId) {
        store.deleteSessionById(sessionId);
        store.incrementTokenVersion(accountId);
        return new LogoutResponse(true);
    }
}
