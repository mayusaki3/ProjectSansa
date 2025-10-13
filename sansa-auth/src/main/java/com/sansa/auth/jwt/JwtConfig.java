package com.sansa.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 設定のプロパティクラス。
 *
 * <p>想定プロパティ:
 * <ul>
 *   <li>{@code jwt.secret} — 署名用シークレット（本番は十分な長さ・エントロピー必須）</li>
 *   <li>{@code jwt.issuer} — 発行者（{@code iss}）クレーム</li>
 *   <li>{@code jwt.accessTokenMinutes} — アクセストークンの有効期限（分）</li>
 *   <li>{@code jwt.refreshTokenDays} — リフレッシュトークンの有効期限（日）</li>
 * </ul>
 *
 * <p>注意:
 * <ul>
 *   <li>Getter/Setter は Lombok でも手書きでも可（現在はPOJO想定）</li>
 *   <li>本番では値のバリデーション（空・桁不足など）を強めることを推奨</li>
 * </ul>
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * Base64 でエンコードされた共有秘密鍵（HS256/HS512 用）
     * 例: application-*.yml で jwt.secret: "xxxxx(base64)xxxxx"
     */
    private String secret;

    /** iss (Issuer) */
    private String issuer;

    /** アクセストークンの有効期間（分） */
    private int accessTokenMinutes;

    /** リフレッシュトークンの有効期間（日） */
    private int refreshTokenDays;

    // ---- getters ----
    public String getSecret() { return secret; }
    public String getIssuer() { return issuer; }
    public int getAccessTokenMinutes() { return accessTokenMinutes; }
    public int getRefreshTokenDays() { return refreshTokenDays; }

    // ---- setters ----
    public void setSecret(String secret) { this.secret = secret; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public void setAccessTokenMinutes(int accessTokenMinutes) { this.accessTokenMinutes = accessTokenMinutes; }
    public void setRefreshTokenDays(int refreshTokenDays) { this.refreshTokenDays = refreshTokenDays; }
}
