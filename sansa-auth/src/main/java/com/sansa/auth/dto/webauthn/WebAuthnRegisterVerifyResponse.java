package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

/**
 * POST /webauthn/register/verify のレスポンスDTO
 * 仕様: 03_WebAuthn.md「A) 登録 → WebAuthnRegisterVerifyResponse」
 * - fields: credentialId, publicKey, aaguid?, transports?, signCount? :contentReference[oaicite:9]{index=9}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnRegisterVerifyResponse {
    /** Base64url の credentialId */                                       // 03_WebAuthn.md A)表
    private String credentialId;                                           // :contentReference[oaicite:10]{index=10}
    /** COSE Key 等の公開鍵表現（実装依存） */                              // 同上
    private String publicKey;                                              // :contentReference[oaicite:11]{index=11}
    /** 認証器の AAGUID（任意） */
    private String aaguid;                                                 // :contentReference[oaicite:12]{index=12}
    /** 例: "usb","nfc","ble","internal"（任意） */
    private List<String> transports;                                       // 同上
    /** 認証器カウンタ（任意, 0以上） */
    private Integer signCount;                                             // 同上
}
