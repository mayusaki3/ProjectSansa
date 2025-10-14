package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 05. ログアウト/全端末無効化 (/auth/logout, /auth/logout_all)
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/05_IT_ログアウト・全端末無効化.md
@DisplayName("[IT-05] ログアウト・全端末無効化")
class IT05_LogoutTest extends ITBase {

  @Test @DisplayName("IT-05-001 logout 現セッション -> 200/204")
  void IT_05_001() throws Exception { /* TODO: 直後 /auth/session active=false */ }

  @Test @DisplayName("IT-05-002 logout sessionId/refreshToken 指定")
  void IT_05_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-05-003 logout_all token_version++ 旧AT/RT無効")
  void IT_05_003() throws Exception { /* TODO */ }
}
