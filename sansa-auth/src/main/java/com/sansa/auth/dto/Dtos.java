package com.sansa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DTO classes used by controllers/services.
 * JavaBeans style: getters start with getX, setters with setX.
 */
public final class Dtos {

    private Dtos() {}

    // ---------- Auth flows ----------
    public static class PreRegisterRequest {
        @NotBlank @Email
        private String email;

        public PreRegisterRequest() { }
        public PreRegisterRequest(String email) { this.email = email; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class VerifyEmailRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String code;

        public VerifyEmailRequest() { }
        public VerifyEmailRequest(String email, String code) {
            this.email = email;
            this.code = code;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class RegisterRequest {
        // values used by different layers; some may be optional depending on flow
        @NotBlank
        private String preRegId;
        @NotBlank
        private String accountId;
        @NotBlank
        private String language;

        @Email
        private String email;
        private String password;

        @NotNull
        private UUID userId; // some code paths expect this

        public RegisterRequest() { }

        public String getPreRegId() { return preRegId; }
        public void setPreRegId(String preRegId) { this.preRegId = preRegId; }

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }

    public static class LoginRequest {
        @NotNull
        private UUID userId;
        @NotBlank
        private String password;
        private String deviceId;

        public LoginRequest() { }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    }

    // ---------- MFA ----------
    public static class TotpVerifyRequest {
        private int code;

        public TotpVerifyRequest() { }
        public TotpVerifyRequest(int code) { this.code = code; }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
    }

    public static class EmailOtpRequest {
        @NotBlank @Email
        private String email;

        public EmailOtpRequest() { }
        public EmailOtpRequest(String email) { this.email = email; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class EmailOtpVerifyRequest {
        @NotBlank @Email
        private String email;
        private int code;

        public EmailOtpVerifyRequest() { }
        public EmailOtpVerifyRequest(String email, int code) {
            this.email = email;
            this.code = code;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
    }

    // ---------- Responses ----------
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
        private long expiresIn; // seconds

        public TokenPair() { }

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public TokenPair(String accessToken, String refreshToken, long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    }

    public static class LoginResponse {
        private String status;
        private boolean success;
        private String userId;
        private TokenPair tokens;
        private long challengeTimeout;

        public LoginResponse() { }

        public LoginResponse(String status, boolean success, String userId, TokenPair tokens) {
            this.status = status;
            this.success = success;
            this.userId = userId;
            this.tokens = tokens;
        }

        public LoginResponse(String status, String userId, long challengeTimeout) {
            this.status = status;
            this.userId = userId;
            this.challengeTimeout = challengeTimeout;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public TokenPair getTokens() { return tokens; }
        public void setTokens(TokenPair tokens) { this.tokens = tokens; }

        public long getChallengeTimeout() { return challengeTimeout; }
        public void setChallengeTimeout(long challengeTimeout) { this.challengeTimeout = challengeTimeout; }
    }

    public static class AuthResult {
        private boolean success;
        private String message;
        private Map<String, Object> data;
        private TokenPair tokens;
        private Instant timestamp = Instant.now();

        public AuthResult() { }

        public static AuthResult ok(String message) {
            AuthResult r = new AuthResult();
            r.success = true;
            r.message = message;
            return r;
        }

        public static AuthResult ok(TokenPair tokens) {
            AuthResult r = new AuthResult();
            r.success = true;
            r.tokens = tokens;
            return r;
        }

        public static AuthResult ok(Map<String, Object> data) {
            AuthResult r = new AuthResult();
            r.success = true;
            r.data = new HashMap<>(data);
            return r;
        }

        public static AuthResult error(String message) {
            AuthResult r = new AuthResult();
            r.success = false;
            r.message = message;
            return r;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }

        public TokenPair getTokens() { return tokens; }
        public void setTokens(TokenPair tokens) { this.tokens = tokens; }

        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    }
}
