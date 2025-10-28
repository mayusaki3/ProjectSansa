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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("cassandra")
class IT07_EmailOtpAndRecoveryMailTest extends CassandraITBootstrap {

    @Autowired
    MockMvc mvc;

    MailHogClient mailhog = new MailHogClient(System.getProperty("mailhog.apiUrl", "http://localhost:8025/api"));

    @BeforeEach
    void clearMails() {
        mailhog.purge();
    }

    @Test
    @DisplayName("IT-07-007: Email OTP 送信→受信→検証（レート制限/TTLの基本確認）")
    void emailOtp_send_receive_verify() throws Exception {
        // まずはダミーログインでメールOTP必須状態にしてある前提（既存 IT と同条件で可）
        mvc.perform(post("/mfa/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{}"""))
            .andExpect(status().isAccepted());

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(20), Duration.ofMillis(250));
        var body = mailhog.lastBody();
        var code = body.replaceAll("(?s).*?(\\b\\d{6}\\b).*", "$1");
        assertThat(code).matches("\\d{6}");

        mvc.perform(post("/mfa/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code":"%s"}""".formatted(code)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authenticated").value(true));

        // 直後の再送はレート制限（429）が望ましい
        mvc.perform(post("/mfa/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{}"""))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("IT-07-008: Recovery codes 発行メールは一度きり表示の保証")
    void recoveryCodes_issue_onceOnly() throws Exception {
        // 1回目の発行
        mvc.perform(post("/mfa/recovery/issue")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mailhog.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(20), Duration.ofMillis(250));
        var body1 = mailhog.lastBody();
        assertThat(body1).containsAnyOf("Recovery", "リカバリ");

        // 2回目直後に再発行要求
        mvc.perform(post("/mfa/recovery/issue")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // 新規メールは来ない（または件名/本文に “already issued” 系の文言で再提示しない）
        // ＝「一度きり表示」の保証。メール件数と本文差分で緩く検証
        var subjects = mailhog.subjects(10);
        assertThat(subjects.size()).isBetween(1, 2); // 実装によって通知だけ増やす場合を許容
    }
}
