package com.sansa.auth.service.impl;

import com.sansa.auth.dto.sessions.*;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.store.Store;
import com.sansa.auth.store.Store.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * /sessions 配下のユースケース実装
 * 対応仕様: 05_セッション管理.md
 * - GET /sessions → SessionsListResponse
 * - DELETE /sessions/{id} → 204/404（void 戻り）
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final Store store;

    @Override
    public SessionsListResponse list() throws UnauthorizedException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        List<Session> list = store.listSessions(userId);
        return SessionsListResponse.builder()
                .sessions(list.stream().map(SessionServiceImpl::toSessionInfo).toList())
                .build();
    }

    @Override
    public void deleteById(String sessionId) throws UnauthorizedException, NotFoundException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        // 仕様は 204/404（本文なし）。存在しない場合は NotFoundException を投げる実装方針に。
        boolean exists = store.findSessionById(sessionId).isPresent();
        if (!exists) throw new NotFoundException("session not found");
        store.deleteSession(userId, sessionId);
    }

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
                        .email(null).displayName(null).build())
                .build();
    }

    public static final class CurrentRequestContext {
        public static String getUserIdOrThrow() { throw new UnsupportedOperationException(); }
    }
}
