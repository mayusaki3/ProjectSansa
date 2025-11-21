package com.sansa.auth.dto.mfa;

/** Email MFA 検証レスポンス */
public class MfaEmailVerifyResponse {
    private final boolean ok;
    public MfaEmailVerifyResponse(boolean ok) { this.ok = ok; }
    public boolean isOk() { return ok; }
}
