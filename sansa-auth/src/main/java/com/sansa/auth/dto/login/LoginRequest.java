package com.sansa.auth.dto.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * POST /auth/login のリクエストDTO
 * 仕様: 02_ログイン.md 「A) POST /auth/login → LoginRequest(identifier, password)」参照。:contentReference[oaicite:7]{index=7}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {

    /** ログイン識別子（email または accountId）。必須。 */
    @NotBlank
    private String identifier;

    /** パスワード。必須。パース負荷対策として上限を設ける。 */
    @NotBlank
    @Size(max = 1024)
    private String password;
}
