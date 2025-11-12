package com.sansa.auth.dto.sessions;

import java.time.Instant;

/**
 * SessionInfo.builder() が参照されるため、手製ビルダーと UserSummary を提供。
 */
public class SessionInfo {
    public static class UserSummary {
        private String userId;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    private String sessionId;
    private Instant issuedAt;
    private Instant lastActive;
    private Instant expiresAt;
    private String amr;
    private UserSummary userSummary;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public Instant getLastActive() { return lastActive; }
    public void setLastActive(Instant lastActive) { this.lastActive = lastActive; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getAmr() { return amr; }
    public void setAmr(String amr) { this.amr = amr; }

    public UserSummary getUserSummary() { return userSummary; }
    public void setUserSummary(UserSummary userSummary) { this.userSummary = userSummary; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SessionInfo inst = new SessionInfo();
        public Builder sessionId(String v) { inst.setSessionId(v); return this; }
        public Builder issuedAt(Instant v) { inst.setIssuedAt(v); return this; }
        public Builder lastActive(Instant v) { inst.setLastActive(v); return this; }
        public Builder expiresAt(Instant v) { inst.setExpiresAt(v); return this; }
        public Builder amr(String v) { inst.setAmr(v); return this; }
        public Builder userSummary(UserSummary v) { inst.setUserSummary(v); return this; }
        public SessionInfo build() { return inst; }
    }
}
