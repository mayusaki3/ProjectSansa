package com.sansa.auth.service;

import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.service.store.InmemStore;
import com.sansa.auth.service.util.Timestamps;
import com.sansa.auth.service.error.AuthExceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final InmemStore store = InmemStore.get();

  // 実運用では SecurityContext や Token から解決
  public SessionInfo currentSession() {
    var ctx = store.debugCurrentContext(); // 開発用スタブ
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var session = store.getSession(ctx.sessionId());
    var user = store.getUser(ctx.userId());
    if (session == null || user == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    return Timestamps.toSessionInfo(session, user);
  }

  public void logout(LogoutRequest req) {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");

    if (req == null || (req.getSessionId() == null && req.getRefreshToken() == null)) {
      // 現セッションのみ
      store.revokeSession(ctx.sessionId());
      return;
    }
    if (req.getSessionId() != null) {
      store.revokeSession(req.getSessionId());
    }
    if (req.getRefreshToken() != null) {
      store.revokeRefreshToken(req.getRefreshToken());
    }
  }

  public void logoutAll() {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    store.bumpTokenVersion(ctx.userId()); // tv++
    store.revokeAllSessions(ctx.userId());
  }
}
