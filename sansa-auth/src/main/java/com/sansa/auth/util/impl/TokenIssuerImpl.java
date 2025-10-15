package com.sansa.auth.util.impl;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import java.util.Objects;

/**
 * {@link TokenIssuer} のデフォルト実装。
 *
 * <p>実処理は {@link JwtProvider} に委譲します。
 * 本クラスは「発行（issue）」と「解析（parse）」に特化した薄い層で、
 * 余計なドメイン知識は持ちません。</p>
 *
 * <p>DI 構成例：
 * <pre>{@code
 * @Configuration
 * class ServiceWiringConfig {
 *   @Bean
 *   TokenIssuer tokenIssuer(JwtProvider jwt) {
 *     return new TokenIssuerImpl(jwt);
 *   }
 * }
 * }</pre>
 * </p>
 */
public class TokenIssuerImpl implements TokenIssuer {

    private final JwtProvider jwt;

    /**
     * 依存する {@link JwtProvider} を受け取るシンプルなコンストラクタ。
     */
    public TokenIssuerImpl(JwtProvider jwt) {
        this.jwt = Objects.requireNonNull(jwt, "jwt");
    }

    @Override
    public String issueAccessToken(String userId, int tokenVersion) {
        // JwtProvider 側が有効期限・署名キー・issuer などを内包している想定
        return jwt.createAccessToken(userId, tokenVersion);
    }

    @Override
    public String issueRefreshToken(String userId, String refreshId) {
        return jwt.createRefreshToken(userId, refreshId);
    }

    @Override
    public RefreshParseResult parseRefresh(String refreshToken) {
        // jwt.parseRefresh(...) の戻りに合わせて**正しいアクセサ**を使う
        // 例A: 戻りが record ParsedRefresh(String userId, String refreshId, int tokenVersion)
        var pr = jwt.parseRefresh(refreshToken);
        return new RefreshParseResult(pr.userId(), pr.refreshId());

        // 例B: 戻りが Map<String,Object> の場合
        // var claims = jwt.parseRefresh(refreshToken);
        // return new RefreshParseResult((String) claims.get("sub"), (String) claims.get("jti"));

        // 例C: 戻りが io.jsonwebtoken.Claims の場合
        // var claims = jwt.parseRefresh(refreshToken);
        // return new RefreshParseResult(claims.getSubject(), claims.getId());
    }
}
