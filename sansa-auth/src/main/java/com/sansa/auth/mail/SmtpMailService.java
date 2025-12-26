package com.sansa.auth.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * SmtpMailService
 * - 本番メール送信実装
 * - Profile=prod のときのみ有効化する（IT/inmem では MailService が一意になるようにする）
 */
@Service
@Profile("prod")
public class SmtpMailService implements MailService {

    private final MailSender sender;

    public SmtpMailService(MailSender sender) {
        this.sender = sender;
    }

    @Override
    public void send(MailMessage msg) {
        sender.send(msg);
    }
}
