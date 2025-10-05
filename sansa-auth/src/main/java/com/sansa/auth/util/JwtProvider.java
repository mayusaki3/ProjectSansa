package com.sansa.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtProvider {

    private final SecretKey secretKey; // jjwt 0.12: verifyWith は SecretKey/PublicKey が必要
    private final String issuer;
    private final int accessTokenMinutes;
    private final int refreshTokenDays;

    public JwtProvider(SecretKey secretKey, String issuer, int accessTokenMinutes, int refreshTokenDays) {
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.accessTokenMinutes = accessTokenMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claims(claims == null ? new HashMap<>() : new HashMap<>(claims))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey) // HS256/HS512 など
                .compact();
    }

    public String createRefreshToken(String subject, String tokenVersion) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshTokenDays, ChronoUnit.DAYS);

        Map<String, Object> claims = new HashMap<>();
        if (tokenVersion != null) claims.put("tv", tokenVersion);

        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    public Claims parse(String token) {
        // parserBuilder は verifyWith(SecretKey/PublicKey) しか受けない
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Map<String, Object> claimsAsMap(Claims claims) {
        // 0.12 には asMap() がないため、コピーして返す
        return new HashMap<>(claims);
    }
}
