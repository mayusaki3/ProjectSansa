package com.sansa.auth.config;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import com.sansa.auth.util.impl.TokenIssuerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * アプリ内サービス（ユーティリティ層）の「配線」用コンフィグ。
 *
 * <h2>方針</h2>
 * <ul>
 *   <li>このクラスでは <b>JwtProvider の Bean は定義しません</b>。
 *       すでに {@code JwtProviderConfig} が提供しているため、ここで定義すると
 *       Bean 名の衝突（overriding 禁止時の起動失敗）を招きます。</li>
 *   <li>ここでは、既存の Bean（例：{@link JwtProvider}）を受け取り、
 *       それに依存する軽量サービス（例：{@link TokenIssuer}）を組み立てます。</li>
 *   <li>コンストラクタやフィールドに <code>String</code> 等のプリミティブな型を
 *       直接 @Autowired しないことで、
 *       「No qualifying bean of type 'java.lang.String'」エラーを防ぎます。
 *       必要があれば {@code @Value} か {@code @ConfigurationProperties} を
 *       専用の別クラス（例：{@code JwtConfig}）で扱ってください。</li>
 * </ul>
 */
@Configuration
public class ServiceWiringConfig {

  /**
   * TokenIssuer の組み立て。
   *
   * <p>引数の {@link JwtProvider} はメソッド引数インジェクションにより
   * Spring が既存の Bean を解決して渡します。
   * ここで新たに {@code jwtProvider()} を定義しないことで、重複定義を避けます。</p>
   */
  @Bean
  public TokenIssuer tokenIssuer(JwtProvider jwtProvider) {
    // 実体は薄いファサード。JwtProvider の API に合わせるだけ。
    return new TokenIssuerImpl(jwtProvider);
  }

  /*
   * ここに他サービスの配線を追加したい場合の注意点:
   * - 既存の @Configuration や @Component により同名 Bean が
   *   すでに存在しないかを確認すること。
   * - 原則として、ドメイン/アプリサービスの実装クラスが @Service で
   *   自己登録しているなら、ここで二重に @Bean を作らないこと。
   * - 外部設定値（issuer、期限、鍵素材など）は本クラスでは終端せず、
   *   専用の Config クラスで束ねたうえで、利用側に注入すること。
   */
}
