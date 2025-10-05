package com.sansa.auth.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain models as simple JavaBeans.
 */
public final class Models {

    private Models() {}

    public static class User {
        private UUID id;
        private String email;
        private String loginId;
        private String passwordHash;
        private boolean emailVerified;
        private Instant createdAt;

        public User() { }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLoginId() { return loginId; }
        public void setLoginId(String loginId) { this.loginId = loginId; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class Session {
        private UUID id;
        private UUID userId;
        private String deviceId;
        private Instant createdAt;

        public Session() { }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
