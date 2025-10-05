package com.sansa.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
