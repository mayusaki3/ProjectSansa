package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * POST /auth/register のリクエストDTO
 * 目的: verify-email で得た preRegId を用いてユーザーを本登録する。
 * 仕様: 01_ユーザー登録.md 「3) POST /auth/register → RegisterRequest」参照。:contentReference[oaicite:9]{index=9}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegisterRequest {

    /** verify-email で払い出された preRegId（TTL=10分、1回限り） */
    @NotBlank
    private String preRegId;

    /** アカウントID（3〜64, 英数と . _ - を許可） */
    @NotBlank
    @Size(min = 3, max = 64)
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,64}$")
    private String accountId;

    /** 任意のパスワード（パスキー主体なら空可）。使う場合は 8〜128 推奨。 */
    @Size(min = 8, max = 128)
    private String password;

    /** 任意の言語コード（例: ja, ja-JP, en-US） */
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$")
    private String language;
}
