package com.sansa.auth.dto.mfa;

/** Recovery コード発行リクエスト */
public class MfaRecoveryInitRequest {
    private final String accountId;
    private final int count;
    public MfaRecoveryInitRequest(String accountId, int count) { this.accountId = accountId; this.count = count; }
    public String getAccountId() { return accountId; }
    public int getCount() { return count; }
}
