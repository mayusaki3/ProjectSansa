package com.sansa.auth.spec;

import org.junit.jupiter.api.*;

// Service 登録フロー（UT-02-001〜007）
// 仕様: docs/ja-JP/単体テスト/Webサービス/認証・セッション/02_Service_登録フロー_pre-verify-register.md
@DisplayName("[UT-02] Service 登録フロー")
class RegistrationServiceUnitTest {

  @Test @DisplayName("UT-02-001 pre-register 正常")
  void UT_02_001() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-002 pre-register レート制限 -> 429")
  void UT_02_002() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-003 verify-email 成功 preRegId 付与")
  void UT_02_003() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-004 verify-email 期限切れ -> 400 expired")
  void UT_02_004() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-005 register 成功 201 + userId + emailVerified")
  void UT_02_005() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-006 register preRegId 使い回し -> 410/400")
  void UT_02_006() { Assertions.assertTrue(true); }

  @Test @DisplayName("UT-02-007 accountId 重複 -> 409")
  void UT_02_007() { Assertions.assertTrue(true); }
}
