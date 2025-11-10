package com.sansa.auth.dto.auth;

/**
 * 本登録リクエスト。
 */
public class RegisterRequest {

    private String preRegId;
    private String email;
    private String accountId;
    private String password;

    public RegisterRequest() {
    }

    public String getPreRegId() {
        return preRegId;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPreRegId(String preRegId) {
        this.preRegId = preRegId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
