package com.sansa.auth.dto.login;

import lombok.Builder;
import lombok.Value;

/**
 * ログイン後または MFA 完了後に発行されるトークン群。
 * accessToken / refreshToken をセットで保持する。
 */
@Value
@Builder
public class LoginTokens {
    /** アクセストークン (JWT) */
    String accessToken;

    /** リフレッシュトークン (JWT) */
    String refreshToken;
}
