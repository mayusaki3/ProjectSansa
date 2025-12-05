package com.sansa.auth.service.impl;

import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.service.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * セッション管理サービスの暫定 in-memory 実装。
 *
 * 現時点では「アプリ起動を通す」ことを目的としたダミー実装であり、
 * セッション情報は保持せず、すべて no-op / 空レスポンスを返す。
 *
 * 本番の仕様に合わせて、後続作業で実装を差し替える前提。
 */
@Service
public class InMemorySessionService implements SessionService {

    /**
     * 指定ユーザーの現在のセッションを取得。
     * 暫定実装では常に空を返す。
     *
     * @param userId 対象ユーザーID
     * @return セッション情報（現状は常に Optional.empty()）
     */
    @Override
    public Optional<SessionInfo> getCurrentSession(String userId) {
        // TODO: 本実装では userId に紐づく現在のセッションを返す
        return Optional.empty();
    }

    /**
     * 指定ユーザーの全アクティブセッションを取得。
     * 暫定実装では常に空リストを返す。
     *
     * @param userId 対象ユーザーID
     * @return セッション情報のリスト（現状は常に空リスト）
     */
    @Override
    public List<SessionInfo> listSessions(String userId) {
        // TODO: 本実装では userId に紐づく全セッション一覧を返す
        return List.of();
    }

    /**
     * 指定ユーザーの特定セッションを削除（ログアウト扱い）。
     * 暫定実装では実処理を行わず、null を返す。
     *
     * @param userId    対象ユーザーID
     * @param sessionId 削除対象のセッションID
     * @return ログアウト結果レスポンス（現状は null）
     */
    @Override
    public LogoutResponse deleteById(String userId, String sessionId) {
        // TODO: 本実装ではストアから該当セッションを削除し、結果を組み立てて返す
        return null;
    }

    /**
     * 指定ユーザーの現在のセッションからログアウト。
     * 暫定実装では何もしない。
     *
     * @param userId 対象ユーザーID
     */
    @Override
    public void logoutCurrentSession(String userId) {
        // TODO: 本実装では「現在のセッション」を特定して削除する
    }

    /**
     * 指定ユーザーの特定セッションからログアウト。
     * 暫定実装では何もしない。
     *
     * @param userId    対象ユーザーID
     * @param sessionId ログアウト対象のセッションID
     */
    @Override
    public void logoutBySessionId(String userId, String sessionId) {
        // TODO: 本実装では sessionId を元に該当セッションを削除する
    }
}
