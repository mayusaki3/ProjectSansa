package com.sansa.auth.util;

import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.store.InmemStore;

import java.time.format.DateTimeFormatter;

public final class Timestamps {
  private static final DateTimeFormatter F = DateTimeFormatter.ISO_INSTANT;

  public static SessionInfo toSessionInfo(InmemStore.Session s, InmemStore.User u) {
    return SessionInfo.builder()
        .active(s.expiresAt().isAfter(java.time.Instant.now()))
        .sessionId(s.sessionId())
        .issuedAt(F.format(s.issuedAt()))
        .lastActive(F.format(s.lastActive()))
        .expiresAt(F.format(s.expiresAt()))
        .amr(s.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(u.userId())
            .email(u.email())
            .displayName(u.displayName())
            .build())
        .build();
  }
  private Timestamps() {}
}
