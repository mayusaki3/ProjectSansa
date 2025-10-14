package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 03. ログイン・MFA誘導・現在セッション
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/03_IT_ログイン・MFA誘導・現在セッション.md
@DisplayName("[IT-03] ログイン/MFA誘導/現在セッション")
class IT03_LoginAndCurrentSessionTest extends ITBase {

  @Test @DisplayName("IT-03-001 password 成功 (POST /auth/login)")
  void IT_03_001() throws Exception { /* TODO: tokens + amr=['pwd'] + sessionId → at/rt 設定 */ }

  @Test @DisplayName("IT-03-002 password → MFA 必須")
  void IT_03_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-03-003 password 失敗 -> 401")
  void IT_03_003() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-03-004 GET /auth/session（有効）")
  void IT_03_004() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-03-005 GET /auth/session（無効） -> 401")
  void IT_03_005() throws Exception { /* TODO */ }
}
