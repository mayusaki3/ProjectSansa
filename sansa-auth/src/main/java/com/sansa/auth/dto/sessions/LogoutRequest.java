package com.sansa.auth.dto.sessions;

import lombok.*;

@Value @Builder
public class LogoutRequest {
  String refreshToken;
  String sessionId;
}
