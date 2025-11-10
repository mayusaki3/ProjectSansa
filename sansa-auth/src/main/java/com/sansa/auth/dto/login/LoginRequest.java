package com.sansa.auth.dto.login;

/**
 * ログインリクエスト。
 * accountId または email のいずれかで認証する想定。
 */
public class LoginRequest {

    private String accountId;
    private String email;
    private String password;
    private String ip;
    private String userAgent;

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

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
