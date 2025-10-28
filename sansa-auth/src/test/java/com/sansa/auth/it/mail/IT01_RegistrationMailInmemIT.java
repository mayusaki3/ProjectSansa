package com.sansa.auth.it.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sansa.auth.testutil.InMemoryMailOutbox;
import com.sansa.auth.testutil.MailMessage;
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
 * inmem環境で、実際のテンプレート結果（件名/本文/コード/言語）をOutboxで検証するIT。
 *
 * 注意：
 *  - まだ inmem Mailer -> Outbox の配線が無い場合、@Disabled を外すと失敗します。
 *  - 配線が終わり次第 @Disabled を削除してください。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inmem")
@DisplayName("IT01 (inmem) Verify-Email メール内容")
@Disabled("inmemメール配線(Outbox連携)が整い次第、外して実行してください")
class IT01_RegistrationMailInmemIT {

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
    static class PreRegisterReq {
        String email;
        String language; // 実装側でAccept-Languageを使うなら不要
    }

    @Test
    @DisplayName("ja-JP: 件名/本文/6桁コード/宛先/言語を検証")
    void preRegister_jaJP_mailContent() throws Exception {
        // 1) pre-register（日本語）
        var req = new PreRegisterReq("alice@example.jp", "ja-JP");
        mockMvc.perform(
                post("/auth/pre-register")
                        .header("Accept-Language", "ja-JP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
        ).andReturn();

        // 2) Outboxで受信待ち（非同期対策）
        outbox.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(100));

        // 3) 検証
        var last = outbox.last();
        assertThat(last).isNotNull();
        assertThat(last.getTo()).contains("alice@example.jp");
        assertThat(last.getLocale()).isIn("ja", "ja-JP"); // 実装に合わせて調整
        assertThat(last.getSubject()).contains("メール確認"); // i18nの件名
        assertThat(InMemoryMailOutbox.containsSixDigits(last.getBody())).isTrue();
    }

    @Test
    @DisplayName("en-US: 件名/本文/6桁コード/宛先/言語を検証")
    void preRegister_enUS_mailContent() throws Exception {
        // 1) pre-register（英語）
        var req = new PreRegisterReq("bob@example.com", "en-US");
        mockMvc.perform(
                post("/auth/pre-register")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
        ).andReturn();

        // 2) Outboxで受信待ち
        outbox.waitUntilSubjectsAtLeast(1, Duration.ofSeconds(5), Duration.ofMillis(100));

        // 3) 検証
        var last = outbox.last();
        assertThat(last).isNotNull();
        assertThat(last.getTo()).contains("bob@example.com");
        assertThat(last.getLocale()).isIn("en", "en-US");
        assertThat(last.getSubject().toLowerCase()).contains("verify your email"); // i18nの件名
        assertThat(InMemoryMailOutbox.containsSixDigits(last.getBody())).isTrue();
    }
}
