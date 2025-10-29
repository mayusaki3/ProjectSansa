package com.sansa.auth.it.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * SMTP + MailHog のITは別プロファイルでのみ実行。
 * in-mem プロファイルではコンパイルのみ通す。
 */
@Disabled("Run with smtp & mailhog profile only")
class MailHogSmtpIT {
  @Test void noop() {}
}
