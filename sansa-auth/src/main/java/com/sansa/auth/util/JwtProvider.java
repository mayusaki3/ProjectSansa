package com.sansa.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * JWT の発行・解析の実装詳細を隠蔽する薄いユーティリティ。
 *
 * <p>ポリシー</p>
 * <ul>
 *   <li>アクセストークン: {@code sub}=userId, {@code tv}=tokenVersion を必ず含める</li>
 *   <li>リフレッシュトークン: {@code sub}=userId, {@code jti}=refreshId を必ず含める</li>
 * </ul>
 *
 * <p>JJWT 0.12+ を想定（パーサは {@code Jwts.parser().verifyWith(key).build()}）。</p>
 */
public final class JwtProvider {

    /** 署名鍵（HMAC 系を想定） */
    private final SecretKey secretKey;
    /** iss（任意。監査や多環境の識別に使用） */
    private final String issuer;
    /** アクセストークンのTTL（秒） */
    private final int accessTokenTtlSeconds;
    /** リフレッシュトークンのTTL（秒） */
    private final int refreshTokenTtlSeconds;

    /**
     * 直接コンストラクタ。
     */
    public JwtProvider(SecretKey secretKey, String issuer,
                        int accessTokenTtlSeconds, int refreshTokenTtlSeconds) {
        this.secretKey = Objects.requireNonNull(secretKey, "secretKey");
        this.issuer = issuer;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    /**
     * Base64 エンコード済みシークレットから HMAC 鍵を生成して組み立てるユーティリティ。
     *
     * @param base64Secret Base64 文字列（推奨：十分な長さのランダムバイト）
     */
    public static JwtProvider fromBase64Secret(String base64Secret, String issuer,
                                                int accessTokenTtlSeconds, int refreshTokenTtlSeconds) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret.getBytes(StandardCharsets.US_ASCII));
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return new JwtProvider(key, issuer, accessTokenTtlSeconds, refreshTokenTtlSeconds);
    }

    /**
     * アクセストークンを発行する。
     * 必須クレーム:
     * <ul>
     *   <li>{@code sub}=userId</li>
     *   <li>{@code tv}=tokenVersion（整数）</li>
     *   <li>{@code iss}, {@code iat}, {@code exp}</li>
     * </ul>
     */
    public String createAccessToken(String userId, int tokenVersion) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenTtlSeconds);
        return Jwts.builder()
            .subject(userId)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .claim("tv", tokenVersion)
            .signWith(secretKey) // 0.12+ : アルゴリズムは鍵から解決
            .compact();
    }

    /**
     * リフレッシュトークンを発行する。
     * 必須クレーム:
     * <ul>
     *   <li>{@code sub}=userId</li>
     *   <li>{@code jti}=refreshId</li>
     *   <li>{@code iss}, {@code iat}, {@code exp}</li>
     * </ul>
     */
    public String createRefreshToken(String userId, String refreshId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTokenTtlSeconds);
        return Jwts.builder()
            .subject(userId)
            .id(refreshId)      // = jti
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(secretKey)
            .compact();
    }

    /**
     * リフレッシュトークンを解析し、{@code sub} と {@code jti} を取り出す。
     * 不正／失効は {@link io.jsonwebtoken.JwtException} などランタイム例外をスロー。
     */
    public ParsedRefresh parseRefresh(String refreshToken) {
        Claims c = Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(refreshToken)
            .getPayload();

        String userId = c.getSubject();
        String refreshId = c.getId();
        if (userId == null || refreshId == null) {
        throw new IllegalArgumentException("refresh token missing required claims (sub/jti).");
        }
        return new ParsedRefresh(userId, refreshId);
    }

    /**
     * リフレッシュトークン解析結果のシンプルな DTO。
     */
    public record ParsedRefresh(String userId, String refreshId) {}
}
