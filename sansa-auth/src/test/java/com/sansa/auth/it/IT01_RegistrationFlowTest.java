package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 01. 登録フロー（/auth/pre-register → /auth/verify-email → /auth/register）
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/01_IT_登録フロー_pre-verify-register.md
@DisplayName("[IT-01] 登録フロー")
class IT01_RegistrationFlowTest extends ITBase {

  @Test @DisplayName("IT-01-001 pre-register 正常 (POST /auth/pre-register)")
  void IT_01_001() throws Exception {
    // Given: email=user1@example.com, language=ja-JP
    // When: POST /auth/pre-register
    // Then: 200/202 success=true, throttleMs?, Content-Language
    // TODO: mvc.perform(...).andExpect(...)
  }

  @Test @DisplayName("IT-01-002 pre-register ブロックドメイン -> 400 invalid-argument")
  void IT_01_002() throws Exception {
    // TODO
  }

  @Test @DisplayName("IT-01-003 verify-email 正常 -> preRegId 取得 (POST /auth/verify-email)")
  void IT_01_003() throws Exception {
    // TODO
  }

  @Test @DisplayName("IT-01-004 verify-email 期限切れ/不一致 -> 400")
  void IT_01_004() throws Exception {
    // TODO
  }

  @Test @DisplayName("IT-01-005 register 正常 201 + userId + emailVerified=true (POST /auth/register)")
  void IT_01_005() throws Exception {
    // TODO
  }

  @Test @DisplayName("IT-01-006 register preRegId 二重使用 -> 410/400")
  void IT_01_006() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-01-007 accountId 重複 -> 409")
  void IT_01_007() throws Exception { /* TODO */ }
}
