package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 08. 監査ログ / レート制限
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/08_IT_監査・レート制限.md
@DisplayName("[IT-08] 監査ログ・レート制限")
class IT08_AuditAndRateLimitTest extends ITBase {

  @Test @DisplayName("IT-08-001 REGISTER 系の監査")
  void IT_08_001() { /* TODO: 監査出力のアサート方針（テスト用 Appender or TestSink） */ }

  @Test @DisplayName("IT-08-002 LOGIN/LOGOUT/LOGOUT_ALL 監査")
  void IT_08_002() { /* TODO */ }

  @Test @DisplayName("IT-08-003 MFA 監査")
  void IT_08_003() { /* TODO */ }

  @Test @DisplayName("IT-08-004 pre-register のレート制限ヘッダ")
  void IT_08_004() throws Exception { /* TODO */ }
}
