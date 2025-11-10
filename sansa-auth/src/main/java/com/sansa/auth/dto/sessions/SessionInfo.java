package com.sansa.auth.dto.sessions;

import java.time.Instant;

/**
 * セッション情報。APIレスポンス用。
 */
public class SessionInfo {

    private String id;
    private Instant createdAt;
    private Instant lastActiveAt;
    private String ip;
    private String userAgent;
    private boolean current;

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
