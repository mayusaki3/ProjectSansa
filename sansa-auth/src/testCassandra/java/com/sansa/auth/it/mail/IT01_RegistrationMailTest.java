package com.sansa.auth.it.mail;

import com.sansa.auth.testsupport.CassandraITBootstrap;
import com.sansa.auth.testutil.MailHogClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("cassandra")
class IT01_RegistrationMailTest extends CassandraITBootstrap {

    @Autowired
    MockMvc mvc;

    // MailHog API のベースURLは application-cassandra.yml の mailhog.api-url に合わせる
    MailHogClient mailhog = new MailHogClient(System.getProperty("mailhog.apiUrl", "http://localhost:8025/api"));

    @BeforeEach
    void clearMails() {
        mailhog.purge();
    }

    @Test
    @DisplayName("IT-01-008: verify-email 送信 → 件名/本文/コード検証（ja-JP / en-US）")
    void verifyEmailMail_ja_en() throws Exception {
        // --- ja-JP で pre-register 実行 → メール送信を待つ ---
        mvc.perform(post("/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja-JP")
                .content("""
                        {"email":"user-ja@example.com","accountId":"userja","language":"ja-JP"}
                        """))
            .andExpect(status().isAccepted());

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(20), Duration.ofMillis(250));
        var subjectsJa = mailhog.subjects(1);
        assertThat(subjectsJa.get(0)).containsAnyOf("メール認証", "認証コード", "メールアドレスの確認");

        var bodyJa = mailhog.lastBody();
        var codeJa = extract6Digits(bodyJa);
        assertThat(codeJa).isNotBlank();

        // verify-email 成功（コードが本文と整合すること）
        mvc.perform(post("/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja-JP")
                .content("""
                        {"accountId":"userja","email":"user-ja@example.com","code":"%s"}
                        """.formatted(codeJa)))
            .andExpect(status().isOk());

        // --- en-US で pre-register 実行 ---
        mailhog.purge();

        mvc.perform(post("/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en-US")
                .content("""
                        {"email":"user-en@example.com","accountId":"useren","language":"en-US"}
                        """))
            .andExpect(status().isAccepted());

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(20), Duration.ofMillis(250));
        var subjectsEn = mailhog.subjects(1);
        assertThat(subjectsEn.get(0).toLowerCase()).containsAnyOf("verify your email", "verification code");

        var bodyEn = mailhog.lastBody();
        var codeEn = extract6Digits(bodyEn);
        assertThat(codeEn).isNotBlank();

        mvc.perform(post("/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en-US")
                .content("""
                        {"accountId":"useren","email":"user-en@example.com","code":"%s"}
                        """.formatted(codeEn)))
            .andExpect(status().isOk());
    }

    // ブロックドメイン/レート超過時はメールが送信されないこと（エラーメール抑止）
    @Test
    @DisplayName("IT-01-008a: ブロックドメイン等 → メール未送信を確認")
    void noMailOnBlockedOrRateLimited() throws Exception {
        mailhog.purge();

        // ブロックドメイン想定
        mvc.perform(post("/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"user@blocked.example","accountId":"blkuser","language":"ja-JP"}
                        """))
            .andExpect(status().isBadRequest());

        // しばらく待っても 0 件のまま
        Thread.sleep(500); // 瞬間送信の揺れを避ける
        assertThat(mailhog.subjects(0)).isEmpty();
    }

    private static String extract6Digits(String body) {
        Pattern p = Pattern.compile("\\b(\\d{6})\\b");
        Matcher m = p.matcher(body);
        return m.find() ? m.group(1) : "";
    }
}
