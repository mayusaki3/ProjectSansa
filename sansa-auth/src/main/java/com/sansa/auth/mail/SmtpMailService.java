package com.sansa.auth.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * cassandra プロファイル向けのSMTP送信実装。
 * - Spring Boot の JavaMailSender オートコンフィグに依存
 * - application-cassandra.yml で spring.mail.host/port を必ず指定
 * - MailHog 利用時は host=localhost, port=1025 が一般的
 */
@Slf4j
@Service
@Profile("cassandra")
@RequiredArgsConstructor
public class SmtpMailService implements MailService {

    private final JavaMailSender sender;

    @Override
    public void send(MailMessage msg) {
        var m = new SimpleMailMessage();
        m.setTo(msg.to());
        m.setSubject(msg.subject());
        m.setText(msg.body());
        sender.send(m);
        log.debug("SMTP sent: to={}, subject='{}'", msg.to(), msg.subject());
    }
}
