package com.sansa.auth.util.impl;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import java.util.UUID;

/**
 * {@link TokenIssuer} の既定実装。
 *
 * <p>責務の分離:</p>
 * <ul>
 *   <li>署名方式やクレームの物理名（"tv" / "jti" など）は {@link JwtProvider} が担う</li>
 *   <li>アプリ層からはインターフェース（本クラス）だけを使えばよい</li>
 * </ul>
 */
public class TokenIssuerImpl implements TokenIssuer {

    private final JwtProvider jwt;

    public TokenIssuerImpl(JwtProvider jwt) {
        this.jwt = jwt;
    }

    /** アクセストークン発行（"tv" = tokenVersion を付与） */
    @Override
    public String issueAccessToken(String userId, int tokenVersion) {
        return jwt.createAccessToken(userId, tokenVersion);
    }

    /** リフレッシュトークン発行（"jti" = refreshId を付与） */
    @Override
    public String issueRefreshToken(String userId, String refreshId) {
        return jwt.createRefreshToken(userId, refreshId);
    }

    /** リフレッシュトークン解析（sub と jti を返す） */
    @Override
    public RefreshParseResult parseRefresh(String refreshToken) {
        JwtProvider.ParsedRefresh pr = jwt.parseRefresh(refreshToken);
        return new RefreshParseResult(pr.userId(), pr.refreshId());
    }

    /** リフレッシュトークンのローテーションに使う新しい JTI を返す） */
    @Override
    public String newRefreshId() {
        // ローテーション用の新規 JTI を生成（衝突が事実上起こらない UUID で十分）
        return UUID.randomUUID().toString();
    }
}
