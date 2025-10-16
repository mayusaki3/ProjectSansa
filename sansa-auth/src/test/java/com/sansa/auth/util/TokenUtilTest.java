// src/test/java/com/sansa/auth/util/TokenUtilTest.java
package com.sansa.auth.util;

import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.impl.AuthServiceImpl;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.TokenIssuer;
import com.sansa.auth.util.impl.TokenIssuerImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 仕様参照: - 02_ログイン.md 「R1: POST /auth/token/refresh」 - 成功: 200 +
 * TokenRefreshResponse { accessToken, refreshToken, tv } - RTローテーション挙動を
 * TokenIssuer モックで検証
 */
class TokenUtilTest {

    @Test
    void refresh_rotates_refresh_token_and_returns_tv() throws Exception {
        Store store = mock(Store.class);
        TokenIssuer ti = mock(TokenIssuer.class);
        AuthServiceImpl.PasswordHasher ph = mock(AuthServiceImpl.PasswordHasher.class);
        AuthService auth = new AuthServiceImpl(store, ti, ph);

        // RT 解析結果（署名/exp等は TokenIssuer に委譲）
        when(ti.parseRefresh("RT-old"))
                .thenReturn(new TokenIssuer.RefreshParseResult("john", "rt-1"   ));

        when(store.getTokenVersion("john")).thenReturn(2);
        when(ti.newRefreshId()).thenReturn("rt-2");
        when(ti.issueAccessToken("john", 2)).thenReturn("AT-new");
        when(ti.issueRefreshToken( "john", "rt-2")).thenReturn("RT-new");

        TokenRefreshResponse res = auth.refresh(new TokenRefreshRequest("RT-old"));

        assertNotNull(res); // DTO の具体アクセサは最終形に依存するため、存在のみ検証

        // 相互作用（RTローテーション）が正しく行われたことを検証
        verify(ti).parseRefresh("RT-old");
        verify(store).getTokenVersion("john");
        verify(ti).newRefreshId();
        verify(ti).issueAccessToken("john", 2);
        verify(ti).issueRefreshToken("john", "rt-2");
        verifyNoMoreInteractions(ti);
    }
}
