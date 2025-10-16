package com.sansa.auth.jwt;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import com.sansa.auth.util.impl.TokenIssuerImpl;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * JWT 関連の Bean を束ねる設定クラス。
 *
 * <h2>このクラスの役割</h2>
 * <ul>
 *   <li>{@link JwtConfig} に載っているアプリ設定値（secret/issuer/TTL）を受け取り、</li>
 *   <li>署名鍵 {@link SecretKey} を生成し、</li>
 *   <li>トークン操作用の {@link JwtProvider} を生成、</li>
 *   <li>アプリが直接使うファサード {@link TokenIssuer} を公開する。</li>
 * </ul>
 *
 * <h2>重要: 重複定義を避ける</h2>
 * <p>
 * {@code ServiceWiringConfig} に <b>jwtProvider()</b> を定義していると
 * Bean 名の衝突（<code>The bean 'jwtProvider' ... overriding is disabled</code>）が発生します。
 * <b>jwtProvider の定義は本クラスに一本化</b>してください（ServiceWiringConfig からは削除）。
 * </p>
 *
 * <h2>プロパティの例（application.yml）</h2>
 * <pre>
 * sansa:
 *   jwt:
 *     secret:  Base64EncodedSecretHere==
 *     issuer:  sansa-auth
 *     access-ttl-seconds: 900        # 15 min
 *     refresh-ttl-seconds: 1209600   # 14 days
 * </pre>
 *
 * <h2>アクセストークン/リフレッシュトークンのクレーム方針</h2>
 * <ul>
 *   <li>アクセストークン: {@code sub}=userId, {@code tv}=tokenVersion</li>
 *   <li>リフレッシュトークン: {@code sub}=userId, {@code jti}=refreshId</li>
 * </ul>
 * これらの物理クレーム名の扱いは {@link JwtProvider} に閉じ込めています。
 */
@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class JwtProviderConfig {

  /**
   * Base64 文字列（ランダム十分長）のシークレットから HMAC 用の {@link SecretKey} を生成。
   *
   * <p>注意: 平文キーではなく <b>Base64</b> で受け取る想定です。</p>
   */
  @Bean
  public SecretKey jwtSecretKey(JwtConfig cfg) {
    byte[] keyBytes = Base64.getDecoder().decode(cfg.getSecret());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 署名鍵・発行者・TTL を束ねた {@link JwtProvider}。
   * <p>
   * TokenIssuerImpl からは本プロバイダの「発行／解析」メソッドだけを使います。
   * </p>
   */
  @Bean
  public JwtProvider jwtProvider(SecretKey jwtSecretKey, JwtConfig cfg) {
    return new JwtProvider(
        jwtSecretKey,
        cfg.getIssuer(),
        cfg.getAccessTtlSeconds(),
        cfg.getRefreshTtlSeconds()
    );
  }

  /**
   * アプリ側が直接利用するファサード。
   * <p>
   * アクセストークン発行時の {@code tokenVersion(tv)} は呼び出し側から引数で受け取ります
   * （デフォルト値を固定したい場合は、アプリ層でラップするか設定値を別途注入してください）。
   * </p>
   */
  @Bean
  public TokenIssuer tokenIssuer(JwtProvider jwtProvider) {
    return new TokenIssuerImpl(jwtProvider);
  }
}
