package com.sansa.auth.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Project Sansa - Domain Models
 * JavaBeans 準拠の getter/setter を用意
 */
public final class Models {

    // --------------------
    // User
    // --------------------
    public static final class User {
        private UUID id;
        private UUID accountId;
        private String email;
        private String passwordHash;
        private Instant createdAt;

        public User() {}

        public User(UUID id, UUID accountId, String email, Instant createdAt) {
            this.id = id;
            this.accountId = accountId;
            this.email = email;
            this.createdAt = createdAt;
        }

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

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;
            User user = (User) o;
            return Objects.equals(id, user.id);
        }
        @Override public int hashCode() { return Objects.hash(id); }
    }

    // --------------------
    // Session
    // --------------------
    public static final class Session {
        private UUID sessionId;
        private UUID userId;
        private String token;
        private Instant createdAt;

        public Session() {}

        public Session(UUID sessionId, UUID userId, String token, Instant createdAt) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.token = token;
            this.createdAt = createdAt;
        }

        public UUID getSessionId() { return sessionId; }
        public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    /** Pre-registration */
    public static final class PreReg {
        private UUID preRegId;
        private String email;
        private String language;
        private Instant createdAt;

        public PreReg() {}

        public PreReg(UUID preRegId, String email, String language, Instant createdAt) {
            this.preRegId = preRegId;
            this.email = email;
            this.language = language;
            this.createdAt = createdAt;
        }

        public UUID getPreRegId() { return preRegId; }
        public void setPreRegId(UUID preRegId) { this.preRegId = preRegId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    private Models() {}
}
