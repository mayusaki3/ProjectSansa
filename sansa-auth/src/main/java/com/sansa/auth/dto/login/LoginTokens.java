package com.sansa.auth.dto.login;

/**
 * 既存コードが tokens.setSessionId(...) を呼ぶため、setter を備える POJO にする。
 */
public class LoginTokens {
    private String accessToken;
    private String refreshToken;
    private String sessionId;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
