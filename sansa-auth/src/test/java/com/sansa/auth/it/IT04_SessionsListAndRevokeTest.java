package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 04. セッション列挙・個別失効 (/sessions)
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/04_IT_セッション列挙・個別失効.md
@DisplayName("[IT-04] セッション列挙・個別失効")
class IT04_SessionsListAndRevokeTest extends ITBase {

  @Test @DisplayName("IT-04-001 GET /sessions 複数端末 >=2")
  void IT_04_001() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-04-002 DELETE /sessions/{id} 存在 -> 204")
  void IT_04_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-04-003 DELETE /sessions/{id} 未存在 -> 404")
  void IT_04_003() throws Exception { /* TODO */ }
}
