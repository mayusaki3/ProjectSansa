package com.sansa.auth.service.port.impl;

import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.util.TokenIssuer;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * TokenFacade の標準実装。
 * 下位の TokenIssuer に委譲し、AT/RT の発行・ローテーションを提供する。
 *
 * 設計ポイント:
 * - アクセストークンの "tv"（tokenVersion）やクレーム構成は TokenIssuer → JwtProvider に委譲。
 * - リフレッシュトークンの "jti"（refreshId）生成も TokenIssuer.newRefreshId() に委譲。
 *
 * DI について:
 * - 構成クラス（例: MfaWiringConfig）側で @Bean を作るか、
 *   ここに @Component を付けてコンポーネントスキャンさせるかのどちらか。
 *   既に匿名クラスで @Bean 生成している場合は重複回避のため @Component は付けないでください。
 */
@Service
@RequiredArgsConstructor
public class TokenFacadeImpl implements TokenFacade {

    private final TokenIssuer tokenIssuer;

    /**
     * 認証直後に新規の AT/RT を発行する。
     * - refreshId は毎回新規採番
     * - roles は必要な実装で TokenIssuer 側へ渡す。現状未使用なら無視。
     */
    @Override
    public LoginTokens issueAfterAuth(String userId, List<String> roles) {
        String rolesStr = (roles == null || roles.isEmpty()) ? "" : String.join(" ", roles);
        var accessToken = tokenIssuer.issueAccessToken(userId, 1);
        var refreshToken = tokenIssuer.issueRefreshToken(userId, rolesStr);
        return LoginTokens.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * RT のローテーション。oldRefreshId は監査・無効化等で使う想定。
     * 実際の “旧 RT 無効化” は TokenIssuer 側やストア側に委ねてOK。
     */
    @Override
    public LoginTokens rotate(String userId, String oldRefreshId) {
        // 新しい refreshId を採番して置き換え
        final String newRefreshId  = tokenIssuer.newRefreshId();
        final String accessToken   = tokenIssuer.issueAccessToken(userId, 1);
        final String refreshToken  = tokenIssuer.issueRefreshToken(userId, newRefreshId);
        return LoginTokens.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
