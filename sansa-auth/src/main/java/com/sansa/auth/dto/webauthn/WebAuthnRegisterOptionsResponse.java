package com.sansa.auth.dto.webauthn;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Value
@Builder
public class WebAuthnRegisterOptionsResponse {
  @NotBlank String challenge;
  @NotBlank String rpId;
  @NotBlank String user; // encoded userId
  List<PubKeyCredParam> pubKeyCredParams;
  @Builder.Default String attestation = "none";

  @Value @Builder
  public static class PubKeyCredParam {
    String type;  // "public-key"
    Integer alg;  // -7(ES256)...
  }
}
