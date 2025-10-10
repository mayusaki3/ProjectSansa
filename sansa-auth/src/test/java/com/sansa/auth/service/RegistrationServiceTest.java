package com.sansa.auth.service;

import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.service.error.InvalidCodeException;
import com.sansa.auth.service.store.InmemStore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("inmem")
class RegistrationServiceTest {

    @Autowired
    AuthService service;

    @Autowired
    InmemStore store; // ★ 発行コードを仕込むために使用

    @Test
    void verifyEmail_ok_returns_preRegId() {
        String email = "user@example.com";
        // まず同じメールでコードを発行
        String code = store.issueEmailCode(email);

        VerifyEmailRequest req = VerifyEmailRequest.builder()
            .email(email)
            .code(code)
            .build();

        VerifyEmailResponse res = service.verifyEmail(req);

        assertNotNull(res.getPreRegId());
        assertFalse(res.getPreRegId().isBlank());
    }

    @Test
    void verifyEmail_expired_or_invalid_code() {
        // 期限切れを厳密に再現できないなら「無効コード」で失敗させる
        String emailOriginal = "expired@example.com";
        String emailNormalized = normEmail(emailOriginal);
        // 参考：実コードを発行しておいて、わざと違うコードを投げる
        store.issueEmailCode(emailNormalized);

        VerifyEmailRequest bad = VerifyEmailRequest.builder()
                .email(emailOriginal)
                .code("WRONG-CODE")
                .build();

        assertThrows(InvalidCodeException.class, () -> service.verifyEmail(bad));
    }

    /**
     * テスト内の簡易メール正規化（本番の動きに揃えるための代替）。
     * - ローカル部の '+...' を除去
     * - ローカル部/ドメイン部ともに小文字
     * - 前後の空白をtrim
     */
    private static String normEmail(String email) {
        if (email == null) return null;
        String trimmed = email.trim();
        int at = trimmed.indexOf('@');
        if (at < 0) return trimmed.toLowerCase(); // 念のため
        String local = trimmed.substring(0, at);
        String domain = trimmed.substring(at + 1);
        int plus = local.indexOf('+');
        if (plus >= 0) local = local.substring(0, plus);
        return (local + "@" + domain).toLowerCase();
    }
}
