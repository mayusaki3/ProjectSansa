package com.sansa.auth.dto.mfa;

/** TOTP 初期化リクエスト */
public class MfaTotpInitRequest {
    private final String accountId;
    public MfaTotpInitRequest(String accountId) { this.accountId = accountId; }
    public String getAccountId() { return accountId; }
}
