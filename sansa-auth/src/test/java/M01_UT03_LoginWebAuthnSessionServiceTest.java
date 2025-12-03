import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * M01:UT-03 Service ログイン・WebAuthn・セッションの DUMMY テストクラス。
 *
 * 役割:
 * - A) Password ログイン
 * - B) WebAuthn 認証
 * - C) セッション
 * - D) トークンリフレッシュ
 *   に対応するテストメソッドを、テストIDに沿って用意する。
 */
@Disabled("M01:UT-03 DUMMY skeleton (実装前)")
class M01_UT03_LoginWebAuthnSessionServiceTest {

    // A) Password ログイン

    @Test
    @DisplayName("M01:UT-03-001[DUMMY] パスワード成功")
    void M01_UT_03_001_password_login_success() {
        // TODO: identifier=alice, password=correct で authenticated=true になることを検証
    }

    @Test
    @DisplayName("M01:UT-03-002[DUMMY] パスワードで MFA 必須")
    void M01_UT_03_002_password_login_requiresMfa() {
        // TODO: MFA 有効ユーザーで mfaRequired=true + mfa.challengeId などを検証
    }

    // B) WebAuthn 認証

    @Test
    @DisplayName("M01:UT-03-003[DUMMY] WebAuthn 認証チャレンジ取得")
    void M01_UT_03_003_webauthn_getChallenge() {
        // TODO: GET /webauthn/challenge で challenge, rpId, userVerification を検証
    }

    @Test
    @DisplayName("M01:UT-03-004[DUMMY] WebAuthn アサーション成功")
    void M01_UT_03_004_webauthn_assertion_success() {
        // TODO: 正常アサーションで authenticated=true, amr に \"webauthn\" を含むことを検証
    }

    @Test
    @DisplayName("M01:UT-03-005[DUMMY] WebAuthn アサーション成功だが MFA 必須")
    void M01_UT_03_005_webauthn_assertion_success_but_mfa_required() {
        // TODO: リスクポリシーで MFA 必須となるケースの応答を検証
    }

    @Test
    @DisplayName("M01:UT-03-006[DUMMY] WebAuthn アサーション不正 -> 400")
    void M01_UT_03_006_webauthn_assertion_invalid_returns400() {
        // TODO: 署名検証失敗時の 400 + type=.../invalid_assertion を検証
    }

    // C) セッション

    @Test
    @DisplayName("M01:UT-03-007[DUMMY] 現在セッション取得")
    void M01_UT_03_007_get_current_session() {
        // TODO: GET /auth/session で active=true, amr, expiresAt などを検証
    }

    @Test
    @DisplayName("M01:UT-03-008[DUMMY] 複数セッション列挙")
    void M01_UT_03_008_list_multiple_sessions() {
        // TODO: GET /sessions で SessionInfo[] (2件以上) を検証
    }

    @Test
    @DisplayName("M01:UT-03-009[DUMMY] セッション個別失効（存在するID）")
    void M01_UT_03_009_revoke_existing_session() {
        // TODO: DELETE /sessions/{sessionId} で 204 + 対象セッションが無効化されることを検証
    }

    @Test
    @DisplayName("M01:UT-03-010[DUMMY] セッション個別失効（未存在） -> 404")
    void M01_UT_03_010_revoke_nonExisting_session_returns404() {
        // TODO: 未存在 ID で 404 + type=.../session_not_found を検証
    }

    // D) トークンリフレッシュ

    @Test
    @DisplayName("M01:UT-03-011[DUMMY] refresh 成功（200）")
    void M01_UT_03_011_refresh_success() {
        // TODO: 有効 RT で 200 + 新しい AT/RT が返り、tv は現行値であることを検証
    }

    @Test
    @DisplayName("M01:UT-03-012[DUMMY] refresh 期限切れ → 401 /token/expired")
    void M01_UT_03_012_refresh_expired_returns401() {
        // TODO: 期限切れ RT で 401 + type=https://errors.sansa.dev/token/expired を検証
    }

    @Test
    @DisplayName("M01:UT-03-013[DUMMY] refresh 再利用検知 → 401 /token/reused + tv++")
    void M01_UT_03_013_refresh_reuse_detected_returns401_and_increments_tv() {
        // TODO: 再利用 RT で 401 + type=/token/reused、および token_version++ を検証
    }
}
