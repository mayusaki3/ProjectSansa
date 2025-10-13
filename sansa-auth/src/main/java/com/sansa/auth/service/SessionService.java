package com.sansa.auth.service;

import com.sansa.auth.dto.sessions.*;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;

/**
 * セッション列挙/個別失効（/sessions 配下）を扱うサービス。
 * 参照仕様: 05_セッション管理.md（/sessions, /sessions/{id}） :contentReference[oaicite:10]{index=10}
 */
public interface SessionService {

    /** セッション一覧の取得（自アカウント）。GET /sessions
     *  仕様: 05_セッション管理.md 「#2 GET /sessions → SessionsListResponse」 :contentReference[oaicite:11]{index=11}
     *  成功: 200 + SessionsListResponse
     */
    SessionsListResponse list()
            throws UnauthorizedException;

    /** セッション個別失効。DELETE /sessions/{sessionId}
     *  仕様: 05_セッション管理.md 「#3 DELETE /sessions/{sessionId}」204/404（本文なし） :contentReference[oaicite:12]{index=12}
     *  成功: 204（本文なし）
     */
    void deleteById(String sessionId)
            throws UnauthorizedException, NotFoundException;
}
