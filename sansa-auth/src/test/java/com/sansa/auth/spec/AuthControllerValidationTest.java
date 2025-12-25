package com.sansa.auth.spec;

import com.sansa.auth.controller.ApiExceptionHandler;
import com.sansa.auth.controller.AuthController;
import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.service.AuthService;
import com.sansa.testkit.util.TestPrinter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.PrintStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller入力検証・エラー整形（AuthController: /auth/login）
 *
 * 仕様優先:
 * - identifier(accountId/email) 未指定は 400 invalid-argument
 * - errors[].field は "identifier" とする
 *
 * 注意:
 * - [TESTCASE] 出力は testkit/logging の自己検証のみ。
 * - 本テストは TestPrinter により ✅/❌ を出力する。
 */
@WebMvcTest(controllers = AuthController.class)
@Import(ApiExceptionHandler.class)
class AuthControllerValidationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService auth;

    private TestPrinter newPrinter(PrintStream out) {
        // module/suite はプロジェクト内で統一する前提。
        // 例: module="AUTH", suite="CTRL-UT"
        return new TestPrinter("AUTH", "CTRL-UT", out);
    }

    /**
     * AUTH-CTRL-UT-005
     * login の identifier（accountId/email）未指定 -> 400
     */
    @Test
    void AUTH_CTRL_UT_005_login_identifier_missing_returns_400_problem_json() throws Exception {
        var printer = newPrinter(System.out);
        boolean ok = false;

        try {
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

            ok = true;
        } finally {
            printer.printCase(
                    "005",
                    "AUTH-CTRL-UT-005 login identifier未指定 -> 400 problem+json (errors[].field==identifier)",
                    ok,
                    AuthController.class
            );
            printer.printSummary();
        }
    }

    /**
     * AUTH-CTRL-UT-006
     * login 失敗 -> 401 invalid-credentials
     */
    @Test
    void AUTH_CTRL_UT_006_login_invalid_credentials_returns_401_problem_json() throws Exception {
        var printer = newPrinter(System.out);
        boolean ok = false;

        try {
            Mockito.when(auth.login(any()))
                   .thenThrow(new InvalidCredentialsException("dummy"));

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

            ok = true;
        } finally {
            printer.printCase(
                    "006",
                    "AUTH-CTRL-UT-006 login invalid-credentials -> 401 problem+json",
                    ok,
                    AuthController.class
            );
            printer.printSummary();
        }
    }

    /**
     * AUTH-CTRL-UT-007
     * i18n Accept-Language -> Content-Language 反映（バリデーションエラーで確認）
     */
    @Test
    void AUTH_CTRL_UT_007_accept_language_reflected_to_content_language_on_validation_error() throws Exception {
        var printer = newPrinter(System.out);
        boolean ok = false;

        try {
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

            ok = true;
        } finally {
            printer.printCase(
                    "007",
                    "AUTH-CTRL-UT-007 Accept-Language -> Content-Language (ja-JP) on validation error",
                    ok,
                    AuthController.class
            );
            printer.printSummary();
        }
    }
}
