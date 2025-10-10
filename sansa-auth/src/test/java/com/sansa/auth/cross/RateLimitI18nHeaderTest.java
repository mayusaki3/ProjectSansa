package com.sansa.auth.cross;

import com.sansa.auth.controller.AuthController;
import com.sansa.auth.controller.ApiExceptionHandler;
import com.sansa.auth.exception.RateLimitException;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * レート制限(429) と i18n ヘッダ反映（Content-Language）の疎通テスト。
 * Web 層のスライスのみ（AuthController + ApiExceptionHandler）。
 */
@WebMvcTest(controllers = { AuthController.class })
@Import(ApiExceptionHandler.class)
@ActiveProfiles("inmem")
class RateLimitI18nHeaderTest {

    @Autowired
    private MockMvc mvc;

    // ==== Controller 依存はすべてモック ====
    @org.springframework.boot.test.mock.mockito.MockBean
    private AuthService authService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private SessionService sessionService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("i18n + rate-limit: Accept-Language=ja で 429 + problem+json + Content-Language=ja-*")
    void preRegister_rateLimit_returns_429_with_problem_and_lang() throws Exception {
        // サービス呼び出し時に業務例外（レート制限）を投げる
        when(authService.preRegister(any())).thenThrow(new RateLimitException("rate-limit"));

        mvc.perform(post("/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content("{\"email\":\"a@example.com\",\"language\":\"ja\"}"))
           .andExpect(status().isTooManyRequests())
           // Content-Type: application/problem+json
           .andExpect(header().string("Content-Type", containsString("application/problem+json")))
           // Content-Language が ja で始まる
           .andExpect(header().string("Content-Language", startsWith("ja")))
           // ペイロードの shape（ApiExceptionHandler の problem() に合わせる）
           .andExpect(jsonPath("$.title", containsString("rate-limit")))
           .andExpect(jsonPath("$.code",  containsString("rate-limit")));
    }

    @Test
    @DisplayName("i18n: バリデーションエラー(400)でも Content-Language を反映")
    void preRegister_validation_error_reflects_lang_header() throws Exception {
        // preRegister が呼ばれる前に Validation で 400 になる想定（email 空）
        // モック動作は不要だが、万一呼ばれても何もしないようにしておく
        Mockito.reset(authService);

        mvc.perform(post("/auth/pre-register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en-US")
                .content("{\"email\":\"\",\"language\":\"en\"}"))
           .andExpect(status().isBadRequest())
           .andExpect(header().string("Content-Type", containsString("application/problem+json")))
           .andExpect(header().string("Content-Language", startsWith("en")));
    }
}
