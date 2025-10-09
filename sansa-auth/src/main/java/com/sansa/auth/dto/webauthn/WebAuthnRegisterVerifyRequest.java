package com.sansa.auth.dto.webauthn;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class WebAuthnRegisterVerifyRequest {
  @NotBlank String clientDataJSON;
  @NotBlank String attestationObject;
}
