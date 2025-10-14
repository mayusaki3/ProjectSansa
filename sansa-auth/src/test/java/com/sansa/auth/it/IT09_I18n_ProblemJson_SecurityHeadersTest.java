package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 09. i18n / problem+json / セキュリティヘッダ
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/09_IT_i18n_problem-json_ヘッダ.md
@DisplayName("[IT-09] i18n・problem+json・セキュリティヘッダ")
class IT09_I18n_ProblemJson_SecurityHeadersTest extends ITBase {

  @Test @DisplayName("IT-09-001 Accept-Language→Content-Language（成功）")
  void IT_09_001() throws Exception { /* TODO: /auth/login 成功, Content-Language==ja-JP */ }

  @Test @DisplayName("IT-09-002 Accept-Language→Content-Language（エラー）")
  void IT_09_002() throws Exception { /* TODO: 検証エラーで problem+json の言語確認 */ }

  @Test @DisplayName("IT-09-003 エラーフォーマット共通確認 (problem+json)")
  void IT_09_003() throws Exception { /* TODO: type/title/status/detail/errors[]/traceId */ }

  @Test @DisplayName("IT-09-004 認可必須APIを未認証で -> 401")
  void IT_09_004() throws Exception { /* TODO: /auth/session, /sessions, /webauthn/credentials */ }
}
