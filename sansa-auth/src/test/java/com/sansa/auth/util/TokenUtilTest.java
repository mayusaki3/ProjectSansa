package com.sansa.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sansa.auth.dto.login.LoginTokens;

class TokenUtilTest {

    @Test
    @DisplayName("LoginTokens のビルダーで最低限の形を確認")
    void tokensBuilderShape() {
        LoginTokens tokens = LoginTokens.builder()
                .accessToken("access-123")
                .refreshToken("refresh-456")
                .build();

        assertNotNull(tokens.getAccessToken());
        assertNotNull(tokens.getRefreshToken());
        assertEquals("access-123", tokens.getAccessToken());
        assertEquals("refresh-456", tokens.getRefreshToken());
    }
}
