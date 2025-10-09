package com.sansa.auth.util;

import com.sansa.auth.dto.mfa.MfaTotpVerifyRequest;
import com.sansa.auth.service.MfaService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UT-06-001..007
 * 仕様根拠: 06_Util_MFA_TOTP・メールコード・Recovery.md
 */
class MfaUtilTest {

    @Test
    void totp_activate_ok_allows_skew() {
        var mfa = new MfaService();
        mfa.totpEnroll();
        mfa.totpActivate(MfaTotpActivateRequest.builder().code(code).build());
        mfa.totpVerify(MfaTotpVerifyRequest.builder()
            .challengeId(challengeId)
            .code(code)
            .build());
        assertThat(res.isAuthenticated()).isTrue();
        assertThat(res.getAmr()).contains("mfa");
    }

    // Email OTP / Recovery は雛形
    @Test void email_send_rate_limited_429() { /* TODO */ }
    @Test void email_verify_ok_ng_expired() { /* TODO */ }
    @Test void recovery_issue_once_only() { /* TODO */ }
    @Test void recovery_verify_consumes_code() { /* TODO */ }
}
