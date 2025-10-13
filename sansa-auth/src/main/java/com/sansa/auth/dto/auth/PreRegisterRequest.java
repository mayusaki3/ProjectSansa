package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /auth/pre-register のリクエストDTO
 * 目的: ユーザーのメールアドレス（と任意言語）を受け取り、検証コード送信をトリガーする。
 * 仕様: 01_ユーザー登録.md 「1) POST /auth/pre-register」参照。:contentReference[oaicite:0]{index=0}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PreRegisterRequest {

    /** 送信先メールアドレス（正規化はサーバ側） */
    @NotBlank @Email
    private String email;

    /** UI/通知の言語（任意）。例: ja, ja-JP, en-US */
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$")
    private String language;
}
