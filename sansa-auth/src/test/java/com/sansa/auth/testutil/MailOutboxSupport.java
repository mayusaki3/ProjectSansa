package com.sansa.auth.testutil;

import com.sansa.auth.mail.MailMessage;
import java.time.Duration;
import java.util.List;

/**
 * テスト補助:
 * - outbox のクリア/待機/最後の本文取得 などを統一APIで提供
 * - 継承される想定があるため public + 引数なしコンストラクタも用意
 */
public class MailOutboxSupport {
    protected InmemOutboxMailSender outbox;

    public MailOutboxSupport() { /* for extends */ }

    public MailOutboxSupport(InmemOutboxMailSender outbox) {
        this.outbox = outbox;
    }

    public void setOutbox(InmemOutboxMailSender outbox) {
        this.outbox = outbox;
    }

    /** 旧 clear()/purgeOutbox() の吸収 */
    public void purgeOutbox() {
        if (outbox != null) outbox.clear();
    }

    /** 期待通数まで待機 */
    public void awaitMails(int expected, int timeoutMillis) throws InterruptedException {
        long until = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < until) {
            if (outbox != null && outbox.size() >= expected) return;
            Thread.sleep(50);
        }
        // 最後に一度確認
        if (outbox == null || outbox.size() < expected) {
            throw new AssertionError("mail not arrived: expected=" + expected + " actual=" + (outbox == null ? -1 : outbox.size()));
        }
    }

    /** 最後の本文（null不可） */
    public String lastMailBodyNotNull() {
        if (outbox == null) throw new IllegalStateException("outbox is null");
        MailMessage last = outbox.last();
        if (last == null || last.body() == null) {
            throw new AssertionError("last mail body is null");
        }
        return last.body();
    }

    /** スナップショット（テストで比較用） */
    public List<MailMessage> snapshot() {
        return outbox != null ? outbox.snapshot() : List.of();
    }
}
