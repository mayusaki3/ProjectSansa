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

/**
 * JWT の発行／検証を行うユーティリティ。
 *
 * <p>主な提供機能（実装に応じて）:
 * <ul>
 *   <li>アクセストークン発行（subject, scope, セッション情報などをクレームに含める）</li>
 *   <li>リフレッシュトークン発行</li>
 *   <li>署名検証・期限切れ検出・クレーム抽出</li>
 * </ul>
 *
 * <p>設計上の注意:
 * <ul>
 *   <li>スレッドセーフ: 不変フィールドのみを保持することで並行利用可</li>
 *   <li>時刻基準: サーバ時刻依存のため、NTP等で時刻同期を行うこと</li>
 *   <li>Clock Skew: 若干の時刻ずれを許容したい場合は余裕秒を考慮（検証側で調整）</li>
 *   <li>キー管理: {@code secret} は本番で環境依存の安全な保管・ローーテーション計画を持つ</li>
 * </ul>
 */
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

    /**
     * アクセストークンを発行する。
     *
     * @param subject サブジェクト（例: userId）
     * @param claims  追加クレーム（ロール・セッションID等）。不要なら {@code null} 可
     * @return 署名済みJWT文字列
     * @throws IllegalStateException 設定が不正な場合など
     */
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

    /**
     * リフレッシュトークンを発行する。
     *
     * @param subject      サブジェクト（例: userId）
     * @param tokenVersion トークンバージョン（無効化管理用）。不要なら {@code null} 可
     * @return 署名済みJWT文字列
     * @throws IllegalStateException 設定が不正な場合など
     */
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

    /**
     * JWTを解析し、署名検証とクレーム抽出を行う。
     *
     * @param token 署名済みJWT文字列
     * @return クレームオブジェクト
     * @throws io.jsonwebtoken.security.SecurityException 署名検証失敗
     * @throws io.jsonwebtoken.ExpiredJwtException 期限切れ
     * @throws io.jsonwebtoken.MalformedJwtException 不正な形式
     * @throws io.jsonwebtoken.UnsupportedJwtException サポート外のJWT
     * @throws IllegalArgumentException 引数が不正
     */
    public Claims parse(String token) {
        // parserBuilder は verifyWith(SecretKey/PublicKey) しか受けない
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Base64エンコードされたシークレットから JwtProvider を構築するユーティリティ。
     *
     * <p>HS256/HS512 などの対称鍵アルゴリズムを想定。実際の運用では安全な方法でキーを管理すること。
     *
     * @param base64Secret       Base64エンコードされたシークレット文字列
     * @param issuer             トークン発行者識別子
     * @param accessTokenMinutes アクセストークンの有効期間（分）
     * @param refreshTokenDays   リフレッシュトークンの有効期間（日）
     * @return JwtProvider インスタンス
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public static JwtProvider fromBase64Secret(String base64Secret, String issuer, int accessTokenMinutes, int refreshTokenDays) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return new JwtProvider(secretKey, issuer, accessTokenMinutes, refreshTokenDays);
    }

    /**
     * Claims を Map に変換するユーティリティ。
     * jjwt 0.12 では Claims 自体が Map を継承していないため、コピーして返す。
     *
     * @param claims Claims オブジェクト
     * @return クレームの Map 表現
     */
    public Map<String, Object> claimsAsMap(Claims claims) {
        // 0.12 には asMap() がないため、コピーして返す
        return new HashMap<>(claims);
    }
}
