package com.sansa.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sansa.auth.dto.mfa.MfaTotpEnrollResponse;
import com.sansa.auth.dto.mfa.MfaTotpVerifyRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.SessionInfo;

import java.util.List;

class MfaUtilTest {

    @Test
    @DisplayName("TOTP登録～検証フローのDTO形を確認（サービス呼び出しなし）")
    void totpDtoShape() {
        // enroll の戻り：ビルダーに challengeId()/provisioningUri() が無い想定なので、素の build のみ
        MfaTotpEnrollResponse enroll = MfaTotpEnrollResponse.builder().build();
        assertNotNull(enroll);

        // verify リクエストDTO（こちらは builder に challengeId/code がある前提。無い場合は削除してください）
        MfaTotpVerifyRequest verifyReq = MfaTotpVerifyRequest.builder()
                .challengeId("CH-123")
                .code("123456")
                .build();
        assertNotNull(verifyReq);

        // verify の想定レスポンス（多くの設計で LoginResponse を返す）
        LoginTokens tokens = LoginTokens.builder()
                .accessToken("acc-after-mfa")
                .refreshToken("ref-after-mfa")
                .build();

        // UserSummary: accountId(...) は存在しない想定のため使わない
        SessionInfo session = SessionInfo.builder()
                .sessionId("S2")
                .user(SessionInfo.UserSummary.builder()
                        .userId("U1")
                        .displayName("Alice")
                        .build())
                .build();

        LoginResponse res = LoginResponse.builder()
                .authenticated(true)
                .amr(List.of("pwd", "mfa"))
                .tokens(tokens)
                .session(session)
                .build();

        // 検証（getAuthenticated() は無い → isAuthenticated() を使う）
        assertTrue(res.isAuthenticated());
        assertEquals(List.of("pwd","mfa"), res.getAmr());
        assertEquals("acc-after-mfa", res.getTokens().getAccessToken());
        assertEquals("S2", res.getSession().getSessionId());
    }
}
