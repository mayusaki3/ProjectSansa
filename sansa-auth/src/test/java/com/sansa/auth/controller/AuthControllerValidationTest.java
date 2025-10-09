package com.sansa.auth.controller;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.common.ProblemDetail;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.TokenService;
import com.sansa.auth.service.WebAuthnService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AuthController.class,
        WebAuthnController.class
})
@Import(ApiExceptionHandler.class) // 例外→ProblemDetail(JSON) を返すため
class AuthControllerValidationTest {

    private static final String API = "/api";

    @Resource
    MockMvc mvc;

    // --- 必要サービスは Mock で用意 ---
    @MockBean AuthService authService;
    @MockBean SessionService sessionService;
    @MockBean WebAuthnService webAuthnService;
    @MockBean TokenService tokenService;

    // ========== pre-register ==========
    @Test
    @DisplayName("UT-01-001: pre-register の email ブランク -> 400")
    void preRegister_email_blank_400() throws Exception {
        mvc.perform(post(API + "/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"email\":\"\",\"language\":\"ja-JP\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")))
            .andExpect(jsonPath("$.type").value(org.hamcrest.Matchers.containsString("invalid-argument")))
            .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    @DisplayName("UT-01-002: pre-register の language フォーマット不正 -> 400")
    void preRegister_language_invalid_400() throws Exception {
        mvc.perform(post(API + "/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"email\":\"a@b.com\",\"language\":\"jp_JP\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("language"));
    }

    // 連続実行で 429 を期待するテスト（ダミー実装想定：authService が rate limit 例外を投げる）
    @Test
    @DisplayName("UT-01-003: pre-register のレート制限 -> 429")
    void preRegister_rateLimit_429() throws Exception {
        // ここでサービスの振る舞いを 429 相当の例外にモック（例：ApiException("rate-limit") を投げる）
        Mockito.doThrow(new RuntimeException("rate-limit"))
               .when(authService).preRegister(any(PreRegisterRequest.class));

        mvc.perform(post(API + "/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"email\":\"a@b.com\",\"language\":\"ja-JP\"}"))
            .andExpect(status().isTooManyRequests());
    }

    // ========== login ==========
    @Test
    @DisplayName("UT-02-001: login の identifier 欠落 -> 400")
    void login_identifier_missing_400() throws Exception {
        mvc.perform(post(API + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"password\":\"x\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("identifier"));
    }

    @Test
    @DisplayName("UT-02-002: login の認証失敗 -> 401 (invalid-credentials)")
    void login_invalid_credentials_401() throws Exception {
        Mockito.doThrow(new RuntimeException("invalid-credentials"))
               .when(authService).login(any(LoginRequest.class));

        mvc.perform(post(API + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"identifier\":\"a@b.com\",\"password\":\"wrong\"}"))
            .andExpect(status().isUnauthorized());
    }

    // ========== WebAuthn ==========
    @Test
    @DisplayName("UT-03-001: WebAuthn assertion の result 欠落 -> 400")
    void webauthn_assertion_missing_400() throws Exception {
        mvc.perform(post(API + "/webauthn/assertion/result")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"id\":\"abcd\"}"))
            .andExpect(status().isBadRequest());
    }

    // ========== Sessions ==========
    @Test
    @DisplayName("UT-04-001: セッション削除: 存在しないID -> 404 + problem+json")
    void session_delete_not_found_404() throws Exception {
        Mockito.when(sessionService.revokeById(eq("not-found"))).thenReturn(false);

        mvc.perform(delete(API + "/sessions/not-found")
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja"))
            .andExpect(status().isNotFound())
            .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")))
            .andExpect(jsonPath("$.type").value(org.hamcrest.Matchers.containsString("not-found")));
    }

    // ========== i18n header ==========
    @Test
    @DisplayName("UT-05-001: エラーレスポンスは Content-Language を反映")
    void i18n_header_reflect() throws Exception {
        mvc.perform(post(API + "/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"email\":\"\",\"language\":\"ja-JP\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string("Content-Language", org.hamcrest.Matchers.startsWith("ja")));
    }
}
