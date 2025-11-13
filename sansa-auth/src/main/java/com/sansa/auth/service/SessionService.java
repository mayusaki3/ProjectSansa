package com.sansa.auth.service;

import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import java.util.List;
import java.util.Optional;

/**
 * セッション管理サービス。
 * - ユーザーの全セッション一覧取得
 * - セッション削除（ログアウト）
 */
public interface SessionService {

    /**
     * 指定ユーザーの現在のセッションを取得。
     * 
     * @param userId 対象ユーザーID
     * @return セッション情報
     */
    Optional<SessionInfo> getCurrentSession(String userId);

    /**
     * 指定ユーザーの全アクティブセッションを取得。
     * 
     * @param userId 対象ユーザーID
     * @return セッション情報のリスト
     */
    List<SessionInfo> listSessions(String userId);

    /**
     * 指定ユーザーの特定セッションを削除（ログアウト扱い）。
     * 
     * @param userId 対象ユーザーID
     * @param sessionId 削除対象のセッションID
     * @return ログアウト結果レスポンス
     */
    LogoutResponse deleteById(String userId, String sessionId);

    /**
     * 指定ユーザーの現在のセッションからログアウト。
     * 
     * @param userId 対象ユーザーID
     */
    void logoutCurrentSession(String userId);

    /**
     * 指定ユーザーの特定セッションからログアウト。
     * 
     * @param userId 対象ユーザーID
     * @param sessionId ログアウト対象のセッションID
     */
    void logoutBySessionId(String userId, String sessionId);
}
