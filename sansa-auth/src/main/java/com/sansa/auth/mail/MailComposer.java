package com.sansa.auth.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 多言語テンプレートの組み立てヘルパ。
 * - MessageSource（messages_xx.properties）から件名/本文を引き当ててMailMessageを生成
 * - 呼び出し側で builder を直接使ってもよいが、文言キー管理を集中したい場合に利用
 *
 * 例:
 *   var mail = composer.verifyEmail(to, code, ttlMinutes);
 *   mailService.send(mail);
 */
@Component
@RequiredArgsConstructor
public class MailComposer {

    private final MessageSource messageSource;

    public MailMessage verifyEmail(String to, String code, int ttlMinutes) {
        var locale = LocaleContextHolder.getLocale();
        var subject = msg("mail.verify.subject", locale);
        var body    = msg("mail.verify.body", locale, code, ttlMinutes);
        return MailMessage.builder().to(to).subject(subject).body(body).build();
    }

    public MailMessage emailOtp(String to, String code, int ttlMinutes) {
        var locale = LocaleContextHolder.getLocale();
        var subject = msg("mail.otp.subject", locale);
        var body    = msg("mail.otp.body", locale, code, ttlMinutes);
        return MailMessage.builder().to(to).subject(subject).body(body).build();
    }

    /**
     * リカバリーコード発行時の通知（※本文にコード本体は含めない：仕様上“一度きり表示”）
     */
    public MailMessage recoveryIssuedNotice(String to) {
        var locale = LocaleContextHolder.getLocale();
        var subject = msg("mail.recovery.subject", locale);
        var body    = msg("mail.recovery.notice", locale);
        return MailMessage.builder().to(to).subject(subject).body(body).build();
    }

    private String msg(String key, java.util.Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
