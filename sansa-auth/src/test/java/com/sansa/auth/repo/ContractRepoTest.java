package com.sansa.auth.repo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UT-07: Repository 契約テスト（プリミティブ版）
 *
 * 仕様根拠: - 07_Repo_契約テスト_PreReg_WebAuthn_MFA_Session.md
 *
 * 方針: - ここでは「テストの型/期待値」を固定し、実リポジトリ実装差し替えで再利用できる形にする。 - 現時点では実装が未固定のため @Disabled
 * でスキップ。実装到着後に解除して合格させる。
 */
class ContractRepoTest {

    @Test
    @Disabled("リポジトリ実装が未接続のためスキップ")
    @DisplayName("PreReg: 同一メールの最新を取得できる / 旧いものは上書き")
    void preReg_issue_and_find_latest() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("PreReg: TTL で期限切れが掃除される")
    void preReg_ttl_expire() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("PreReg: consume は 1 回限り")
    void preReg_consume_once() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("WebAuthn: save → list → delete ができる")
    void webauthn_save_get_list_delete() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("WebAuthn: signCount は単調増加で更新される")
    void webauthn_signCount_monotonic() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("MFA: TOTP の enroll → activate → verify の状態が保存される")
    void mfa_totp_enroll_activate_verify_reflects() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("MFA: Email の送信状態/TTL/再送間隔が反映される")
    void mfa_email_state_ttl_resend_interval() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("Session: create → list → delete")
    void session_create_list_delete() {
        /* 実装到着後に記述 */ }

    @Test
    @Disabled("未接続")
    @DisplayName("TokenVersion: get → increment ができる")
    void token_version_get_increment() {
        /* 実装到着後に記述 */ }
}
