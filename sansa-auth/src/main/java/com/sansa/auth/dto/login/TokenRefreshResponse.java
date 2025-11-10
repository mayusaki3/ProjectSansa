package com.sansa.auth.dto.login;

/**
 * リフレッシュトークン再発行結果。
 */
public class TokenRefreshResponse {

    private boolean success;
    private Tokens tokens;

    public boolean isSuccess() {
        return success;
    }

    public Tokens getTokens() {
        return tokens;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setTokens(Tokens tokens) {
        this.tokens = tokens;
    }

    public static TokenRefreshResponseBuilder builder() {
        return new TokenRefreshResponseBuilder();
    }

    public static class Tokens {
        private String accessToken;
        private String refreshToken;

        public Tokens() {
        }

        public Tokens(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class TokenRefreshResponseBuilder {
        private final TokenRefreshResponse target = new TokenRefreshResponse();

        public TokenRefreshResponseBuilder success(boolean success) {
            target.success = success;
            return this;
        }

        public TokenRefreshResponseBuilder tokens(Tokens tokens) {
            target.tokens = tokens;
            return this;
        }

        public TokenRefreshResponse build() {
            return target;
        }
    }
}
