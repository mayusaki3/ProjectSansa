package com.sansa.auth.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * SMTP 送信（本番用）。
 *
 * Profile と Bean 一意性規約:
 * - 本クラスは "prod" のみで有効
 * - test/it/inmem では絶対にロードされない
 */
@Service
@Profile("prod")
public class SmtpMailService implements MailService {

    private final JavaMailSender sender;

    public SmtpMailService(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    public void send(MailMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }

        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(message.getTo());
        m.setSubject(message.getSubject());
        m.setText(message.getBody());

        sender.send(m);
    }
}
