import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * M01:UT-04 Service ログアウト・全端末無効化の DUMMY テストクラス。
 *
 * 役割:
 * - ログアウトおよび logout_all の仕様に対応するテストメソッドを定義する。
 */
@Disabled("M01:UT-04 DUMMY skeleton (実装前)")
class M01_UT04_LogoutServiceTest {

    @Test
    @DisplayName("M01:UT-04-001[DUMMY] ログアウト（現セッション）")
    void M01_UT_04_001_logout_current_session() {
        // TODO: POST /auth/logout (ATで現セッション識別) → 200/204 + active=false を検証
    }

    @Test
    @DisplayName("M01:UT-04-002[DUMMY] ログアウト（RT/セッションID 指定）")
    void M01_UT_04_002_logout_by_refreshToken_or_sessionId() {
        // TODO: refreshToken / sessionId 指定で対象セッションが無効化されることを検証
    }

    @Test
    @DisplayName("M01:UT-04-003[DUMMY] 全端末ログアウト（logout_all）で token_version++")
    void M01_UT_04_003_logout_all_increments_token_version() {
        // TODO: logout_all 後、旧AT/RT が 401 となり、新AT/RT の tv が旧+1 であることを検証
    }

    @Test
    @DisplayName("M01:UT-04-004[DUMMY] logout_all の多重実行（冪等）")
    void M01_UT_04_004_logout_all_is_idempotent() {
        // TODO: logout_all 2回連続実行でも副作用が増えないことを検証
    }
}
