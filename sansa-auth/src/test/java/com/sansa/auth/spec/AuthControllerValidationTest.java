package com.sansa.auth.spec;

import com.sansa.auth.controller.ApiExceptionHandler;
import com.sansa.auth.controller.AuthController;
import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.service.AuthService;
import com.sansa.testkit.util.TestCaseReporter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller入力検証・エラー整形（AuthController: /auth/login）
 *
 * 仕様優先:
 * - identifier(accountId/email) 未指定は 400 invalid-argument
 * - errors[].field は "identifier" とする
 */
@WebMvcTest(controllers = AuthController.class)
@Import(ApiExceptionHandler.class)
class AuthControllerValidationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService auth;

    /**
     * AUTH-CTRL-UT-005
     * login の identifier（accountId/email）未指定 -> 400
     */
    @Test
    void auth_ctrl_ut_005_login_identifier_missing_returns_400_problem_json() throws Exception {
        TestCaseReporter.tc("AUTH-CTRL-UT-005");

        // password はあるが accountId/email が無い（AccountIdOrEmailRequired が弾く）
        String body = """
          {
            "password": "pass"
          }
        """;

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
           .andExpect(jsonPath("$.title").value("invalid-argument"))
           .andExpect(jsonPath("$.code").value("invalid-argument"))
           .andExpect(jsonPath("$.errors").isArray())
           .andExpect(jsonPath("$.errors.length()").value(greaterThanOrEqualTo(1)))
           .andExpect(jsonPath("$.errors[*].field", hasItem("identifier")));
    }

    /**
     * AUTH-CTRL-UT-006
     * login 失敗 -> 401 invalid-credentials
     */
    @Test
    void auth_ctrl_ut_006_login_invalid_credentials_returns_401_problem_json() throws Exception {
        TestCaseReporter.tc("AUTH-CTRL-UT-006");

        Mockito.when(auth.login(any()))
               .thenThrow(new InvalidCredentialsException("dummy"));

        // 入力検証を通すため accountId と password を入れる
        String body = """
          {
            "accountId": "alice",
            "password": "wrong"
          }
        """;

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isUnauthorized())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
           .andExpect(jsonPath("$.title").value("invalid-credentials"))
           .andExpect(jsonPath("$.code").value("invalid-credentials"));
    }

    /**
     * AUTH-CTRL-UT-007
     * i18n Accept-Language -> Content-Language 反映（バリデーションエラーで確認）
     */
    @Test
    void auth_ctrl_ut_007_accept_language_reflected_to_content_language_on_validation_error() throws Exception {
        TestCaseReporter.tc("AUTH-CTRL-UT-007");

        // バリデーションエラーを確実に起こす（identifier不足）
        String body = """
          {
            "password": "pass"
          }
        """;

        mvc.perform(post("/auth/login")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "ja-JP")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "ja-JP"));
    }
}
