import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * M01:UT-02 Service 登録フロー（pre-register / verify-email / register）の DUMMY テストクラス。
 *
 * 役割:
 * - テスト仕様 M01:UT-02-001〜007 のテストメソッドを定義しておく。
 * - 後続で AuthService 実装およびポートスタブと接続する。
 */
@Disabled("M01:UT-02 DUMMY skeleton (実装前)")
class M01_UT02_RegistrationServiceTest {

    @Test
    @DisplayName("M01:UT-02-001[DUMMY] pre-register 正常")
    void M01_UT_02_001_preRegister_success() {
        // TODO: email 有効時に AuthResult(SUCCESS) 相当が返ることを検証
    }

    @Test
    @DisplayName("M01:UT-02-002[DUMMY] pre-register レート制限")
    void M01_UT_02_002_preRegister_rateLimited() {
        // TODO: 同一 email + IP 連続実行で 429 となることを検証
    }

    @Test
    @DisplayName("M01:UT-02-003[DUMMY] verify-email 成功（preRegId 発行）")
    void M01_UT_02_003_verifyEmail_success_issuesPreRegId() {
        // TODO: VerifyEmailResponse.preRegId が UUID で expiresIn>0 となることを検証
    }

    @Test
    @DisplayName("M01:UT-02-004[DUMMY] verify-email 期限切れ -> 400")
    void M01_UT_02_004_verifyEmail_expired_returns400() {
        // TODO: 期限切れコードで 400 + type=.../expired を検証
    }

    @Test
    @DisplayName("M01:UT-02-005[DUMMY] register 成功（ユーザー作成）")
    void M01_UT_02_005_register_success_createsUser() {
        // TODO: 有効 preRegId + accountId=alice で 201 + success=true + userId 付与を検証
    }

    @Test
    @DisplayName("M01:UT-02-006[DUMMY] register preRegId 使い回し -> 410/400")
    void M01_UT_02_006_register_preRegId_reuse_returns410_or400() {
        // TODO: preRegId 2回目使用で 410 or 400(expired/consumed) を検証
    }

    @Test
    @DisplayName("M01:UT-02-007[DUMMY] accountId 重複 -> 409")
    void M01_UT_02_007_register_accountId_conflict_returns409() {
        // TODO: accountId 重複時の 409 + type=.../account_id_taken を検証
    }
}
