package com.sansa.auth.dto.login;

import lombok.*;

@Value
@Builder
public class LoginTokens {
  String accessToken;
  String refreshToken;
}
