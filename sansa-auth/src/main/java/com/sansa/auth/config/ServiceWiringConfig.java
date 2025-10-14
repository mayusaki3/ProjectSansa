package com.sansa.auth.config;

import com.sansa.auth.service.impl.AuthServiceImpl;
import com.sansa.auth.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Service層のDI配線。
 * - JwtProvider.fromBase64Secret(secret, issuer, accessTtlSec, refreshTtlSec) の4引数シグネチャに合わせる
 * - TokenIssuer は1定義のみ（重複Bean回避）
 */
@Configuration
@RequiredArgsConstructor
public class ServiceWiringConfig {

    // === JWT 設定 ===
    @Value("${jwt.secretBase64}")
    private String secretBase64;

    @Value("${jwt.issuer:sansa-auth}")
    private String issuer;

    @Value("${jwt.accessTtlSec:900}")     // 15min
    private int accessTtlSec;

    @Value("${jwt.refreshTtlSec:604800}") // 7d
    private int refreshTtlSec;

    /** JwtProvider をプロパティから生成（※ audience は不要） */
    @Bean
    public JwtProvider jwtProvider() {
        // ★ シグネチャ: (secretBase64, issuer, accessTtlSec, refreshTtlSec)
        return JwtProvider.fromBase64Secret(secretBase64, issuer, accessTtlSec, refreshTtlSec);
    }

    /** JwtProvider を薄く包む TokenIssuer */
    @Bean
    public AuthServiceImpl.TokenIssuer tokenIssuer(JwtProvider jwtProvider) {
        return new AuthServiceImpl.DefaultTokenIssuer(jwtProvider);
    }

    /** PasswordHasher (BCrypt) */
    @Bean
    public AuthServiceImpl.PasswordHasher passwordHasher() {
        return new AuthServiceImpl.PasswordHasher() {
            @Override public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt()); }
            @Override public boolean verify(String raw, String hashed) {
                try { return BCrypt.checkpw(raw, hashed); } catch (Exception e) { return false; }
            }
        };
    }
}
