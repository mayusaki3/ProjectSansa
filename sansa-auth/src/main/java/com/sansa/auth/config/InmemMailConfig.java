package com.sansa.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;

/**
 * inmem プロファイル用のダミー JavaMailSender 設定。
 *
 * - 実際にはメールは送信しない。
 * - SmtpMailService からの依存解決だけを目的とした暫定実装。
 */
@Configuration
@Profile("inmem")
public class InmemMailConfig {

    private static final Logger log = LoggerFactory.getLogger(InmemMailConfig.class);

    /**
     * ダミーの JavaMailSender 実装を提供する。
     *
     * @return 実際には送信処理を行わない JavaMailSender
     */
    @Bean
    public JavaMailSender inmemJavaMailSender() {
        return new JavaMailSender() {

            @Override
            public MimeMessage createMimeMessage() {
                // null Session でも MimeMessage は生成可能
                return new MimeMessage((Session) null);
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) {
                try {
                    return new MimeMessage((Session) null, contentStream);
                } catch (MessagingException e) {
                    log.warn("Failed to create MimeMessage from InputStream (inmem stub).", e);
                    return new MimeMessage((Session) null);
                }
            }

            @Override
            public void send(MimeMessage mimeMessage) {
                // inmem 環境では送信せず、ログだけ出す
                log.info("[inmem] JavaMailSender.send(MimeMessage) called. No mail is actually sent.");
            }

            @Override
            public void send(MimeMessage... mimeMessages) {
                log.info("[inmem] JavaMailSender.send(MimeMessage...) called ({} messages). No mail is actually sent.",
                        mimeMessages != null ? mimeMessages.length : 0);
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) {
                log.info("[inmem] JavaMailSender.send(MimeMessagePreparator) called. No mail is actually sent.");
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) {
                log.info("[inmem] JavaMailSender.send(MimeMessagePreparator...) called ({} preparators). No mail is actually sent.",
                        mimeMessagePreparators != null ? mimeMessagePreparators.length : 0);
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) {
                log.info("[inmem] JavaMailSender.send(SimpleMailMessage) called. to={}, subject={}",
                        simpleMessage.getTo(), simpleMessage.getSubject());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) {
                log.info("[inmem] JavaMailSender.send(SimpleMailMessage...) called ({} messages).",
                        simpleMessages != null ? simpleMessages.length : 0);
            }
        };
    }
}
