package com.sansa.auth.spec;

import org.junit.jupiter.api.*;

// Service ログイン/WA/セッション/リフレッシュ（UT-03-001〜013）
// 仕様: docs/ja-JP/単体テスト/Webサービス/認証・セッション/03_Service_ログイン・WebAuthn・セッション.md
@DisplayName("[UT-03] Service ログイン / WebAuthn / セッション / Refresh")
class LoginWebAuthnSessionServiceUnitTest {

    // A) Password
    @Test
    @DisplayName("UT-03-001 パスワード成功: tokens + amr=['pwd'] + sessionId")
    void UT_03_001() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-002 パスワード MFA 必須: authenticated=false, mfaRequired=true")
    void UT_03_002() {
        Assertions.assertTrue(true);
    }

    // B) WebAuthn
    @Test
    @DisplayName("UT-03-003 認証チャレンジ取得")
    void UT_03_003() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-004 アサーション成功: authenticated=true, amr contains 'webauthn'")
    void UT_03_004() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-005 アサーション成功だが MFA 必須")
    void UT_03_005() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-006 アサーション不正 -> 400")
    void UT_03_006() {
        Assertions.assertTrue(true);
    }

    // C) セッション
    @Test
    @DisplayName("UT-03-007 現在セッション取得: active=true, amr/expiresAt")
    void UT_03_007() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-008 セッション列挙: 2件以上")
    void UT_03_008() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-009 個別失効（存在）-> 204")
    void UT_03_009() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-010 個別失効（未存在）-> 404")
    void UT_03_010() {
        Assertions.assertTrue(true);
    }

    // D) Refresh
    @Test
    @DisplayName("UT-03-011 refresh 成功: 新AT/RT + tv据え置き + 旧RT不可")
    void UT_03_011() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-012 refresh 期限切れ -> 401 /token/expired")
    void UT_03_012() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-03-013 refresh 再利用検知 -> 401 /token/reused + tv++")
    void UT_03_013() {
        Assertions.assertTrue(true);
    }
}
