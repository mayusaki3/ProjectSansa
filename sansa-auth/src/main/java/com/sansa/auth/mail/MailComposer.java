package com.sansa.auth.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 用途別に MailMessage を組み立てる責務。
 * - 宛先は builder の単数Adder（@Singular）で追加する（to("...")）。
 * - 既定Fromは外部構成（application-*.yml 等）側で MailService が補完しても良い。
 */
@Component
@RequiredArgsConstructor
public class MailComposer {

    /**
     * メールアドレス検証（verify-email）コード送信用
     */
    public MailMessage composeVerifyEmail(String to, String code, Locale locale) {
        final String subject = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "メールアドレスの確認コード"
                : "Your email verification code";

        final String body = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "確認コード: " + code + "\n15分以内に入力してください。"
                : "Your verification code: " + code + "\nPlease enter within 15 minutes.";

        return MailMessage.builder()
                .to(to)                 // @Singular により単数追加OK
                .subject(subject)
                .body(body)
                .locale(locale)
                .build();
    }

    /**
     * Email OTP 送信用
     */
    public MailMessage composeEmailOtp(String to, String otp, Locale locale) {
        final String subject = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "ワンタイムパスコード"
                : "One-Time Passcode";

        final String body = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "ワンタイムパスコード: " + otp + "\n5分以内に入力してください。"
                : "Your one-time passcode: " + otp + "\nPlease enter within 5 minutes.";

        return MailMessage.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .locale(locale)
                .build();
    }

    /**
     * リカバリーコード送信用（表示は一度きり想定）
     */
    public MailMessage composeRecoveryCodes(String to, String codesBlock, Locale locale) {
        final String subject = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "リカバリーコードのご案内（大切に保管してください）"
                : "Your recovery codes (store securely)";

        final String body = (Locale.JAPANESE.getLanguage().equals(locale.getLanguage()))
                ? "以下のリカバリーコードは一度しか表示されません。安全な場所に保管してください。\n\n" + codesBlock
                : "The following recovery codes are shown only once. Store them securely.\n\n" + codesBlock;

        return MailMessage.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .locale(locale)
                .build();
    }
}
