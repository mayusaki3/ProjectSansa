package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;

/**
 * 認証サービス I/F
 *
 * 現時点ではログイン / トークンリフレッシュのみを扱う。
 * ユーザー登録フロー（pre-register / verify-email / register）は
 * 後続の実装フェーズで追加する。
 */
public interface AuthService {

    /**
     * ログイン処理。
     *
     * @param request ログインリクエスト（identifier / password 等）
     * @return ログインレスポンス（アクセストークン / リフレッシュトークン等）
     */
    LoginResponse login(LoginRequest request);

    /**
     * リフレッシュトークンによるアクセストークン再発行。
     *
     * @param request リフレッシュトークンリクエスト
     * @return リフレッシュトークンレスポンス（新しいアクセストークン等）
     */
    TokenRefreshResponse refresh(TokenRefreshRequest request);
}
