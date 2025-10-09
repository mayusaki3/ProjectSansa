package com.sansa.auth.dto.login;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class TokenRefreshRequest {
  @NotBlank String refreshToken;
}
