package com.sansa.auth.dto.mfa;

/** TOTP 検証レスポンス */
public class MfaTotpVerifyResponse {
    private final boolean ok;
    public MfaTotpVerifyResponse(boolean ok) { this.ok = ok; }
    public boolean isOk() { return ok; }
}
