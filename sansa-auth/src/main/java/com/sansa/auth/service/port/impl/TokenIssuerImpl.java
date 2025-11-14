package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenIssuer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * TokenIssuer 実装（JJWT）
 *  - HS256 / 最小クレーム
 */
public class TokenIssuerImpl implements TokenIssuer {

    private final SecretKey key;

    public TokenIssuerImpl(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AccessTokens issueAccess(String userId, String sessionId, String accessJti, String[] scopes, int ttlSeconds) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        String token = Jwts.builder()
                .subject(userId)
                .claim("sid", sessionId)
                .claim("jti", accessJti)
                .claim("scp", String.join(" ", scopes))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
        return new AccessTokens(token, accessJti, exp);
    }

    @Override
    public RefreshTokens issueRefresh(String userId, String sessionId, String refreshJti, int ttlSeconds) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        String token = Jwts.builder()
                .subject(userId)
                .claim("sid", sessionId)
                .claim("rjti", refreshJti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
        return new RefreshTokens(token, refreshJti, exp);
    }

    @Override
    public RefreshParseResult parseRefresh(String refreshToken) {
        var parsed = Jwts.parser().verifyWith(key).build().parseSignedClaims(refreshToken);
        String userId = parsed.getPayload().getSubject();
        String refreshId = parsed.getPayload().get("rjti", String.class);
        return new RefreshParseResult(userId, refreshId);
    }
}
