package com.sansa.auth.jwt;

import com.sansa.auth.util.JwtProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

/**
 * JWT 関連の Bean 定義。
 *
 * <p>{@link JwtConfig} から読み込んだ設定値（secret / issuer / 有効期限）を使って
 * {@link JwtProvider} を組み立て、アプリ全体で DI 可能にする。
 *
 * <p>注意:
 * <ul>
 *   <li>外部環境では secret を必ず環境変数や Secret Manager から注入すること</li>
 *   <li>テストでは短い有効期限／ダミー secret を使うと検証が容易</li>
 * </ul>
 */
@Configuration
public class JwtProviderConfig {

    @Bean
    public JwtProvider jwtProvider(JwtConfig cfg) {
        // HS 系の共有鍵を SecretKey に
        byte[] keyBytes = Decoders.BASE64.decode(cfg.getSecret());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        return new JwtProvider(
                secretKey,
                cfg.getIssuer(),
                cfg.getAccessTokenMinutes(),
                cfg.getRefreshTokenDays()
        );
    }
}
