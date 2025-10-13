package com.sansa.auth.dto.mfa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * POST /auth/mfa/totp/activate のリクエストDTO
 * 仕様: 04_MFA.md「TOTP activate」
 * フィールド:
 *  - code: 必須（6〜10桁）、時計ずれは実装方針で ±1 step
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaTotpActivateRequest {
    /** ユーザー入力の TOTP コード（6〜10桁） */
    private String code;
}
