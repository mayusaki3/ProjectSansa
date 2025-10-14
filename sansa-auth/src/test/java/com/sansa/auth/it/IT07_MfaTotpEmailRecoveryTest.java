package com.sansa.auth.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 07. MFA (TOTP/Email OTP/Recovery)
// 仕様: docs/ja-JP/結合テスト/Webサービス/認証・セッション/07_IT_MFA_TOTP・EmailOTP・Recovery.md
@DisplayName("[IT-07] MFA TOTP・EmailOTP・Recovery")
class IT07_MfaTotpEmailRecoveryTest extends ITBase {

  @Test @DisplayName("IT-07-001 TOTP enroll→activate→verify")
  void IT_07_001() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-07-002 TOTP verify 時計ずれ ±1 step 許容")
  void IT_07_002() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-07-003 Email OTP send レート制限 429")
  void IT_07_003() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-07-004 Email OTP verify 成功/不正/期限切れ")
  void IT_07_004() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-07-005 Recovery issue 一度だけ表示")
  void IT_07_005() throws Exception { /* TODO */ }

  @Test @DisplayName("IT-07-006 Recovery verify 消費/再利用不可")
  void IT_07_006() throws Exception { /* TODO */ }
}
