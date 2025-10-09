package com.sansa.auth.dto.webauthn;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class WebAuthnChallengeResponse {
  @NotBlank String challenge;
  @NotBlank String rpId;
  Integer timeout; // ms
  @Pattern(regexp="^(required|preferred|discouraged)$")
  String userVerification; // 既定: preferred
}
