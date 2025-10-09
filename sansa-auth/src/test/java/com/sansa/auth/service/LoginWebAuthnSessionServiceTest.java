package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoginWebAuthnSessionServiceTest {

  @InjectMocks AuthService authService;

  @Mock com.sansa.auth.repo.RepoInterfaces.IUserRepo userRepo;
  @Mock com.sansa.auth.repo.RepoInterfaces.ISessionRepo sessionRepo;

  @Mock TokenService tokenService;

  @Test @DisplayName("UT-03-001: パスワード成功 → authenticated=true, tokens, amr=[pwd]")
  void password_login_ok() {
    LoginRequest req = LoginRequest.builder()
        .identifier("alice")
        .password("correct")
        .build();
    assertThat(res.isAuthenticated()).isTrue();
    assertThat(res.getTokens().getAccessToken()).isNotBlank();
    assertThat(res.getAmr()).contains("pwd");
    assertThat(res.getSession().getSessionId()).isNotBlank();
  }
}
