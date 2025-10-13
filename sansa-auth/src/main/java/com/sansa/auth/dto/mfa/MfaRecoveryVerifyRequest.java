package com.sansa.auth.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /auth/mfa/recovery/verify のリクエストDTO
 * 仕様: 04_MFA.md「Recovery verify」
 * フィールド（必須）:
 *  - challengeId
 *  - code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaRecoveryVerifyRequest {
    /** MFA 検証フローを識別するチャレンジID */
    private String challengeId;

    /** リカバリーコード（1回使い切り） */
    private String code;
}
