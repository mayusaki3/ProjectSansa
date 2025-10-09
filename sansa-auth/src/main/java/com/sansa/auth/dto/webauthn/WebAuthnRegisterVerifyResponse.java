package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

@Value @Builder
public class WebAuthnRegisterVerifyResponse {
  String credentialId;   // Base64url
  String publicKey;      // COSE(実装依存)
  String aaguid;         // 任意
  List<String> transports;
  Long signCount;
}
