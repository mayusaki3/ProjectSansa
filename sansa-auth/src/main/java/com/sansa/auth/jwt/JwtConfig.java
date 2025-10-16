package com.sansa.auth.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT に関する設定値を application.yml / application.properties からバインドするための
 * コンフィグレーションクラス。
 *
 * <h2>役割</h2>
 * <ul>
 *   <li>署名用シークレット（Base64 文字列）</li>
 *   <li>発行者（issuer）</li>
 *   <li>アクセストークンの有効期間（秒）</li>
 *   <li>リフレッシュトークンの有効期間（秒）</li>
 * </ul>
 * を一か所に集約し、{@link JwtProviderConfig} から参照できるようにします。
 *
 * <h2>プロパティ定義例</h2>
 * <pre>
 * sansa:
 *   jwt:
 *     secret:  Base64EncodedSecretHere==   # HMAC 用のキーを Base64 で保持
 *     issuer:  sansa-auth
 *     access-ttl-seconds: 900              # 15 分
 *     refresh-ttl-seconds: 1209600         # 14 日
 * </pre>
 *
 * <h2>検証</h2>
 * <ul>
 *   <li>{@code secret}, {@code issuer} は空文字不可</li>
 *   <li>TTL は 0 より大きい秒数である必要あり</li>
 * </ul>
 *
 * <h2>注意</h2>
 * <ul>
 *   <li>ここでは「トークンバージョン（tv）」は保持しません。アクセストークンの tv は
 *       発行時にアプリ側から値を渡す設計（利用者ごとに異なる／可変のため）です。</li>
 *   <li>シークレットは <b>Base64</b> で保存してください（平文キーを直接入れない）。</li>
 * </ul>
 */
@Validated
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtConfig {

  /** HMAC 署名用の Base64 エンコード済みシークレット。 */
  @NotBlank
  private String secret;

  /** 発行者（iss クレームに設定）。 */
  @NotBlank
  private String issuer;

  /** アクセストークンの TTL（秒）。 */
  @Positive
  private int accessTtlSeconds;

  /** リフレッシュトークンの TTL（秒）。 */
  @Positive
  private int refreshTtlSeconds;

  // ----- getters / setters -----

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

  public int getAccessTtlSeconds() {
    return accessTtlSeconds;
  }

  public void setAccessTtlSeconds(int accessTtlSeconds) {
    this.accessTtlSeconds = accessTtlSeconds;
  }

  public int getRefreshTtlSeconds() {
    return refreshTtlSeconds;
  }

  public void setRefreshTtlSeconds(int refreshTtlSeconds) {
    this.refreshTtlSeconds = refreshTtlSeconds;
  }

  @Override
  public String toString() {
    // secret をログに出さないために伏字化
    String masked = (secret == null || secret.isEmpty())
        ? "(empty)"
        : "****" + secret.length() + "****";
    return "JwtConfig{" +
        "secret=" + masked +
        ", issuer='" + issuer + '\'' +
        ", accessTtlSeconds=" + accessTtlSeconds +
        ", refreshTtlSeconds=" + refreshTtlSeconds +
        '}';
  }
}
