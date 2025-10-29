package com.sansa.auth.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 実SMTP配信用のサービス実装。
 * - JavaMailSender をDIし、MailMessageからSimpleMailMessageへ写像。
 * - getTo()/getCc()/getBcc() は null/空を許容して安全に反映。
 */
@Service
@RequiredArgsConstructor
public class SmtpMailService implements MailService {

    private final JavaMailSender javaMailSender; // ← これが未定義でエラーになっていた

    @Override
    public void send(MailMessage msg) {
        SimpleMailMessage m = new SimpleMailMessage();

        // From（未指定なら application-*.yml の spring.mail.username などにフォールバック）
        if (msg.getFrom() != null) {
            m.setFrom(msg.getFrom());
        }

        // 宛先系を安全に反映
        setIfPresentTo(m, msg.getTo());
        setIfPresentCc(m, msg.getCc());
        setIfPresentBcc(m, msg.getBcc());

        m.setSubject(msg.getSubject());
        m.setText(msg.getBody());

        javaMailSender.send(m);
    }

    private static void setIfPresentTo(SimpleMailMessage m, @Nullable List<String> addrs) {
        if (addrs != null && !addrs.isEmpty()) {
            m.setTo(addrs.toArray(new String[0]));  // ← 可変長String...に変換
        }
    }

    private static void setIfPresentCc(SimpleMailMessage m, @Nullable List<String> addrs) {
        if (addrs != null && !addrs.isEmpty()) {
            m.setCc(addrs.toArray(new String[0]));
        }
    }

    private static void setIfPresentBcc(SimpleMailMessage m, @Nullable List<String> addrs) {
        if (addrs != null && !addrs.isEmpty()) {
            m.setBcc(addrs.toArray(new String[0]));
        }
    }
}
