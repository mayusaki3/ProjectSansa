package com.sansa.auth.dto.sessions;

/**
 * LogoutResponse.builder() が参照されるため手製ビルダーを用意。
 */
public class LogoutResponse {
    private boolean success;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final LogoutResponse inst = new LogoutResponse();
        public Builder success(boolean v) { inst.setSuccess(v); return this; }
        public LogoutResponse build() { return inst; }
    }
}
