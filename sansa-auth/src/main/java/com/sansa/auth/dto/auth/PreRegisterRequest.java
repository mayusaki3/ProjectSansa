package com.sansa.auth.dto.auth;

/**
 * メールアドレス事前登録リクエスト。
 */
public class PreRegisterRequest {

    private String email;
    private String locale; // 任意: メール文面ロケールなど

    public PreRegisterRequest() {
    }

    public PreRegisterRequest(String email, String locale) {
        this.email = email;
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public String getLocale() {
        return locale;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
