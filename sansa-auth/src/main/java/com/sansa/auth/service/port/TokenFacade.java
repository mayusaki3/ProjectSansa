package com.sansa.auth.service.port;

import com.sansa.auth.dto.login.LoginTokens;

import java.util.List;

/**
 * アプリケーションサービス層から利用するトークン発行窓口。
 * 実体は TokenIssuer + Store 等に委譲する。
 */
public interface TokenFacade {

    /**
     * 認証成功後に AT / RT を発行する。
     *
     * @param userId ユーザーID
     * @param authorities クレームに載せる権限など（必要なければ空でOK）
     */
    LoginTokens issueAfterAuth(String userId, List<String> authorities);

    /**
     * RT を用いて AT/RT をローテーションする。
     *
     * @param refreshToken 受け取った RT
     */
    LoginTokens rotate(String refreshToken);
}
