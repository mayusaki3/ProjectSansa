package com.sansa.auth.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * TestMailConfig
 * - IT で in-memory メール送受信を使うための最小設定。
 * - InMemoryMailService は no-args CTOR 前提とし、ここでは単純に new して返す。
 * - 送信文面の検証は IT で outbox スナップショットから行う（MailComposer に直接依存しない）。
 */
@TestConfiguration
@Profile("it")
public class TestMailConfig {

    @Bean
    @Primary
    public InMemoryMailService inMemoryMailService() {
        return new InMemoryMailService(); // ← コンストラクタ引数なしに統一
    }

    // 必要なら Outbox 実装や MessageSource の Test 用 Bean を追加
}
