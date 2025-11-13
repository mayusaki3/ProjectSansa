package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * TokenIssuer の標準実装。
 * - JJWT 0.12.x に準拠（parserBuilder() は未使用）
 * - HS256 署名、tv（tokenVersion）と amr をクレームに格納
 * - access / refresh で鍵とTTLを分離可能
 */
@Component
public class TokenIssuerImpl implements TokenIssuer {

    // ---- 設定値（application.yml 等から取得） ----
    // Base64URL でエンコードされた共有鍵を想定（HMAC）
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private final Duration defaultAccessTtl;
    private final Duration defaultRefreshTtl;

    // クレーム名の定数
    private static final String CLAIM_TOKEN_VERSION = "tv";
    private static final String CLAIM_AMR = "amr";

    /**
     * コンストラクタ
     *
     * @param accessSecretB64  Base64URL のアクセス用共有鍵
     * @param refreshSecretB64 Base64URL のリフレッシュ用共有鍵
     * @param accessTtlSeconds アクセストークン既定TTL（秒）
     * @param refreshTtlSeconds リフレッシュトークン既定TTL（秒）
     */
    public TokenIssuerImpl(
            @Value("${auth.jwt.access.secret-b64}") String accessSecretB64,
            @Value("${auth.jwt.refresh.secret-b64}") String refreshSecretB64,
            @Value("${auth.jwt.access.ttl-seconds:900}") long accessTtlSeconds,
            @Value("${auth.jwt.refresh.ttl-seconds:1209600}") long refreshTtlSeconds
    ) {
        // 共有鍵を生成（Base64URLデコード → SecretKey）
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(Objects.requireNonNull(accessSecretB64, "access secret not set")));
        // refresh も分ける。単一鍵運用にしたければ同じ値を設定側で与える
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(Objects.requireNonNull(refreshSecretB64, "refresh secret not set")));
        this.defaultAccessTtl = Duration.ofSeconds(accessTtlSeconds);
        this.defaultRefreshTtl = Duration.ofSeconds(refreshTtlSeconds);
    }

    // =====================================================================================
    // 発行系
    // =====================================================================================

    /**
     * アクセストークン発行
     *
     * @param userId       対象ユーザーID（sub）
     * @param tokenVersion Token Version（tv）
     * @param issuedAt     発行時刻（iat）
     * @param ttl          有効期間。null の場合は既定 TTL
     * @param amr          Authentication Methods References（例: ["pwd","mfa"]）
     */
    @Override
    public String issueAccessToken(String userId, int tokenVersion, Instant issuedAt, Duration ttl, List<String> amr) {
        final Instant iat = nonNullInstant(issuedAt, Instant.now());
        final Duration useTtl = (ttl == null || ttl.isZero() || ttl.isNegative()) ? defaultAccessTtl : ttl;
        final Instant exp = iat.plus(useTtl);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(iat))
                .expiration(Date.from(exp))
                .claim(CLAIM_TOKEN_VERSION, tokenVersion)
                .claim(CLAIM_AMR, amr)
                .signWith(accessKey, Jwts.SIG.HS256) // 0.12.x 形式
                .compact();
    }

    /**
     * リフレッシュトークン発行
     */
    @Override
    public String issueRefreshToken(String userId, int tokenVersion, Instant issuedAt, Duration ttl) {
        final Instant iat = nonNullInstant(issuedAt, Instant.now());
        final Duration useTtl = (ttl == null || ttl.isZero() || ttl.isNegative()) ? defaultRefreshTtl : ttl;
        final Instant exp = iat.plus(useTtl);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(iat))
                .expiration(Date.from(exp))
                .claim(CLAIM_TOKEN_VERSION, tokenVersion)
                .signWith(refreshKey, Jwts.SIG.HS256)
                .compact();
    }

    // =====================================================================================
    // 解析/検証系（JJWT 0.12.x）
    // =====================================================================================

    @Override
    public AccessParseResult parseAccessToken(String token) throws JwtException {
        Claims c = parserFor(accessKey).parseSignedClaims(token).getPayload();
        return toAccessResult(c);
    }

    @Override
    public RefreshParseResult parseRefreshToken(String token) throws JwtException {
        Claims c = parserFor(refreshKey).parseSignedClaims(token).getPayload();
        return toRefreshResult(c);
    }

    // =====================================================================================
    // ヘルパ
    // =====================================================================================

    private static JwtParser parserFor(SecretKey key) {
        // 0.12.x: parserBuilder() は廃止。verifyWith(...).build() を使用
        return Jwts.parser().verifyWith(key).build();
    }

    private static Instant nonNullInstant(Instant value, Instant fallback) {
        return value != null ? value : fallback;
    }

    private static int claimInt(Claims c, String name, int defaultValue) {
        Object v = c.get(name);
        if (v == null) return defaultValue;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString());
    }

    @SuppressWarnings("unchecked")
    private static List<String> claimStringList(Claims c, String name) {
        Object v = c.get(name);
        if (v == null) return List.of();
        if (v instanceof List<?> l) {
            return (List<String>) l.stream().map(String::valueOf).toList();
        }
        // カンマ区切りで来た場合のフォールバック
        return List.of(v.toString().split("\\s*,\\s*"));
    }

    private static AccessParseResult toAccessResult(Claims c) {
        String sub = c.getSubject();
        int tv = claimInt(c, CLAIM_TOKEN_VERSION, 0);
        List<String> amr = claimStringList(c, CLAIM_AMR);
        Instant iat = c.getIssuedAt() != null ? c.getIssuedAt().toInstant() : null;
        Instant exp = c.getExpiration() != null ? c.getExpiration().toInstant() : null;
        return new AccessParseResult(sub, tv, iat, exp, amr);
    }

    private static RefreshParseResult toRefreshResult(Claims c) {
        String sub = c.getSubject();
        int tv = claimInt(c, CLAIM_TOKEN_VERSION, 0);
        Instant iat = c.getIssuedAt() != null ? c.getIssuedAt().toInstant() : null;
        Instant exp = c.getExpiration() != null ? c.getExpiration().toInstant() : null;
        return new RefreshParseResult(sub, tv, iat, exp);
    }

    // =====================================================================================
    // 結果レコード（TokenIssuer の戻り型に合わせる）
    // =====================================================================================

    /**
     * アクセストークン解析結果
     */
    public static final class AccessParseResult implements TokenIssuer.AccessParseResult {
        private final String userId;
        private final int tokenVersion;
        private final Instant issuedAt;
        private final Instant expiresAt;
        private final List<String> amr;

        public AccessParseResult(String userId, int tokenVersion, Instant issuedAt, Instant expiresAt, List<String> amr) {
            this.userId = userId;
            this.tokenVersion = tokenVersion;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.amr = amr == null ? List.of() : List.copyOf(amr);
        }

        @Override public String userId() { return userId; }
        @Override public int tokenVersion() { return tokenVersion; }
        @Override public Instant issuedAt() { return issuedAt; }
        @Override public Instant expiresAt() { return expiresAt; }
        @Override public List<String> amr() { return amr; }
    }

    /**
     * リフレッシュトークン解析結果
     */
    public static final class RefreshParseResult implements TokenIssuer.RefreshParseResult {
        private final String userId;
        private final int tokenVersion;
        private final Instant issuedAt;
        private final Instant expiresAt;

        public RefreshParseResult(String userId, int tokenVersion, Instant issuedAt, Instant expiresAt) {
            this.userId = userId;
            this.tokenVersion = tokenVersion;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        @Override public String userId() { return userId; }
        @Override public int tokenVersion() { return tokenVersion; }
        @Override public Instant issuedAt() { return issuedAt; }
        @Override public Instant expiresAt() { return expiresAt; }
    }
}
