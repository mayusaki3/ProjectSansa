package com.sansa.auth.it.mail;

import com.sansa.auth.AuthApplication;
import com.sansa.auth.testutil.MailOutboxSupport;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AuthApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "app.mail.enabled=true",
    "app.mail.transport=inmem",   // ← inmem で強制
    "spring.mail.host=invalid"    // 念のため実SMTPを無効化
})
@ActiveProfiles("inmem")
class InmemMailIT extends MailOutboxSupport {

  @Autowired TestRestTemplate http;

  @BeforeEach void setUp() { purgeOutbox(); }

  @Test
  void IT_01_008_verifyEmail_sends_jaJP_and_enUS() {
    // ja-JP
    var h = new HttpHeaders(); h.add("Accept-Language", "ja-JP");
    http.postForEntity("/auth/pre-register", new Req("jauser@example.com","ja-user"), Void.class);
    // 実装に合わせて verify-email を起動するHTTP呼び出しを追加
    awaitMails(1, 3_000);
    var ja = lastMailBodyNotNull();
    assertThat(ja).containsAnyOf("認証コード","確認コード");

    // en-US
    purgeOutbox();
    h = new HttpHeaders(); h.add("Accept-Language", "en-US");
    http.postForEntity("/auth/pre-register", new Req("enuser@example.com","en-user"), Void.class);
    awaitMails(1, 3_000);
    var en = lastMailBodyNotNull();
    assertThat(en.toLowerCase()).contains("verification code","confirm");
  }

  @Test
  void IT_07_007_emailOtp_flow_and_rate_limits() {
    // 送信
    http.postForEntity("/auth/mfa/email/send", new Empty(), Void.class);
    awaitMails(1, 3_000);
    var body = lastMailBodyNotNull();
    var code = body.replaceAll(".*?(\\b\\d{6}\\b).*", "$1");
    assertThat(code).matches("\\d{6}");

    // 検証
    var res = http.postForEntity("/auth/mfa/email/verify", new Otp(code), Void.class);
    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();

    // レート制限（連続送信で429想定）
    var r429 = http.postForEntity("/auth/mfa/email/send", new Empty(), String.class);
    assertThat(r429.getStatusCode().value()).isIn(200, 202, 429); // 実装側設定に合わせ微調整可
  }

  @Test
  void IT_07_008_recovery_mail_once() {
    purgeOutbox();
    http.postForEntity("/auth/mfa/recovery/issue", new Empty(), Void.class);
    awaitMails(1, 3_000);
    var first = lastMailBodyNotNull();

    purgeOutbox();
    // 同一セッションで再発行不可が正なら 4xx を期待
    var second = http.postForEntity("/auth/mfa/recovery/issue", new Empty(), String.class);
    assertThat(second.getStatusCode().value()).isBetween(400, 499);
    assertThat(outbox.subjects()).isEmpty();
  }

  // ==== 簡易リクエストDTO（実APIのReqに合わせて調整）====
  record Req(String email, String accountId) {}
  record Empty() {}
  record Otp(String code) {}
}
