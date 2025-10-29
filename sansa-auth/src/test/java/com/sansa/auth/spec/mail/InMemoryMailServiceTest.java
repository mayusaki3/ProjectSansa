package com.sansa.auth.spec.mail;

import com.sansa.auth.testutil.InmemOutboxMailSender;
import com.sansa.auth.testutil.MailOutboxSupport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 仕様テスト：Outboxへ記録されることのみを in-mem で確認。
 * service.clearOutbox()/snapshot() は使用せず、Support 経由に統一。
 */
@SpringBootTest(properties = {
    "app.mail.enabled=true",
    "app.mail.transport=inmem",
    "sansa.auth.mode=inmem"
})
class InMemoryMailServiceTest {

  @Autowired InmemOutboxMailSender outbox;
  MailOutboxSupport support;

  @BeforeEach
  void init() {
    support = new MailOutboxSupport(outbox);
    support.purgeOutbox();
  }

  @Test
  void sendsIntoOutbox() {
    // act: 送信のトリガ（ユースケース/REST）を呼ぶ（省略）
    support.awaitMails(1, 10);
    Assertions.assertFalse(support.snapshot().isEmpty());
  }
}
