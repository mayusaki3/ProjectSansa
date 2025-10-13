package com.sansa.auth.dto.webauthn;

import lombok.*;

/**
 * POST /webauthn/assertion のリクエストDTO
 * 仕様: 03_WebAuthn.md「B) 認証 → 入力」id, clientDataJSON, authenticatorData, signature, userHandle? :contentReference[oaicite:15]{index=15}
 * 成功時は LoginResponse（authenticated=true, amr+=["webauthn"]）を返す。:contentReference[oaicite:16]{index=16}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnAssertionRequest {
    /** credentialId（id） */                                              // 03_WebAuthn.md B)入力
    private String id;                                                     // :contentReference[oaicite:17]{index=17}
    /** navigator.credentials.get の clientDataJSON */
    private String clientDataJSON;                                         // 同上
    /** authenticatorData */
    private String authenticatorData;                                      // 同上
    /** 署名（signature） */
    private String signature;                                              // 同上
    /** 任意: userHandle */
    private String userHandle;                                             // 同上
}
