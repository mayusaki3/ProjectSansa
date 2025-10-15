package com.sansa.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 関連のアプリ設定を型安全に束ねるクラス。
 *
 * <p>application.yml の例</p>
 * <pre>
 * sansa:
 *   jwt:
 *     secret: "base64:xxxxxxxx..."   # or 任意のプレーン文字列（十分な長さ）
 *     issuer: "sansa-auth"
 *     access-minutes: 15
 *     refresh-days: 7
 * </pre>
 *
 * <p>注意</p>
 * <ul>
 *   <li>「base64:」で始まる場合は Base64 と解釈（{@link JwtProviderConfig} 側でデコード）。</li>
 *   <li>本クラスは {@code @ConfigurationProperties} のみ。Bean として有効化するのは
 *       {@link JwtProviderConfig} または {@link com.sansa.auth.config.ServiceWiringConfig} の
 *       {@code @EnableConfigurationProperties(JwtConfig.class)} で行います。</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "sansa.jwt")
public class JwtConfig {

    /** 署名鍵。先頭が "base64:" の場合は Base64 エンコード文字列として扱う。 */
    private String secret;

    /** iss クレームに入れる発行者名。 */
    private String issuer = "sansa-auth";

    /** アクセストークンの有効期間（分）。 */
    private int accessMinutes = 15;

    /** リフレッシュトークンの有効期間（日）。 */
    private int refreshDays = 7;

    // ---- getters / setters ----

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public int getAccessMinutes() {
        return accessMinutes;
    }

    public void setAccessMinutes(int accessMinutes) {
        this.accessMinutes = accessMinutes;
    }

    public int getRefreshDays() {
        return refreshDays;
    }

    public void setRefreshDays(int refreshDays) {
        this.refreshDays = refreshDays;
    }
}
