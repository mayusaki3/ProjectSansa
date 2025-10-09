package com.sansa.auth.dto.mfa;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Value @Builder
public class MfaTotpEnrollResponse {
  @NotBlank String secret;
  @NotBlank String uri;    // otpauth://...
}
