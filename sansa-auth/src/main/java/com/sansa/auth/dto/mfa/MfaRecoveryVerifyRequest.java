package com.sansa.auth.dto.mfa;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class MfaRecoveryVerifyRequest {
  @NotBlank String challengeId;
  @NotBlank String code;
}
