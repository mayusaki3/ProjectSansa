package com.sansa.auth.testsupport;

import com.sansa.auth.testutil.InMemoryMailOutbox;
import com.sansa.auth.testutil.MailMessage;
import java.time.Instant;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 暫定の送信フック。
 * - 本番コードの“メール送信”箇所からこの send(...) を呼べばOutboxに積まれる
 * - 本配線完了後は削除可能
 */
@TestConfiguration
@RequiredArgsConstructor
public class InmemMailTestConfig {

    private final InMemoryMailOutbox outbox;

    public interface TestMailSenderHook {
        void send(String to, String subject, String body, String locale);
    }

    @Bean
    public TestMailSenderHook testMailSenderHook() {
        return (to, subject, body, locale) -> outbox.append(
                MailMessage.builder()
                        .to(List.of(to))
                        .subject(subject)
                        .body(body)
                        .locale(locale)
                        .createdAt(Instant.now())
                        .build()
        );
    }
}
