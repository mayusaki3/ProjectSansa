package com.sansa.auth.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /auth/mfa/email/verify のリクエストDTO
 * 仕様: 04_MFA.md「Email verify」
 * フィールド（必須）:
 *  - challengeId
 *  - code（TTL=5分）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaEmailVerifyRequest {
    /** MFA 検証フローを識別するチャレンジID */
    private String challengeId;

    /** メールで受け取った OTP コード（TTL=5分） */
    private String code;
}
