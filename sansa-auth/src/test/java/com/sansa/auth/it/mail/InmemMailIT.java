package com.sansa.auth.it.mail;

import com.sansa.auth.testutil.InmemOutboxMailSender; // ← ラッパ
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 既存 InmemMailIT の最小疎通版。
 *
 * - 以前: MailOutboxSupport の private ctor / final で継承不可などの問題があった。
 * - 今回: 変数は testutil.InmemOutboxMailSender とし、実体は ctor に渡すだけ。
 */
public class InmemMailIT {

    private InmemOutboxMailSender outbox;

    @BeforeEach
    void setUp() {
        com.sansa.auth.mail.InmemOutboxMailSender real =
                new com.sansa.auth.mail.InmemOutboxMailSender();
        this.outbox = new InmemOutboxMailSender(real);
        outbox.clear();
    }

    @Test
    void smoke_last_and_size() {
        assertEquals(0, outbox.size());
        assertNull(outbox.last(), "メール未送信なら last() は null の想定（実装によっては変更可）");
    }
}
