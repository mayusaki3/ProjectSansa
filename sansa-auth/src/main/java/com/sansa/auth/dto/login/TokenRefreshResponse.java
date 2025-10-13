package com.sansa.auth.dto.login;

import lombok.*;

/**
 * POST /auth/token/refresh のレスポンスDTO
 * 仕様: 02_ログイン.md 「R1 TokenRefreshResponse（tokens.accessToken, tokens.refreshToken, tv）」参照。:contentReference[oaicite:15]{index=15}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenRefreshResponse {

    /** 新しいアクセストークン/リフレッシュトークン */
    private Tokens tokens;

    /** 現在の token_version（/auth/logout_all でインクリメント） */
    private int tv;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class Tokens {
        private String accessToken;
        private String refreshToken;
    }
}
