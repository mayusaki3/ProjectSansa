package com.sansa.auth.dto.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * アクセストークン / リフレッシュトークンの束（共通DTO）
 *
 * 利用箇所:
 * - POST /auth/login の結果: LoginResponse.tokens として返却（「tokens は LoginTokens」）:contentReference[oaicite:0]{index=0}
 * - POST /auth/token/refresh の結果: TokenRefreshResponse.tokens として返却（accessToken/refreshToken を含む）:contentReference[oaicite:1]{index=1}
 *
 * 運用上の注意（仕様要旨）:
 * - accessToken は短寿命、refreshToken は長寿命。:contentReference[oaicite:2]{index=2}
 * - RT はローテーション必須。旧RTの再利用は検知してブロックし、検知時は tv++ により全端末を失効。:contentReference[oaicite:3]{index=3}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginTokens {

    /** 新規アクセストークン（JWT 等）。/auth/login および /auth/token/refresh で返す必須フィールド。:contentReference[oaicite:4]{index=4} */
    @NotBlank
    private String accessToken;

    /** 新規リフレッシュトークン。/auth/login および /auth/token/refresh で返す必須フィールド。:contentReference[oaicite:5]{index=5} */
    @NotBlank
    private String refreshToken;
}
