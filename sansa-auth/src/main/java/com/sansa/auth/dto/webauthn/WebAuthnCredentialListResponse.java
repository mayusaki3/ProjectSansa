package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

@Value @Builder
public class WebAuthnCredentialListResponse {
  List<WebAuthnCredentialSummary> credentials;
}
