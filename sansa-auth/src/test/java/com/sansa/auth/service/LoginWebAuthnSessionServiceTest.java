package com.sansa.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.SessionInfo;

class LoginWebAuthnSessionServiceTest {

    @Test
    @DisplayName("パスワードログインのレスポンスDTO最低限の形を検証（ビルダー前提）")
    void loginPassword_responseShape() {
        // 入力DTO（サービス実行はせず、DTOの形のみ確認）
        LoginRequest req = LoginRequest.builder()
                .identifier("alice@example.com")
                .password("p@ssw0rd")
                .build();
        assertNotNull(req);
        assertEquals("alice@example.com", req.getIdentifier());
        assertEquals("p@ssw0rd", req.getPassword());

        // 出力DTO（想定形）
        LoginTokens tokens = LoginTokens.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        // UserSummary: accountId(...) は存在しないため使用しない
        SessionInfo.UserSummary user = SessionInfo.UserSummary.builder()
                .userId("U1")
                .displayName("Alice")
                .build();

        SessionInfo session = SessionInfo.builder()
                .sessionId("S1")
                .user(user)
                .build();

        LoginResponse res = LoginResponse.builder()
                .authenticated(true)
                .amr(List.of("pwd"))
                .tokens(tokens)
                .session(session)
                .build();

        // 検証（getAuthenticated() は無い → isAuthenticated() を使う）
        assertTrue(res.isAuthenticated());
        assertNotNull(res.getTokens());
        assertEquals("access-token", res.getTokens().getAccessToken());
        assertEquals("refresh-token", res.getTokens().getRefreshToken());
        assertNotNull(res.getSession());
        assertEquals("S1", res.getSession().getSessionId());
        assertEquals("U1", res.getSession().getUser().getUserId());
        assertEquals(List.of("pwd"), res.getAmr());
    }
}
