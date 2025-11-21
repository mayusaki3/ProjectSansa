package com.sansa.auth.dto.mfa;

/** Email MFA 初期化（コード発行）リクエスト */
public class MfaEmailInitRequest {
    private final String accountId;
    public MfaEmailInitRequest(String accountId) { this.accountId = accountId; }
    public String getAccountId() { return accountId; }
}
