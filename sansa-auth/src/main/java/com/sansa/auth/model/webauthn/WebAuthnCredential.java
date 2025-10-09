package com.sansa.auth.model.webauthn;

import lombok.*;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class WebAuthnCredential {
  @Builder.Default UUID id = UUID.randomUUID();      // internal id
  UUID userId;
  String credentialId;  // Base64url
  String publicKey;     // COSE（表現は実装依存）
  String aaguid;
  java.util.List<String> transports;
  Long signCount;
  boolean revoked;
}
