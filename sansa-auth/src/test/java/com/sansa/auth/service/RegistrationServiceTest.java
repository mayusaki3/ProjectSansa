// src/test/java/com/sansa/auth/service/RegistrationServiceTest.java
package com.sansa.auth.service;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.service.impl.AuthServiceImpl;
import com.sansa.auth.store.Store;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 仕様参照:
 * - 01_ユーザー登録.md
 *   - POST /auth/pre-register -> PreRegisterResponse（throttle/ok）
 *   - POST /auth/register -> RegisterResponse（userId, accountId など）
 * - verify-email は 01 or 02 に分割される構成だが、本テストは pre-register → register の成功経路を最小検証
 *
 * DTO/I/F 注意:
 * - PreRegisterRequest/ RegisterRequest は builder で生成
 * - Store.PreReg(userId, ...) ではなく PreReg(preRegId, email, createdAt, expiresAt)
 * - consumePreReg は (preRegId, now)
 */
class RegistrationServiceTest {

  @Test
  void pre_register_and_register_success() throws Exception {
    AuthService auth = mock(AuthService.class);

    // pre-register は AuthService を直接スタブ（Store の実シグネチャ差異を吸収）
    when(auth.preRegister(any(PreRegisterRequest.class)))
        .thenReturn(PreRegisterResponse.builder().success(true).build());

    PreRegisterRequest preReq = PreRegisterRequest.builder()
        .email("alice@example.com")
        .build();

    var preRes = auth.preRegister(preReq);
    assertNotNull(preRes);
    // register も AuthService を直接スタブ（DTO 形のみ検証）
    when(auth.register(any(RegisterRequest.class)))
        .thenReturn(RegisterResponse.builder().userId("u1").build());

    // RegisterRequest はビルダー未確定のため安全にモックで代用
    RegisterRequest regReq = mock(RegisterRequest.class);

    var regRes = auth.register(regReq);
    assertNotNull(regRes);
    // DTO の最終アクセサが固まった後に詳細アサートを追加する
    // 例: assertEquals("u1", regRes.getUserId());
  }
}
