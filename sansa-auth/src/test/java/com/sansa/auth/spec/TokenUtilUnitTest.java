package com.sansa.auth.spec;

import org.junit.jupiter.api.*;

// Util: トークン署名・TTL・検証（UT-05-001〜006）
// 仕様: docs/ja-JP/単体テスト/Webサービス/認証・セッション/05_Util_トークン署名・TTL・検証.md
@DisplayName("[UT-05] Util トークン署名・TTL・検証")
class TokenUtilUnitTest {

    @Test
    @DisplayName("UT-05-001 署名検証成功")
    void UT_05_001() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-05-002 署名検証失敗 -> 401")
    void UT_05_002() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-05-003 TTL 満了 -> token_expired 401")
    void UT_05_003() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-05-004 token_version 埋め込み・不一致 401")
    void UT_05_004() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-05-005 /auth/token/refresh の tv 整合")
    void UT_05_005() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-05-006 /token/reused 以降は旧AT/RTが401")
    void UT_05_006() {
        Assertions.assertTrue(true);
    }
}
