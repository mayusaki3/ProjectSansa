package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class VerifyEmailResponse {
  @NotBlank String preRegId;  // UUID String
  @NotNull Integer expiresIn; // seconds
}
