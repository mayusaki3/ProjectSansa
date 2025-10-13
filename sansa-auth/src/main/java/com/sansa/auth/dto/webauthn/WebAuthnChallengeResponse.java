package com.sansa.auth.dto.webauthn;

import lombok.*;

/**
 * GET /webauthn/challenge のレスポンスDTO（PublicKeyCredentialRequestOptions 相当）
 * 仕様: 03_WebAuthn.md「B) 認証 → WebAuthnChallengeResponse」
 * - challenge, rpId, timeout(ms), userVerification="preferred"（既定） :contentReference[oaicite:13]{index=13}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnChallengeResponse {
    private String challenge;  // Base64url
    private String rpId;
    /** タイムアウト（ms） */
    private Long timeout;
    /** "required" | "preferred"(推奨) | "discouraged" */
    @Builder.Default private String userVerification = "preferred";        // :contentReference[oaicite:14]{index=14}
}
