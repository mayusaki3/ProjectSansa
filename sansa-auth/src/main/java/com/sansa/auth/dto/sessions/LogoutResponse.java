package com.sansa.auth.dto.sessions;

/**
 * ログアウト結果。
 */
public class LogoutResponse {

    private boolean success;

    public LogoutResponse() {
    }

    public LogoutResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // 既存コード互換のためのヘルパ
    public static LogoutResponse ok(boolean success) {
        return new LogoutResponse(success);
    }
}
