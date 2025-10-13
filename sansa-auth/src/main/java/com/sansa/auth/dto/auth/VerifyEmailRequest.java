package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * POST /auth/verify-email のリクエストDTO
 * 目的: 受信した検証コードでメールを検証し、preRegId を払い出す前段。
 * 仕様: 01_ユーザー登録.md 「2) POST /auth/verify-email → VerifyEmailRequest」参照。:contentReference[oaicite:5]{index=5}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VerifyEmailRequest {
    /** 検証対象メール */
    @NotBlank @Email
    private String email;

    /** 受信した検証コード（6〜10桁、英数字） */
    @NotBlank
    @Size(min = 6, max = 10)
    @Pattern(regexp = "^[0-9A-Za-z]{6,10}$")
    private String code;
}
