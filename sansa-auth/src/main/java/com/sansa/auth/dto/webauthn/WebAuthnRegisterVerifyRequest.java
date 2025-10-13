package com.sansa.auth.dto.webauthn;

import lombok.*;

/**
 * POST /webauthn/register/verify のリクエストDTO
 * 仕様: 03_WebAuthn.md「A) 登録 → 検証入力」clientDataJSON / attestationObject が必須。:contentReference[oaicite:6]{index=6}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnRegisterVerifyRequest {
    /** navigator.credentials.create の clientDataJSON（必須） */           // 03_WebAuthn.md A)検証入力
    private String clientDataJSON;                                         // :contentReference[oaicite:7]{index=7}
    /** attestationObject（必須） */                                        // 同上
    private String attestationObject;                                      // :contentReference[oaicite:8]{index=8}
}
