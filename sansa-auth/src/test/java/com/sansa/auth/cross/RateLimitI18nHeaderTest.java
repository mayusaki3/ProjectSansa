package com.sansa.auth.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * レートリミットと i18n ヘッダの挙動を確認するテスト。
 *
 * 目的:
 *  - X-RateLimit-* / Retry-After / Content-Language 等のヘッダ付与確認
 *  - 多言語化の Accept-Language に基づくレスポンスの検証
 *
 * 注意:
 *  - 実装が Filter/Interceptor/ControllerAdvice いずれで行われるかに依存。
 *  - 実装が固まるまでコンパイルOKかつ実行抑止のため @Disabled。
 */
@WebMvcTest(controllers = { AuthController.class }) // 適当な公開エンドポイントで確認
class RateLimitI18nHeaderTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("RateLimit ヘッダが付与される")
    @Disabled("レートリミット実装確定後に有効化")
    void shouldAttachRateLimitHeaders() throws Exception {
        mvc.perform(get("/auth/ping")) // TODO: 実装に合わせた疎通用エンドポイント
           .andExpect(status().isOk())
           .andExpect(header().exists("X-RateLimit-Limit"))
           .andExpect(header().exists("X-RateLimit-Remaining"))
           .andExpect(header().exists("X-RateLimit-Reset"));
    }

    @Test
    @DisplayName("Accept-Language に基づき Content-Language が設定される")
    @Disabled("i18n 実装確定後に有効化")
    void shouldRespectAcceptLanguage() throws Exception {
        mvc.perform(get("/auth/ping").header("Accept-Language", "ja-JP"))
           .andExpect(status().isOk())
           .andExpect(header().string("Content-Language", "ja-JP"));
    }
}
