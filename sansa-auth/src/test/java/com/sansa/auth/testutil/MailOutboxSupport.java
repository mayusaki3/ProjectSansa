package com.sansa.auth.testutil;

import com.sansa.auth.mail.InmemOutboxMailSender;
import com.sansa.auth.mail.MailMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * MailOutboxSupport
 * - IT から合成(composition)で利用するメール受信トレイ検証ユーティリティ。
 * - 以前の final / private CTOR を廃止し、public CTOR で outbox を受け取る。
 * - IT 側で呼んでいるユーティリティ・メソッド（purgeOutbox/awaitMails/lastMailBodyNotNull）を提供。
 */
public class MailOutboxSupport {

    private final InmemOutboxMailSender outbox;

    /** IT から new できるよう public CTOR にする */
    public MailOutboxSupport(InmemOutboxMailSender outbox) {
        this.outbox = Objects.requireNonNull(outbox, "outbox");
    }

    /** 受信箱クリア */
    public void purgeOutbox() {
        outbox.clear();
    }

    /**
     * 指定数に達するまでポーリング待機
     * @param expected 最低期待通数
     * @param timeoutMs タイムアウト(ms)
     */
    public void awaitMails(int expected, int timeoutMs) {
        final Instant end = Instant.now().plusMillis(timeoutMs);
        while (Instant.now().isBefore(end)) {
            if (outboxSnapshotSize() >= expected) return;
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        throw new AssertionError("awaitMails timeout: expected>=" + expected + " but got " + outboxSnapshotSize());
    }

    /** 直近本文（null なら失敗） */
    public String lastMailBodyNotNull() {
        MailMessage last = lastMessage();
        if (last == null || last.body() == null) {
            throw new AssertionError("last mail body is null");
        }
        return last.body();
    }

    /** 直近メッセージ（存在しなければ null） */
    public MailMessage lastMessage() {
        List<MailMessage> list = outbox.snapshot();
        if (list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    /** 件名件数（テストの簡易確認用） */
    public int outboxSnapshotSize() {
        return outbox.snapshot().size();
    }
}
