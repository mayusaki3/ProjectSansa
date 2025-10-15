package com.sansa.auth.util.impl;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * TokenIssuer の標準実装。JwtProvider に委譲する。
 * - jti は128bit乱数をHEXで表現（ログ見やすさ重視）。
 */
public class TokenIssuerImpl implements TokenIssuer {

    private final JwtProvider jwt;
    private final SecureRandom random = new SecureRandom();
    private final HexFormat hex = HexFormat.of();

    public TokenIssuerImpl(JwtProvider jwt) {
        this.jwt = jwt;
    }

    @Override
    public String newRefreshId() {
        byte[] buf = new byte[16];
        random.nextBytes(buf);
        return hex.formatHex(buf);
    }

    @Override
    public String issueAccessToken(String userId, int ttlMinutes) {
        return jwt.createAccessToken(userId);
    }

    @Override
    public String issueRefreshToken(String userId, String jti, int tokenVersion) {
        return jwt.createRefreshToken(userId, jti, tokenVersion);
    }

    @Override
    public RefreshParseResult parseRefresh(String token) {
        JwtProvider.ParsedRefresh p = jwt.parseRefresh(token);
        return new RefreshParseResult(p.userId(), p.jti(), p.tokenVersion());
    }
}
