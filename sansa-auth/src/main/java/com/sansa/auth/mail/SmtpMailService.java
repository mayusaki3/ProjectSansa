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

        /*
        * Spring の SimpleMailMessage#setTo は String / String... のみ。
        * MailMessage#getTo が List<String> 想定のため配列に変換する。
        */
        var toList = message.getTo();
        if (toList == null || toList.isEmpty()) {
            throw new IllegalArgumentException("mail to must not be empty");
        }
        m.setTo(toList.toArray(new String[0]));

        m.setSubject(message.getSubject());
        m.setText(message.getBody());

        sender.send(m);
    }
}
