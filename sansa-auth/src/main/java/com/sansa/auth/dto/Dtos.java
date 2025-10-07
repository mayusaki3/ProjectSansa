package com.sansa.auth.dto;

import com.sansa.auth.model.Models;

/**
 * 集約DTO。Jackson 用にデフォルトコンストラクタ必須。
 * getter/setter は「getX / setX」に統一。
 */
public final class Dtos {

    private Dtos() {}

    // --------- 認証フロー DTO ---------

    /** /api/auth/pre-register */
    public static class PreRegisterRequest {
        private String email;
        private String language;

        public PreRegisterRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    /** /api/auth/verify-email */
    public static class VerifyEmailRequest {
        private String preRegId;
        private String code;

        public VerifyEmailRequest() {}

        public String getPreRegId() { return preRegId; }
        public void setPreRegId(String preRegId) { this.preRegId = preRegId; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    /** /api/auth/register */
    public static class RegisterRequest {
        private String preRegId;
        private String accountId;
        private String email;
        private String language;

        public RegisterRequest() {}

        public String getPreRegId() { return preRegId; }   // ★追加
        public void setPreRegId(String preRegId) { this.preRegId = preRegId; } // ★追加

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    // --------- MFA DTO ---------

    public static class TotpVerifyRequest {
        private String code;

        public TotpVerifyRequest() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class EmailOtpSendRequest {
        private String email;

        public EmailOtpSendRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class EmailOtpVerifyRequest {
        private String email;
        private String code;

        public EmailOtpVerifyRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    // --------- WebAuthn / Login DTO（将来用） ---------

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenPair {
        private String accessToken;
        private String refreshToken;

        public TokenPair() {}

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class LoginResponse {
        private String status;     // "ok" / "error" など
        private boolean success;
        private String message;
        private TokenPair tokens;  // 任意

        /** Jackson用デフォルト ctor（※呼び出し側が new LoginResponse() しても通る） */
        public LoginResponse() {}

        public LoginResponse(String status, boolean success, String message, TokenPair tokens) {
            this.status = status;
            this.success = success;
            this.message = message;
            this.tokens = tokens;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public TokenPair getTokens() { return tokens; }
        public void setTokens(TokenPair tokens) { this.tokens = tokens; }
    }

    // --------- 共通レスポンス ---------

    public static class AuthResult {
        private boolean success;
        private String message;
        private Models.User user;      // 必要に応じて
        private TokenPair tokens;      // 必要に応じて

        public AuthResult() {}

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static AuthResult ok() {
            return new AuthResult(true, "ok");
        }
        public static AuthResult ok(String message) {
            return new AuthResult(true, message);
        }
        public static AuthResult error(String message) {
            return new AuthResult(false, message);
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Models.User getUser() { return user; }
        public void setUser(Models.User user) { this.user = user; }

        public TokenPair getTokens() { return tokens; }
        public void setTokens(TokenPair tokens) { this.tokens = tokens; }
    }
}
