package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

/**
 * GET /webauthn/credentials → 配列要素
 * 仕様: 03_WebAuthn.md「C) 管理 → WebAuthnCredentialListResponse の要素」
 * - credentialId, aaguid?, transports?, signCount?（任意）に対応。:contentReference[oaicite:18]{index=18}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnCredentialSummary {
    private String credentialId;
    private String aaguid;                   // 任意
    private List<String> transports;         // 任意
    private Integer signCount;               // 任意（仕様の ? 項目として追加）  :contentReference[oaicite:19]{index=19}
}
