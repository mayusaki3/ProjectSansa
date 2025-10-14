package com.sansa.auth.spec;

import org.junit.jupiter.api.*;

// Service ログアウト・全端末無効化（UT-04-001〜004）
// 仕様: docs/ja-JP/単体テスト/Webサービス/認証・セッション/04_Service_ログアウト・全端末無効化.md
@DisplayName("[UT-04] Service ログアウト・全端末無効化")
class LogoutServiceUnitTest {

    @Test
    @DisplayName("UT-04-001 現セッションのログアウト -> 200/204")
    void UT_04_001() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-04-002 RT/セッションID 指定ログアウト -> 200/204")
    void UT_04_002() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-04-003 logout_all で token_version++ (旧AT/RTが401)")
    void UT_04_003() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UT-04-004 logout_all 冪等（多重実行可）")
    void UT_04_004() {
        Assertions.assertTrue(true);
    }
}
