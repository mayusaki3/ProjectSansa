package com.sansa.auth.dto.sessions;

import lombok.*;
import java.util.List;

@Value
@Builder
public class SessionInfo {
  boolean active;
  String sessionId;
  String issuedAt;     // ISO-8601
  String lastActive;   // ISO-8601
  String expiresAt;    // ISO-8601
  List<String> amr;
  UserSummary user;

  @Value @Builder
  public static class UserSummary {
    String userId;
    String email;
    String displayName;
  }
}
