// src/test/java/com/sansa/auth/service/LoginWebAuthnSessionServiceTest.java
package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.service.impl.AuthServiceImpl;
import com.sansa.auth.store.Store;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 仕様参照:
 * - 02_ログイン.md: POST /auth/login -> LoginResponse
 *   - 成功: 200 + LoginResponse { authenticated=true, tokens{AT/RT}, session, amr=["pwd"] }
 *   - 失敗: 401 (Problem+JSON) [本テストでは成功経路のみ]
 *
 * サービス層の役割:
 * - パスワード照合: PasswordHasher.matches()
 * - AT/RT発行: TokenIssuer.issueAccessToken / issueRefreshToken
 * - セッション保存: Store.saveOrUpdateSession()
 */
class LoginWebAuthnSessionServiceTest {

  @Test
  void password_login_success_returns_authenticated_with_session_and_tokens() throws Exception {
    // Arrange
    Store store = mock(Store.class);
    AuthServiceImpl.TokenIssuer ti = mock(AuthServiceImpl.TokenIssuer.class);
    AuthServiceImpl.PasswordHasher ph = mock(AuthServiceImpl.PasswordHasher.class);
    AuthService auth = new AuthServiceImpl(store, ti, ph);

    // ユーザー検索（identifier は email/accId どちらでも可、ここでは email）
    var user = new Store.User("u1", "acc1", "u1@example.com", "U1", "hash", true, 3);
    when(store.findUserByIdentifier("email", "u1@example.com"))
        .thenReturn(Optional.of(user));

    // パスワード一致
    when(ph.matches("pw", "hash")).thenReturn(true);

    // トークン発行
    when(store.getTokenVersion("u1")).thenReturn(3);
    when(ti.newRefreshId()).thenReturn("jti-1");
    when(ti.issueAccessToken("u1", 3)).thenReturn("AT");
    when(ti.issueRefreshToken("u1", "jti-1", 3)).thenReturn("RT");

    // セッション保存の引数検証（void メソッドは verify で確認）
    doAnswer(inv -> {
      Store.Session s = inv.getArgument(0, Store.Session.class);
      assertEquals("jti-1", s.sessionId());
      assertEquals("u1", s.userId());
      assertEquals(List.of("pwd"), s.amr());
      return null;
    }).when(store).saveOrUpdateSession(any(Store.Session.class));

    var req = LoginRequest.builder()
        .identifier("u1@example.com")
        .password("pw")
        .build();

    // Act
    LoginResponse res = auth.login(req);

    // Assert（02_ログイン.md の期待）
    assertTrue(res.isAuthenticated());
    assertFalse(res.isMfaRequired());

    LoginTokens tk = res.getTokens();
    assertNotNull(tk);
    assertEquals("AT", tk.getAccessToken());
    assertEquals("RT", tk.getRefreshToken());

    SessionInfo si = res.getSession();
    assertNotNull(si);
    assertEquals("jti-1", si.getSessionId());
    assertEquals(List.of("pwd"), si.getAmr());
    assertEquals("u1", res.getUser().getUserId());
    assertEquals("u1@example.com", res.getUser().getEmail());

    verify(store).saveOrUpdateSession(any(Store.Session.class));
  }
}
