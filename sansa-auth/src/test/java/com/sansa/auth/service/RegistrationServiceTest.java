package com.sansa.auth.service;

import com.sansa.auth.dto.auth.PreRegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @InjectMocks AuthService authService;

  @Mock com.sansa.auth.repo.RepoInterfaces.IPreRegRepo preRegRepo;
  @Mock com.sansa.auth.repo.RepoInterfaces.IUserRepo userRepo;

  @Test @DisplayName("UT-02-001: pre-register 正常 -> success=true")
  void preRegister_ok() {
    PreRegisterRequest req = PreRegisterRequest.builder()
        .email("valid@example.com")
        .language("ja")
        .build();
    PreRegisterResponse res = authService.preRegister(req);
    assertThat(res).isNotNull();
    assertThat(res.isSuccess()).isTrue();
  }

  @Test @DisplayName("UT-02-003: verify-email 成功 → preRegId 付与")
  void verifyEmail_ok_returns_preRegId() {
    VerifyEmailRequest vreq = VerifyEmailRequest.builder()
        .email("valid@example.com")
        .code("123456")
        .build();
    var res = authService.verifyEmail(req);
    assertThat(res.getPreRegId()).isNotBlank();
    assertThat(res.getExpiresIn()).isPositive();
  }

  @Test @DisplayName("UT-02-004: verify-email 期限切れ -> 例外")
  void verifyEmail_expired() {
    VerifyEmailRequest vreq = VerifyEmailRequest.builder()
        .email("valid@example.com")
        .code("000000")
        .build();
    assertThrows(IllegalArgumentException.class, () -> authService.verifyEmail(req));
  }
}
