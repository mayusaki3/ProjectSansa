package com.sansa.auth.dto.login;

import com.sansa.auth.dto.sessions.SessionInfo;
import lombok.*;
import java.util.List;

@Value
@Builder
public class LoginResponse {
  boolean authenticated;
  boolean mfaRequired;
  SessionInfo session;
  LoginTokens tokens;
  List<String> amr;
  SessionInfo.UserSummary user;

  MfaInfo mfa;

  @Value @Builder
  public static class MfaInfo {
    java.util.List<String> factors;
    String challengeId;
  }
}
