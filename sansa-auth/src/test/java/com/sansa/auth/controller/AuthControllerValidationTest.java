package com.sansa.auth.controller;

import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.MfaService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 単体: Controller 入力検証・エラー整形（UT-01-001..010）
 * 仕様根拠: 01_Controller_入力検証・エラー整形.md
 */
@WebMvcTest(controllers = {
        AuthController.class, MfaController.class, WebAuthnController.class, ApiExceptionHandler.class
})
class AuthControllerValidationTest {

    @Autowired
    MockMvc mvc;

    @MockBean AuthService authService;
    @MockBean MfaService mfaService;
    @MockBean SessionService sessionService;
    @MockBean TokenService tokenService;

    @Test
    @DisplayName("UT-01-001: pre-register の email 空文字 -> 400 (problem+json)")
    void preRegister_email_blank_400() throws Exception {
        mvc.perform(post("/auth/pre-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", containsString("application/problem+json")))
                .andExpect(jsonPath("$.type", containsString("invalid-argument")))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    @DisplayName("UT-01-002: pre-register の language フォーマット不正 -> 400")
    void preRegister_language_invalid_400() throws Exception {
        mvc.perform(post("/auth/pre-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"language\":\"jp_JP\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("language"));
    }

    @Test
    @DisplayName("UT-01-005: login の identifier 未指定 -> 400")
    void login_identifier_missing_400() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("identifier"));
    }

    @Test
    @DisplayName("UT-01-006: login 失敗 -> 401 /invalid-credentials")
    void login_invalid_credentials_401() throws Exception {
        // service からの例外→ApiExceptionHandlerで整形 という想定でもよいが、
        // ここは Controller 入力以外は黒箱扱いで 401 を返すスタブを置かない（デフォルトは未スタブ=何もしない）
        // → 実装依存の場合は、AuthService#login(req) 例外スローをスタブしても良い。
        Mockito.when(authService.login(Mockito.any())).thenThrow(new RuntimeException("invalid-credentials"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"alice\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", containsString("invalid-credentials")));
    }

    @Test
    @DisplayName("UT-01-007: Accept-Language を Content-Language へ反映")
    void i18n_header_reflect() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ja-JP")
                        .content("{\"identifier\":\"alice\",\"password\":\"correct\"}"))
                .andExpect(header().string("Content-Language", startsWith("ja")));
    }

    @Test
    @DisplayName("UT-01-009: DELETE /sessions/not-found -> 404 /session_not_found")
    void session_delete_not_found_404() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("session_not_found"))
                .when(sessionService).logout(LogoutRequest.builder().sessionId(Mockito.anyString()).build());

        mvc.perform(delete("/sessions/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", containsString("session_not_found")));
    }

    @Test
    @DisplayName("UT-01-010: pre-register のレート制限 -> 429 + Retry-After")
    void preRegister_rateLimit_429() throws Exception {
        mvc.perform(post("/auth/pre-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"hot@spot.com\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(header().string("RateLimit-Remaining", notNullValue()));
    }

    // 残り（UT-01-003,008）は雛形（入力例のみ）:
    @Test @DisplayName("UT-01-003: verify-email の code 桁不足 -> 400")
    void verifyEmail_code_short_400() throws Exception {
        mvc.perform(post("/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"a@b.com\",\"code\":\"123\"}"))
           .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("UT-01-008: WebAuthn assertion 必須欠落 -> 400")
    void webauthn_assertion_missing_400() throws Exception {
        mvc.perform(post("/webauthn/assertion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"\",\"clientDataJSON\":\"\",\"authenticatorData\":\"\",\"signature\":\"\"}"))
           .andExpect(status().isBadRequest());
    }
}
