package com.sansa.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * JWTの発行/検証のみを担う薄いユーティリティ。
 * 仕様（テスト/設定に合わせた公開API）:
 * - static JwtProvider fromBase64Secret(String secretB64, String issuer, int accessTtlSec, int refreshTtlSec)
 * - String createAccessToken(String subject)
 * - String createRefreshToken(String subject, String jti)
 * - ParsedRefresh parseRefresh(String token) : userId(subject) と jti と tokenVersion を返す
 *
 * 注意:
 * - ここでは「アクセストークン」「リフレッシュトークン」でTTLを分ける。
 * - 追加クレームは最小化（subject, issuer, exp, iat, jti）に留める。
 */
public final class JwtProvider {

    private final SecretKey key;
    private final String issuer;
    private final int accessTtlSec;
    private final int refreshTtlSec;

    public JwtProvider(SecretKey key, String issuer, int accessTtlSec, int refreshTtlSec) {
        this.key = key;
        this.issuer = issuer;
        this.accessTtlSec = accessTtlSec;
        this.refreshTtlSec = refreshTtlSec;
    }

    /** 設定ファイルのBase64秘密鍵を受け取り、プロバイダを生成 */
    public static JwtProvider fromBase64Secret(String secretB64, String issuer, int accessTtlSec, int refreshTtlSec) {
        byte[] keyBytes = Decoders.BASE64.decode(secretB64);
        SecretKey k = Keys.hmacShaKeyFor(keyBytes);
        return new JwtProvider(k, issuer, accessTtlSec, refreshTtlSec);
    }

    /** アクセストークン発行（短命）。subject は userId 等を想定。 */
    public String createAccessToken(String subject) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtlSec);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * リフレッシュトークン発行（長命）。subject = userId, jti = リフレッシュID。
     * 実装メモ: jti を必ず入れて、サーバ側の失効/ローテートと突き合わせ可能にする。
     */
    public String createRefreshToken(String subject, String jti, int tokenVersion) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTtlSec);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setId(jti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("tokenVersion", tokenVersion)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * リフレッシュのパース（署名・exp検証を含む）。
     * 成功時: subject(userId), jti, tokenVersion を返す。
     */
    public ParsedRefresh parseRefresh(String token) {
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        Claims c = jws.getBody();
        String userId = c.getSubject();
        String jti = c.getId();
        Integer tv = c.get("tokenVersion", Integer.class);
        if (tv == null) throw new JwtException("tokenVersion claim missing");
        int tokenVersion = tv.intValue();
        return new ParsedRefresh(userId, jti, tokenVersion);
    }

    /** 解析結果の薄いDTO（必要最小限） */
    public record ParsedRefresh(String userId, String jti, int tokenVersion) {}

    public int getAccessTtlSec() { return accessTtlSec; }

    public int getRefreshTtlSec() { return refreshTtlSec; }

    public String getIssuer() { return issuer; }
}
