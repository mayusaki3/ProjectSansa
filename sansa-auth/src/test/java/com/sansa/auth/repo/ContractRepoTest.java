package com.sansa.auth.repo;

import org.junit.jupiter.api.Test;

/**
 * UT-07-001..009
 * 仕様根拠: 07_Repo_契約テスト_PreReg_WebAuthn_MFA_Session.md
 */
class ContractRepoTest {

    @Test void preReg_issue_and_find_latest() { /* TODO */ }
    @Test void preReg_ttl_expire() { /* TODO */ }
    @Test void preReg_consume_once() { /* TODO */ }

    @Test void webauthn_save_get_list_delete() { /* TODO */ }
    @Test void webauthn_signCount_monotonic() { /* TODO */ }

    @Test void mfa_totp_enroll_activate_verify_reflects() { /* TODO */ }
    @Test void mfa_email_state_ttl_resend_interval() { /* TODO */ }

    @Test void session_create_list_delete() { /* TODO */ }
    @Test void token_version_get_increment() { /* TODO */ }
}
