package com.sansa.auth.service;

import com.sansa.auth.dto.sessions.SessionInfo;
import java.util.List;
import java.util.Optional;

/**
 * 既存実装から呼び出されるメソッド群を集約。
 */
public interface SessionService {
    Optional<SessionInfo> getCurrentSession(String accountId);
    void logoutCurrentSession(String accountId);
    void logoutBySessionId(String accountId, String sessionId);
    List<SessionInfo> listSessions(String accountId);
}
