package com.sansa.auth.testutil;

import com.sansa.auth.mail.InmemOutboxMailSender;

/**
 * テストから Outbox を扱うための簡易サポート。
 * 以前 main 側に置いていた Inmem 実装を test スコープに移したため、
 * ここでは test 配下の InmemOutboxMailSender を参照する。
 */
public final class MailOutboxSupport {

    private static final InmemOutboxMailSender OUTBOX = new InmemOutboxMailSender();

    private MailOutboxSupport() {}

    public static InmemOutboxMailSender outbox() {
        return OUTBOX;
    }

    public static void purge() {
        OUTBOX.purge();
    }
}
