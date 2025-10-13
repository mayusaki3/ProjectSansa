package com.sansa.auth.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * POST /auth/token/refresh のリクエストDTO
 * 仕様: 02_ログイン.md 「R1 TokenRefreshRequest」参照。:contentReference[oaicite:14]{index=14}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenRefreshRequest {
    /** 既存のリフレッシュトークン（この呼び出しで新RTに置換） */
    @NotBlank
    private String refreshToken;
}
