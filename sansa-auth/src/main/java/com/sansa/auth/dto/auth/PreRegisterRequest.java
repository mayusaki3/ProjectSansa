package com.sansa.auth.dto.auth;

/**
 * POST /auth/pre-register 用リクエストDTO.
 *
 * email:
 *   - 必須
 *   - ユーザーのメールアドレス
 *
 * language:
 *   - 任意
 *   - 通知やメッセージに使用する言語/ロケール識別子
 *   - 例: "ja", "ja-JP", "en", "en-US"
 */
public class PreRegisterRequest {

    /** ユーザーのメールアドレス */
    private String email;

    /** 言語/ロケール識別子 (任意) */
    private String language;

    public PreRegisterRequest() {
    }

    public PreRegisterRequest(String email, String language) {
        this.email = email;
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
