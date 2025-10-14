package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 02. WebAuthn 登録・認証・管理 (/webauthn/...)
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/02_IT_WebAuthn_登録・認証・管理.md
@DisplayName("[IT-02] WebAuthn 登録・認証・管理")
class IT02_WebAuthnTest extends ITBase {

  // A) 登録
  @Test @DisplayName("IT-02-001 register/options 正常 (GET /webauthn/register/options)")
  void IT_02_001() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-002 register/verify 正常 (POST /webauthn/register/verify)")
  void IT_02_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-003 register/verify 失敗 -> 400 invalid_assertion")
  void IT_02_003() throws Exception { /* TODO */ }

  // B) 認証
  @Test @DisplayName("IT-02-004 challenge 正常 (GET /webauthn/challenge)")
  void IT_02_004() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-005 assertion 成功 (POST /webauthn/assertion)")
  void IT_02_005() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-006 assertion 成功だが MFA 必須")
  void IT_02_006() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-007 assertion 署名不正 -> 400")
  void IT_02_007() throws Exception { /* TODO */ }

  // C) 管理
  @Test @DisplayName("IT-02-008 credentials 一覧 (GET /webauthn/credentials)")
  void IT_02_008() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-02-009 credential 失効 (DELETE /webauthn/credentials/{id})")
  void IT_02_009() throws Exception { /* TODO */ }
}
