package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

@Value @Builder
public class WebAuthnCredentialSummary {
  String credentialId;
  String aaguid;
  List<String> transports;
  Long signCount;
  boolean revoked;
}
