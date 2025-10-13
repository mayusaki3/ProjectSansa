package com.sansa.auth.dto.webauthn;

import lombok.*;
import java.util.List;

/**
 * GET /webauthn/register/options のレスポンスDTO
 * 仕様: 03_WebAuthn.md「A) 登録 → WebAuthnRegisterOptionsResponse」
 * - fields: challenge, rpId, user, pubKeyCredParams, attestation（既定 "none"） :contentReference[oaicite:1]{index=1}
 * 備考:
 * - WebAuthn 標準に合わせ user は {id,name,displayName} オブジェクトで返す（表の "user: string" は簡略記述と解釈）。:contentReference[oaicite:2]{index=2}
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnRegisterOptionsResponse {
    /** Base64url 等で表現したチャレンジ （challenge）*/                  // 03_WebAuthn.md A)表
    private String challenge;                                              // :contentReference[oaicite:3]{index=3}
    /** Relying Party ID（rpId）*/                                         // 同上
    private String rpId;                                                   // :contentReference[oaicite:4]{index=4}
    /** 登録対象ユーザー（WebAuthn 標準の user オブジェクト） */
    private User user;
    /** サポートする公開鍵アルゴリズム一覧（{type:"public-key", alg:-7} 等）*/
    private List<PubKeyCredParam> pubKeyCredParams;                        // :contentReference[oaicite:5]{index=5}
    /** attestation 方針（既定 "none"）*/                                  // 同上
    @Builder.Default private String attestation = "none";

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class User {
        /** RP user.id（内部ユーザーIDのエンコード） */                     // 03_WebAuthn.md A)表の user を詳細化
        private String id;
        /** アカウント名（任意） */
        private String name;
        /** 表示名（任意） */
        private String displayName;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PubKeyCredParam {
        /** 常に "public-key" */
        @Builder.Default private String type = "public-key";
        /** 例: -7(ES256), -257(RS256) */                                  // 03_WebAuthn.md A)表
        private int alg;
    }
}
