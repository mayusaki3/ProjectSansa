package com.sansa.auth.mail;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * テスト用 Mail 設定。
 *
 * 仕様（Profile と Bean 一意性規約）により、MailService の Bean 定義は main 側で Profile 排他に統一する。
 * - SmtpMailService : prod のみ（main）
 * - InMemoryMailService : test/it/inmem のみ（main）
 *
 * そのため、本 TestConfiguration では MailService を定義しない。
 * （MailService を @Bean で定義すると main 側の InMemoryMailService と二重化し、規約違反になり得る）
 *
 * ※このファイルは「テスト専用の補助 Bean」が必要になった場合だけ使う。
 *   現状不要なら削除してよい。
 */
@TestConfiguration
@Profile({ "test", "it" })
public class TestMailConfig {

    /**
     * 例：テスト側で MailSender を差し替えたい場合の拡張点。
     * 現時点の MailService 設計では不要なら削除してよい。
     */
    @Bean
    public MailSender noopMailSender() {
        return message -> {
            // 何もしない（将来のテスト拡張用）
        };
    }
}
