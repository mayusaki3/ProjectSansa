package com.sansa.auth.controller;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.service.AuthService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController の入力バリデーションに関するテスト。
 *
 * 目的:
 *  - DTO (@Valid) の検証が 400 を返すか
 *  - ProblemDetail 形式のエラーが返るか（実装に合わせて調整）
 *
 * 注意:
 *  - エンドポイントパスは実装と合わせること（デフォルト: /auth/login）。
 *  - 例外ハンドラ(ApiExceptionHandler)のメッセージ/フォーマットに追随して期待値を調整してください。
 */
@WebMvcTest(AuthController.class)
class AuthControllerValidationTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AuthService auth;

    @Test
    @DisplayName("必須項目不足なら 400 を返す")
    @Disabled("エンドポイント/ProblemDetail 仕様確定後に有効化")
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        // language=json
        String body = """
          {
            "identifier": "",
            "password": ""
          }
        """;

        // モックは呼ばれない想定（@Validで弾かれるため）
        Mockito.verifyNoInteractions(auth);

        mvc.perform(post("/auth/login") // TODO: 実装のパスに合わせる
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
           // TODO: ApiExceptionHandler の ProblemDetail 仕様に合わせて調整
           .andExpect(jsonPath("$.title").exists())
           .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("妥当な入力なら 200 && LoginResponse を返す")
    @Disabled("Service 実装接続後に有効化")
    void shouldLoginWithValidRequest() throws Exception {
        LoginResponse ok = LoginResponse.builder()
                .authenticated(true)
                .build();
        Mockito.when(auth.login(Mockito.any(LoginRequest.class))).thenReturn(ok);

        // language=json
        String body = """
          {
            "identifier": "user@example.com",
            "password": "p@ssw0rd"
          }
        """;

        mvc.perform(post("/auth/login") // TODO: 実装のパスに合わせる
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.success").value(true));
    }
}
