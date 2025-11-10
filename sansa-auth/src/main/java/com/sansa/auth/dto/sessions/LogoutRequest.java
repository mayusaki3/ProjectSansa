package com.sansa.auth.dto.sessions;

/**
 * ログアウト要求。
 * - sessionId: 特定セッションのみ無効化
 * - refreshTokenJti: RT起点でセッションを特定する場合に利用（あれば）
 * どれを使うかはAPI仕様に従う。
 */
public class LogoutRequest {

    private String sessionId;
    private String refreshTokenJti;

    public LogoutRequest() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRefreshTokenJti() {
        return refreshTokenJti;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setRefreshTokenJti(String refreshTokenJti) {
        this.refreshTokenJti = refreshTokenJti;
    }
}
