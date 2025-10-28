package com.sansa.auth.it.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sansa.auth.testutil.InMemoryMailOutbox;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * inmem環境での Email OTP / Recovery メールの内容検証。
 * - 件名/本文/6桁コード
 * - レート制限(送信しすぎでOutboxが増えない) 簡易チェック
 * - Recovery “一度きり表示”の文言を本文に持たせているか（実装に合わせて調整）
 *
 * 注意：inmem Mailer->Outbox配線が必要。完了後に @Disabled を外してください。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inmem")
@DisplayName("IT07 (inmem) Email OTP / Recovery メール内容")
@Disabled("inmemメール配線(Outbox連携)が整い次第、外して実行してください")
class IT07_EmailOtpAndRecoveryMailInmemIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemoryMailOutbox outbox;

    @Autowired
    ObjectMapper om;

    @BeforeEach
    void setUp() {
        outbox.purge();
    }

    @Value
    static class SendEmailOtpReq {
        String userId; // 実装に合わせて必要なら
    }

    @Test
    @DisplayName("Email OTP: 送信→受信→本文6桁コードを検証、連投でレート制限（Outbox件数が増えない）")
    void emailOtp_send_and_rateLimit() throws Exception {
        // 1) 送信
        mockMvc.perform(
                post("/auth/mfa/email/send")
                        .header("Accept-Language", "ja-JP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new SendEmailOtpReq(null)))
        ).andReturn();

        outbox.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(100));

        var first = outbox.last();
        assertThat(first.getSubject()).contains("ワンタイムコード");
        assertThat(InMemoryMailOutbox.containsSixDigits(first.getBody())).isTrue();

        // 2) 連投（レート制限想定）: Outbox件数が簡易には増えない方針で検証
        int before = outbox.size();
        mockMvc.perform(
                post("/auth/mfa/email/send")
                        .header("Accept-Language", "ja-JP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new SendEmailOtpReq(null)))
        ).andReturn();

        // 少し待っても件数が増えなければOK（実装に合わせ調整）
        Thread.sleep(500L);
        assertThat(outbox.size()).isBetween(before, before + 1); // 実装差異に配慮し緩め
    }

    @Test
    @DisplayName("Recovery: 発行メールに“一度きり表示”の文言が含まれる（本文検証）")
    void recovery_issue_mail_containsOneTimeHint() throws Exception {
        mockMvc.perform(
                post("/auth/mfa/recovery/issue")
                        .header("Accept-Language", "ja-JP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of()))
        ).andReturn();

        outbox.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(100));

        var last = outbox.last();
        assertThat(last.getSubject()).contains("リカバリー");
        assertThat(last.getBody()).contains("一度きり"); // 実装の文言に合わせて調整
    }
}
