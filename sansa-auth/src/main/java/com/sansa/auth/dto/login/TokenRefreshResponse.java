package com.sansa.auth.dto.login;

/**
 * リフレッシュ応答DTO。AuthServiceImpl が
 * TokenRefreshResponse.Tokens を要求するため内包クラスを用意。
 */
public class TokenRefreshResponse {

    public static class Tokens {
        private String accessToken;
        private String refreshToken;

        public Tokens() {}

        public Tokens(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}
