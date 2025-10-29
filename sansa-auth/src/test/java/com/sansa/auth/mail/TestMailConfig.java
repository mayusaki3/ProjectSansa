// src/test/java/com/sansa/auth/testsupport/mail/TestMailConfig.java
package com.sansa.auth.testsupport.mail;

import com.sansa.auth.mail.InMemoryMailService;
import com.sansa.auth.mail.InmemOutboxMailSender;
import com.sansa.auth.mail.MailComposer;
import com.sansa.auth.mail.MailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * TestMailConfig
 *
 * 目的:
 *  - テスト時、実SMTPを使わず「メモリ内Outbox」にメールを書き出す MailService を提供する。
 *  - これにより、IT/UTで件名・本文・言語(ローカライズ結果)などを検証できる。
 *
 * 提供Bean:
 *  - InmemOutboxMailSender: 宛先/件名/本文を保存するだけの送信器（副作用なし）
 *  - MailService(@Primary): InMemoryMailService を最優先で注入し、SmtpMailService を上書きする
 *
 * 使い方:
 *  - テストクラスに @Import(TestMailConfig.class) を付与
 *  - もしくは共通のベースIT/UT構成に本クラスを取り込む
 *  - 検証は com.sansa.auth.testutil.MailOutboxSupport を用いて行う
 */
@TestConfiguration
public class TestMailConfig {

    /**
     * メモリ内の送信先(Outbox)へ蓄積する送信器。
     * テストからは MailOutboxSupport.outboxOf(ctx) で参照・purge可能。
     */
    @Bean
    public InmemOutboxMailSender inmemOutboxMailSender() {
        return new InmemOutboxMailSender();
    }

    /**
     * テスト用 MailService 実装。
     * @Primary により、アプリ本体の SmtpMailService より優先して注入される。
     * MailComposer は本体側の @Component をそのまま利用（テンプレート/ローカライズを統一検証）。
     */
    @Bean
    @Primary
    public MailService testMailService(InmemOutboxMailSender outbox, MailComposer composer) {
        return new InMemoryMailService(outbox, composer);
    }
}
