package com.sansa.auth.model;

import java.time.Instant;
import java.util.*;

public class Models {
    public static class User {
        public UUID userId = UUID.randomUUID();
        public String accountId;
        public String email;
        public boolean emailVerified;
        public boolean mfaEnabled = true;
        public Set<String> mfaMethods = new HashSet<>(List.of("totp","email_otp"));
        public String language = "ja-JP";
        public long tokenVersion = 0L;
        public Instant createdAt = Instant.now();
        public Instant lastLoginAt = null;
    }

    public static class Session {
        public UUID sessionId = UUID.randomUUID();
        public UUID userId;
        public String deviceId;
        public long tokenVersion;
        public Instant createdAt = Instant.now();
        public Instant lastSeenAt = Instant.now();
        public Instant mfaLastOkAt = null;
    }
}
