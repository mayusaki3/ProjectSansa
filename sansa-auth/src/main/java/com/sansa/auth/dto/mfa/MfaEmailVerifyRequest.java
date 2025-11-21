package com.sansa.auth.dto.mfa;

/**
 * POST /auth/mfa/email/verify のリクエストDTO
 * 仕様: 04_MFA.md「Email verify」
 * フィールド（必須）:
 *  - accountId
 *  - code（TTL=5分）
 */
public class MfaEmailVerifyRequest {
    private final String accountId;
    private final String code;
    public MfaEmailVerifyRequest(String accountId, String code) { this.accountId = accountId; this.code = code; }
    public String getAccountId() { return accountId; }
    public String getCode() { return code; }
}
