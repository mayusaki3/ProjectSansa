package com.sansa.auth.spec.mail;

import com.sansa.auth.mail.InMemoryMailService;
import com.sansa.auth.mail.MailComposer;
import com.sansa.auth.mail.MailMessage;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UT：inmem プロファイルが不要な純粋ユニットテスト。
 * - OutBox へ格納されること
 * - ローカライズ文面が取り出せること（Locale切替は MessageSource 依存だが、ここではDTOのみ検証）
 */
public class InMemoryMailServiceTest {

    @BeforeEach
    void setup() { InMemoryMailService.clearOutbox(); }

    @Test
    void appendOutbox() {
        var svc = new InMemoryMailService();
        var msg = MailMessage.builder()
                .to("user@example.com")
                .subject("件名テスト")
                .body("本文テスト")
                .build();
        svc.send(msg);

        var box = InMemoryMailService.snapshot();
        assertEquals(1, box.size());
        assertEquals("user@example.com", box.get(0).to());
        assertEquals("件名テスト", box.get(0).subject());
    }

    @Test
    void dtoValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> MailMessage.builder().to("").subject("x").body("y").build());
        assertThrows(IllegalArgumentException.class,
                () -> MailMessage.builder().to("a@b").subject("").body("y").build());
        assertThrows(IllegalArgumentException.class,
                () -> MailMessage.builder().to("a@b").subject("x").body("").build());
    }
}
