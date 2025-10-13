package com.sansa.auth.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /auth/mfa/totp/enroll のレスポンスDTO
 * 仕様: 04_MFA.md「TOTP enroll → MfaTotpEnrollResponse」
 * フィールド:
 *  - secret: 表示用のTOTPシークレット（UIはQRを提示）
 *  - uri   : otpauth URI（例: otpauth://totp/...）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaTotpEnrollResponse {
    /** 表示用の TOTP シークレット（UI 側で QR 生成などに使用） */
    private String secret;

    /** otpauth URI（例: otpauth://totp/...） */
    private String uri;
}
