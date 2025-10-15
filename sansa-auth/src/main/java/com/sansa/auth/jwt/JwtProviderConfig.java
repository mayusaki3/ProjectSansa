package com.sansa.auth.jwt;

import com.sansa.auth.util.JwtProvider;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT の低レイヤ機能（署名/検証/パース）を提供する {@link JwtProvider} を組み立てる設定クラス。
 *
 * <p>設計方針</p>
 * <ul>
 *   <li>{@code jwtProvider} は <b>ここだけ</b>で定義する（重複 Bean 回避）。</li>
 *   <li>鍵素材は {@link JwtConfig} から受け取り、必要に応じて Base64 デコード。</li>
 *   <li>依存している JJWT ライブラリは 0.11/0.12 いずれでも動くよう、鍵生成は {@link Keys#hmacShaKeyFor(byte[])} を使用。</li>
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class JwtProviderConfig {

    private final JwtConfig jwtConfig;

    public JwtProviderConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * 設定の妥当性を軽くチェック。
     * 本格的な検証は起動時例外にしておくと原因が追いやすい。
     */
    @PostConstruct
    void validate() {
        if (jwtConfig.getSecret() == null || jwtConfig.getSecret().isBlank()) {
        throw new IllegalStateException("sansa.jwt.secret must be set");
        }
        if (jwtConfig.getAccessMinutes() <= 0) {
        throw new IllegalStateException("sansa.jwt.access-minutes must be > 0");
        }
        if (jwtConfig.getRefreshDays() <= 0) {
        throw new IllegalStateException("sansa.jwt.refresh-days must be > 0");
        }
    }

    /**
     * 署名用の SecretKey。
     * <ul>
     *   <li>secret が "base64:" で始まる→ Base64 デコード後に HMAC 用鍵を生成。</li>
     *   <li>それ以外→ UTF-8 バイト列から HMAC 用鍵を生成。</li>
     * </ul>
     */
    @Bean(name = "jwtSecretKey")
    public SecretKey jwtSecretKey() {
        final String raw = jwtConfig.getSecret();
        final byte[] keyBytes;
        if (raw.startsWith("base64:")) {
        keyBytes = Base64.getDecoder().decode(raw.substring("base64:".length()));
        } else {
        keyBytes = raw.getBytes(StandardCharsets.UTF_8);
        }
        // HMAC-SHA 用の鍵を生成（HS256/384/512 いずれにも使える）
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * アプリ全体で使う JwtProvider。
     * <p>注意：この Bean 名（"jwtProvider"）は他で重複定義しないこと。</p>
     */
    @Bean(name = "jwtProvider")
    public JwtProvider jwtProvider(SecretKey jwtSecretKey) {
        return new JwtProvider(
            jwtSecretKey,
            jwtConfig.getIssuer(),
            jwtConfig.getAccessMinutes(),
            jwtConfig.getRefreshDays());
    }
}
