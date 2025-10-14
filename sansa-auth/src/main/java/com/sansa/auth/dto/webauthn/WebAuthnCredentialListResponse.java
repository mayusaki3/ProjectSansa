package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

/**
 * GET /webauthn/credentials のレスポンスDTO 仕様: 03_WebAuthn.md「C) 管理 →
 * WebAuthnCredentialListResponse」 - 配列: credentialId, aaguid?, transports?,
 * signCount? を各要素に含める。:contentReference[oaicite:21]{index=21}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebAuthnCredentialListResponse {

    private List<WebAuthnCredentialSummary> credentials;   // 要素定義は上記 Summary を参照
}
