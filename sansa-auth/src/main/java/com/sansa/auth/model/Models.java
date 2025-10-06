package com.sansa.auth.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Project Sansa - Domain Models
 * JavaBeans 準拠の getter/setter を用意
 */
public final class Models {

    // --------------------
    // User
    // --------------------
    public static class User {
        private UUID id;
        private UUID accountId;
        private String email;
        private String passwordHash;
        private Instant createdAt;

        public User() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public UUID getAccountId() { return accountId; }
        public void setAccountId(UUID accountId) { this.accountId = accountId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    // --------------------
    // Session
    // --------------------
    public static class Session {
        private UUID id;
        private UUID userId;
        private String deviceId;
        private Instant createdAt;

        public Session() {}

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
