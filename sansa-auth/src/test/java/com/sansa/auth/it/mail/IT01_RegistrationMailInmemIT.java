// 差し替え版（ポイント抜粋、コメント付き）
package com.sansa.auth.it.mail;

import com.sansa.auth.mail.InmemOutboxMailSender;
import com.sansa.auth.testutil.MailOutboxSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IT01_RegistrationMailInmemIT {

    private InmemOutboxMailSender outbox;
    private MailOutboxSupport support;

    @BeforeEach
    void setup() {
        // 実際の取得方法に合わせて初期化してください（DI / TestConfig など）
        // outbox = ...
        support = new MailOutboxSupport(outbox);
        support.purgeOutbox();
    }

    /** IT-01-003/008 相当: pre-register → verify-email コード受領 → 検証 */
    @Test
    void preRegister_and_verifyEmail_success() {
        // --- pre-register リクエスト（no-args + setter）
        PreRegisterReq req = new PreRegisterReq();
        req.setEmail("user1@example.com");
        req.setLanguage("ja-JP");
        // POST /auth/pre-register ...（送信は既存ユーティリティ/MockMvc/RestAssured等に合わせて）

        // --- メール到着待ち
        support.awaitMails(1, 5000);

        // --- 直近本文から 6 桁コード抽出
        String body = support.lastMailBodyNotNull();
        String code = body.replaceAll("(?s).*?\\b(\\d{6})\\b.*", "$1");
        assertThat(code).matches("\\d{6}");

        // --- /auth/verify-email 実行 -> 200 & preRegId
        // POST /auth/verify-email { email, code } -> preRegId を受領
        // 以後 /auth/register へ（詳細は既存手順に委ねる）
    }

    // 内部 DTO: all-args でなく no-args + setter を前提にする
    static class PreRegisterReq {
        private String email;
        private String language;
        public PreRegisterReq() {}
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
}
