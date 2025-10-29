// 差し替え版（ポイント抜粋、コメント付き）
package com.sansa.auth.it.mail;

import com.sansa.auth.mail.InmemOutboxMailSender;
import com.sansa.auth.testutil.MailOutboxSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IT07_EmailOtpAndRecoveryMailInmemIT {

    private InmemOutboxMailSender outbox;
    private MailOutboxSupport support;

    @BeforeEach
    void setup() {
        // outbox = ...
        support = new MailOutboxSupport(outbox);
        support.purgeOutbox();
    }

    /** IT-07-003/004/007 相当: 送信→受信→検証（レート制限等は別テストでヘッダ確認） */
    @Test
    void emailOtp_send_and_verify_flow() {
        // --- send リクエスト（no-args + setter）
        SendEmailOtpReq req = new SendEmailOtpReq();
        req.setEmail("user1@example.com");
        // POST /auth/mfa/email/send ...

        support.awaitMails(1, 5000);

        String body = support.lastMailBodyNotNull();
        String code = body.replaceAll("(?s).*?\\b(\\d{6})\\b.*", "$1");
        assertThat(code).matches("\\d{6}");

        // --- verify: 成功
        // POST /auth/mfa/email/verify { challengeId, code } -> 200

        // --- 失効/不正コード（別ケースで 400 を確認）
    }

    static class SendEmailOtpReq {
        private String email;
        public SendEmailOtpReq() {}
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
