package com.sansa.auth.dto.auth;

import java.time.Instant;

/**
 * メール認証コード検証結果。
 */
public class VerifyEmailResponse {

    private boolean success;
    private String preRegId;
    private Instant expiresAt;
    private Long throttleMsHint;

    public boolean isSuccess() {
        return success;
    }

    public String getPreRegId() {
        return preRegId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Long getThrottleMsHint() {
        return throttleMsHint;
    }

    // Builder互換（既存コードが builder() を呼んでいる場合用）
    public static VerifyEmailResponseBuilder builder() {
        return new VerifyEmailResponseBuilder();
    }

    public static class VerifyEmailResponseBuilder {
        private final VerifyEmailResponse target = new VerifyEmailResponse();

        public VerifyEmailResponseBuilder success(boolean success) {
            target.success = success;
            return this;
        }

        public VerifyEmailResponseBuilder preRegId(String preRegId) {
            target.preRegId = preRegId;
            return this;
        }

        public VerifyEmailResponseBuilder expiresAt(Instant expiresAt) {
            target.expiresAt = expiresAt;
            return this;
        }

        public VerifyEmailResponseBuilder throttleMsHint(Long throttleMsHint) {
            target.throttleMsHint = throttleMsHint;
            return this;
        }

        public VerifyEmailResponse build() {
            return target;
        }
    }
}
