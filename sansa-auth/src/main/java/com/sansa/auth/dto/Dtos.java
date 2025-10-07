package com.sansa.auth.dto;

import java.util.HashMap;
import java.util.Map;

public final class Dtos {
    private Dtos() {}

    // ---------- 共通レスポンス ----------
    public static class AuthResult {
        private boolean success;
        private String message;
        private Map<String, Object> details = new HashMap<>();

        public AuthResult() {
        }

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResult(boolean success, String message, Map<String, Object> details) {
            this.success = success;
            this.message = message;
            if (details != null) this.details = details;
        }

        // 便利メソッド
        public static AuthResult ok(String message) {
            return new AuthResult(true, message);
        }

        public static AuthResult ok(String message, Map<String, Object> details) {
            return new AuthResult(true, message, details);
        }

        public static AuthResult error(String message) {
            return new AuthResult(false, message);
        }

        // getters / setters
        public boolean isSuccess() {
            return success;
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public Map<String, Object> getDetails() {
            return details;
        }
        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }

    // ---------- トークン ----------
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;

        public TokenPair() {
        }

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        public String getRefreshToken() {
            return refreshToken;
        }
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    // ---------- 事前登録(プリレジ) ----------
    public static class PreRegisterRequest {
        private String email;
        private String language; // 例: "ja", "en"

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

    public static class VerifyEmailRequest {
        private String preRegId; // 事前登録ID
        private String code;     // メールの確認コード

        public VerifyEmailRequest() {
        }

        public VerifyEmailRequest(String preRegId, String code) {
            this.preRegId = preRegId;
            this.code = code;
        }

        public String getPreRegId() {
            return preRegId;
        }
        public void setPreRegId(String preRegId) {
            this.preRegId = preRegId;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class RegisterRequest {
        private String preRegId;
        private String accountId;
        private String language;

        public RegisterRequest() {
        }

        public RegisterRequest(String preRegId, String accountId, String language) {
            this.preRegId = preRegId;
            this.accountId = accountId;
            this.language = language;
        }

        public String getPreRegId() {
            return preRegId;
        }
        public void setPreRegId(String preRegId) {
            this.preRegId = preRegId;
        }
        public String getAccountId() {
            return accountId;
        }
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
        public String getLanguage() {
            return language;
        }
        public void setLanguage(String language) {
            this.language = language;
        }
    }

    // ---------- ログイン ----------
    public static class LoginRequest {
        // ユースケースに応じて利用（パスワード/Passkey/WebAuthnなど）
        private String accountId; // or email
        private String password;  // パスワードログイン時
        private String assertion; // WebAuthn等で使う場合のプレースホルダ

        public LoginRequest() {
        }

        public LoginRequest(String accountId, String password, String assertion) {
            this.accountId = accountId;
            this.password = password;
            this.assertion = assertion;
        }

        public String getAccountId() {
            return accountId;
        }
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getAssertion() {
            return assertion;
        }
        public void setAssertion(String assertion) {
            this.assertion = assertion;
        }
    }

    public static class LoginResponse {
        private String message;
        private boolean success;
        private String userId;
        private TokenPair tokens;

        public LoginResponse() {
        }

        public LoginResponse(String message, boolean success, String userId, TokenPair tokens) {
            this.message = message;
            this.success = success;
            this.userId = userId;
            this.tokens = tokens;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public boolean isSuccess() {
            return success;
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }
        public TokenPair getTokens() {
            return tokens;
        }
        public void setTokens(TokenPair tokens) {
            this.tokens = tokens;
        }
    }

    // ---------- MFA (メールOTP / TOTP) ----------
    public static class EmailOtpSendRequest {
        private String email;
        private String language;

        public EmailOtpSendRequest() {
        }

        public EmailOtpSendRequest(String email, String language) {
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

    public static class EmailOtpVerifyRequest {
        private String email;
        private String code;

        public EmailOtpVerifyRequest() {
        }

        public EmailOtpVerifyRequest(String email, String code) {
            this.email = email;
            this.code = code;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class TotpVerifyRequest {
        private String code;

        public TotpVerifyRequest() {
        }

        public TotpVerifyRequest(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }
}
