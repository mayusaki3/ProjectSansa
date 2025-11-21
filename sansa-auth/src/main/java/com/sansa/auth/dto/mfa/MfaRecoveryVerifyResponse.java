package com.sansa.auth.dto.mfa;

/** Recovery コード検証レスポンス */
public class MfaRecoveryVerifyResponse {
    private final boolean ok;
    public MfaRecoveryVerifyResponse(boolean ok) { this.ok = ok; }
    public boolean isOk() { return ok; }
}
