package com.sansa.auth.dto.mfa;

/**
 * POST /auth/mfa/totp/verify のリクエストDTO
 * 仕様: 04_MFA.md「TOTP verify」
 * フィールド（必須）:
 *  - accountId
 *  - code
 */
public class MfaTotpVerifyRequest {
    private final String accountId;
    private final String code;
    public MfaTotpVerifyRequest(String accountId, String code) { this.accountId = accountId; this.code = code; }
    public String getAccountId() { return accountId; }
    public String getCode() { return code; }
}