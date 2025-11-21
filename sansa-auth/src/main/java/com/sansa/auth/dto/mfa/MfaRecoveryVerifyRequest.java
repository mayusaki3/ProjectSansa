package com.sansa.auth.dto.mfa;

/**
 * POST /auth/mfa/recovery/verify のリクエストDTO
 * 仕様: 04_MFA.md「Recovery verify」
 * フィールド（必須）:
 *  - accountId
 *  - code
 */
public class MfaRecoveryVerifyRequest {
    private final String accountId;
    private final String code;
    public MfaRecoveryVerifyRequest(String accountId, String code) { this.accountId = accountId; this.code = code; }
    public String getAccountId() { return accountId; }
    public String getCode() { return code; }
}
