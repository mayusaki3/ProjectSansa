package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.service.port.TokenFacade.TokenPair;
import com.sansa.auth.util.TokenIssuer;
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
@RequiredArgsConstructor
public class TokenFacadeImpl implements TokenFacade {

    private final TokenIssuer tokenIssuer;

    /**
     * 認証直後に新規の AT/RT を発行する。
     * - refreshId は毎回新規採番
     * - roles は必要な実装で TokenIssuer 側へ渡す。現状未使用なら無視。
     */
    @Override
    public TokenFacade.TokenPair issueAfterAuth(String userId, List<String> roles) {
        // 新しい refreshId を採番して RT を発行、AT も同時に発行
        final String refreshId = tokenIssuer.newRefreshId();

        // 役割クレームが必要なら TokenIssuer の API に合わせて渡す。
        // 例：tokenIssuer.issueAccessToken(userId, roles) に変更する等。
        final String accessToken  = tokenIssuer.issueAccessToken(userId, 1);
        final String refreshToken = tokenIssuer.issueRefreshToken(userId, refreshId);

        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * RT のローテーション。oldRefreshId は監査・無効化等で使う想定。
     * 実際の “旧 RT 無効化” は TokenIssuer 側やストア側に委ねてOK。
     */
    @Override
    public TokenPair rotate(String userId, String oldRefreshId) {
        // 新しい refreshId を採番して置き換え
        final String newRefreshId  = tokenIssuer.newRefreshId();
        final String accessToken   = tokenIssuer.issueAccessToken(userId, 1);
        final String refreshToken  = tokenIssuer.issueRefreshToken(userId, newRefreshId);

        // oldRefreshId の扱い（無効化やログ）は、呼び出し側 or TokenIssuer 内で行う設計に。
        return new TokenPair(accessToken, refreshToken);
    }
}
