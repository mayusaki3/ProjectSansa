package com.sansa.auth.dto.login;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class TokenRefreshResponse {
  @NonNull LoginTokens tokens; // accessToken / refreshToken
  int tv;                      // 現在の token_version
}
