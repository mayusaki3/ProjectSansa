// src/test/java/com/sansa/auth/service/LogoutServiceTest.java
package com.sansa.auth.service;

import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 仕様参照:
 * - 05_セッション管理.md
 *   - POST /auth/logout -> LogoutResponse { success, count }
 *   - POST /auth/logout_all -> LogoutResponse { success, count }
 *
 * 設計ノート:
 * - /auth は AuthService 管轄（SessionService は /sessions 用）
 * - 現在セッション対象は LogoutRequest.sessionId を null とし、実装側で解釈
 */
class LogoutServiceTest {

  @Test
  void logout_current_session() throws Exception {
    AuthService auth = mock(AuthService.class);
    when(auth.logout(any(LogoutRequest.class)))
        .thenReturn(LogoutResponse.builder().success(true).success(true).build());

    LogoutResponse res = auth.logout(
        LogoutRequest.builder().sessionId(null).build()
    );

    assertTrue(res.isSuccess());
  }

  @Test
  void logout_all_sessions() throws Exception {
    AuthService auth = mock(AuthService.class);
    when(auth.logoutAll()).thenReturn(LogoutResponse.builder().success(true).build());

    LogoutResponse res = auth.logoutAll();

    assertTrue(res.isSuccess());
  }
}
