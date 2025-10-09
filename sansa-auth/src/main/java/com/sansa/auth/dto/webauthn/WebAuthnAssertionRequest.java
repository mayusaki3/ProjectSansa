package com.sansa.auth.dto.webauthn;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class WebAuthnAssertionRequest {
  @NotBlank String id;                 // credentialId
  @NotBlank String clientDataJSON;
  @NotBlank String authenticatorData;
  @NotBlank String signature;
  String userHandle;                   // 任意
}
