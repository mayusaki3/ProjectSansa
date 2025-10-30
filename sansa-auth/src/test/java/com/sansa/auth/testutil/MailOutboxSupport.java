package com.sansa.auth.testutil;

import com.sansa.auth.mail.MailMessage;
import java.lang.reflect.Method;
import java.util.List;

/**
 * テスト補助の共通基底。
 *
 * 役割:
 * - Outbox のクリア・到着待ち・最後の本文取得・スナップショット取得を
 *   一貫した API で提供する。
 * - 継承されることがあるため public + 引数なしコンストラクタを提供する。
 *
 * 注意:
 * - 以前の実装では private コンストラクタ / final 等のため継承不能エラーが発生していた。
 *   → 本版は継承可能に調整。
 */
public class MailOutboxSupport {

    /** ラッパー（実装差を吸収） */
    protected InmemOutboxMailSender outbox;

    public MailOutboxSupport() {
        // for subclassing
    }

    public MailOutboxSupport(InmemOutboxMailSender outbox) {
        this.outbox = outbox;
    }

    public void setOutbox(InmemOutboxMailSender outbox) {
        this.outbox = outbox;
    }

    /** 旧 clear()/purgeOutbox() を吸収 */
    public void purgeOutbox() {
        if (outbox != null) outbox.clear();
    }

    /** 期待通数まで受信を待機 */
    public void awaitMails(int expected, int timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (outbox != null && outbox.size() >= expected) return;
            Thread.sleep(50);
        }
        int actual = (outbox == null ? -1 : outbox.size());
        throw new AssertionError("mail not arrived in time: expected=" + expected + ", actual=" + actual);
    }

    /**
     * 最後の本文文字列（null禁止）
     *
     * 実装側の MailMessage が body() を持たないケースに備え、
     * getBody()/getText() も順に反射で試す。
     */
    public String lastMailBodyNotNull() {
        if (outbox == null) throw new IllegalStateException("outbox is null");
        MailMessage last = outbox.last();
        if (last == null) throw new AssertionError("last mail is null");

        // まずは標準想定: body()
        try {
            Method m = last.getClass().getMethod("body");
            m.setAccessible(true);
            Object v = m.invoke(last);
            if (v instanceof String s && s != null) return s;
        } catch (ReflectiveOperationException ignore) {
            // 次を試す
        }

        // 代替1: getBody()
        try {
            Method m = last.getClass().getMethod("getBody");
            m.setAccessible(true);
            Object v = m.invoke(last);
            if (v instanceof String s && s != null) return s;
        } catch (ReflectiveOperationException ignore) {
        }

        // 代替2: getText()
        try {
            Method m = last.getClass().getMethod("getText");
            m.setAccessible(true);
            Object v = m.invoke(last);
            if (v instanceof String s && s != null) return s;
        } catch (ReflectiveOperationException ignore) {
        }

        throw new AssertionError("last mail body is null or not accessible via body()/getBody()/getText()");
    }

    /** スナップショット（比較用） */
    public List<MailMessage> snapshot() {
        return (outbox != null) ? outbox.snapshot() : List.of();
    }
}
