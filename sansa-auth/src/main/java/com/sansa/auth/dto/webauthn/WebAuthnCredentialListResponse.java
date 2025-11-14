package com.sansa.auth.dto.webauthn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * WebAuthn 登録クレデンシャル一覧
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebAuthnCredentialListResponse {

    private List<Credential> credentials;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Credential {
        /** Base64url などの識別子 */
        private String credentialId;
        /** 表示名 */
        private String label;
        /** 作成日時/最終使用日時（必要に応じて） */
        private Instant createdAt;
        private Instant lastUsedAt;
    }
}
