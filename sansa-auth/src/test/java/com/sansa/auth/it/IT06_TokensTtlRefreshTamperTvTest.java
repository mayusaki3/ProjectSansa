package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 06. トークン(TTL/リフレッシュ/改竄/tv)
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/06_IT_トークン_TTL・リフレッシュ・改竄・tv.md
@DisplayName("[IT-06] トークン TTL・Refresh・改竄・tv")
class IT06_TokensTtlRefreshTamperTvTest extends ITBase {

  @Test @DisplayName("IT-06-001 AT TTL 満了 -> 401 *token_expired")
  void IT_06_001() throws Exception { /* TODO: 時刻操作 or 短TTL発行 */ }

  @Test @DisplayName("IT-06-002 RTで refresh 成功（RTローテーション、tv据置）")
  void IT_06_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-06-003 RT 改竄 -> 401")
  void IT_06_003() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-06-004 logout_all 後 RT 使用 -> 401 /token/reused")
  void IT_06_004() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-06-005 kid/鍵束切替")
  void IT_06_005() throws Exception { /* TODO */ }
}
