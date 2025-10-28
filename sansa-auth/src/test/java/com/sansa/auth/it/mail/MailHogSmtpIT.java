package com.sansa.auth.it.mail;

import com.sansa.auth.mail.MailComposer;
import com.sansa.auth.mail.MailMessage;
import com.sansa.auth.mail.SmtpMailService;
import com.sansa.auth.testutil.MailAssertions;
import com.sansa.auth.testutil.MailHogClient;
import org.junit.jupiter.api.*;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IT：MailHog(HTTP:8025/SMTP:1025)が起動している前提。
 * - ja-JP / en-US で件名・本文テンプレートが正しく送られること
 * - 本文から6桁コードを抽出できること
 *
 * 実行前に docker 例：
 *   docker run -d --name mailhog -p 8025:8025 -p 1025:1025 mailhog/mailhog
 */
public class MailHogSmtpIT {

    private static SmtpMailService smtp;
    private static MailHogClient mailhog;
    private static MailComposer composer;

    @BeforeAll
    static void boot() throws Exception {
        // JavaMailSender を直接生成（SpringContextに依存しない）
        var sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        sender.setPort(1025); // MailHog SMTP
        sender.setDefaultEncoding("UTF-8");
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");

        smtp = new SmtpMailService(sender);
        mailhog = new MailHogClient("http://localhost:8025");

        // MessageSource（resources/messages_*.properties を使用）
        var ms = new ResourceBundleMessageSource();
        ms.setBasenames("messages");
        ms.setDefaultEncoding("UTF-8");
        composer = new MailComposer(ms);

        // 受信ボックスをクリア
        mailhog.purge();
    }

    @Test
    void verifyEmail_ja_and_en() throws Exception {
        // === ja-JP ===
        Locale.setDefault(Locale.JAPAN);
        var ja = composer.verifyEmail("ja@example.com", "123456", 15);
        smtp.send(ja);

        // === en-US ===
        Locale.setDefault(Locale.US);
        var en = composer.verifyEmail("en@example.com", "654321", 15);
        smtp.send(en);

        // 2件届くまで待機
        mailhog.waitUntilSubjectsAtLeast(2, Duration.ofSeconds(10), Duration.ofMillis(300));

        // 件名チェック（順不同の可能性を加味して両方確認）
        var subjects = mailhog.subjects(10);
        assertTrue(subjects.stream().anyMatch(s -> s.contains("メールアドレスの確認コード")));
        assertTrue(subjects.stream().anyMatch(s -> s.toLowerCase().contains("email verification code")));

        // 本文のどちらかから6桁抽出（簡易）
        var body = mailhog.lastBody();
        assertNotNull(body);
        String code = MailAssertions.extract6DigitCode(body);
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    void emailOtp_ja() throws Exception {
        Locale.setDefault(Locale.JAPAN);
        var msg = composer.emailOtp("user@example.com", "000111", 10);
        smtp.send(msg);

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(200));
        var subjects = mailhog.subjects(10);
        assertTrue(subjects.stream().anyMatch(s -> s.contains("ワンタイムパスコード")));

        var body = mailhog.lastBody();
        assertTrue(body.contains("000111"));
    }

    @Test
    void recoveryNotice_en() throws Exception {
        Locale.setDefault(Locale.US);
        var msg = composer.recoveryIssuedNotice("user@example.com");
        smtp.send(msg);

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(200));
        var subjects = mailhog.subjects(10);
        assertTrue(subjects.stream().anyMatch(s -> s.toLowerCase().contains("recovery codes issued")));

        var body = mailhog.lastBody().toLowerCase();
        assertTrue(body.contains("not included")); // コード不掲載の注意
    }
}
