package com.sansa.auth.config;

import com.sansa.auth.util.JwtProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

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
