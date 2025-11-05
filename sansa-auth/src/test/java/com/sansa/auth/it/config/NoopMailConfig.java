package com.sansa.auth.it.config;

import java.util.Properties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * IT（結合テスト）用：メール送信のダミー実装。
 *
 * 目的：
 * - inmem + it プロファイルで SpringBootTest を起動した際、
 *   SmtpMailService が要求する JavaMailSender を解決し、送信は何もしない（NOOP）。
 *
 * ポイント：
 * - @TestConfiguration : テスト実行時に限定して登録される Bean 定義。
 * - @Profile("it")     : it プロファイル時のみ有効（= UT には影響しない）。
 * - @Primary           : 既存の JavaMailSender があっても本 Bean を優先。
 */
@TestConfiguration
@Profile({"it"})
public class NoopMailConfig {

    /** JavaMailSender の最小限ダミー。send(...) は NOOP。 */
    @Bean
    @Primary
    public JavaMailSender noopJavaMailSender() {
        return new JavaMailSender() {

            @Override
            public MimeMessage createMimeMessage() {
                // 空の Session でダミー MimeMessage を生成
                return new MimeMessage(Session.getInstance(new Properties()));
            }

            @Override
            public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
                try {
                    return new MimeMessage(Session.getInstance(new Properties()), contentStream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // 送信系はすべて NOOP（例外を投げない）
            @Override public void send(MimeMessage mimeMessage) {}
            @Override public void send(MimeMessage... mimeMessages) {}
            @Override public void send(SimpleMailMessage simpleMessage) {}
            @Override public void send(SimpleMailMessage... simpleMessages) {}
        };
    }
}
