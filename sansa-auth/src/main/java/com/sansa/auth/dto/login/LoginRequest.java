package com.sansa.auth.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ログインリクエスト。
 *
 * 仕様:
 * - accountId または email のどちらかが必須
 * - password は必須
 *
 * 注意:
 * - ip / userAgent は入力として受け取らない（ログ用途は HttpServletRequest から取得する）
 */
@AccountIdOrEmailRequired
public class LoginRequest {

    @Size(max = 64)
    private String accountId;

    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(max = 128)
    private String password;

    public LoginRequest() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
