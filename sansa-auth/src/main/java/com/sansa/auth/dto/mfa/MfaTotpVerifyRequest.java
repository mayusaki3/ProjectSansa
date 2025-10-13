package com.sansa.auth.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /auth/mfa/totp/verify のリクエストDTO
 * 仕様: 04_MFA.md「TOTP verify」
 * フィールド（必須）:
 *  - challengeId
 *  - code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaTotpVerifyRequest {
    /** MFA 検証フローを識別するチャレンジID */
    private String challengeId;

    /** ユーザー入力の TOTP コード */
    private String code;
}
