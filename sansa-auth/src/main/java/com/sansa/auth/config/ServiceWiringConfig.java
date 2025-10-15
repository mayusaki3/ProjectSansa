package com.sansa.auth.config;

import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.util.TokenIssuer;
import com.sansa.auth.util.impl.TokenIssuerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Service層のDI配線。
 * - JwtProvider と TokenIssuer（実装）を1箇所で生成
 * - AuthServiceImpl からは TokenIssuer だけを参照させる
 */
@Configuration
@RequiredArgsConstructor
public class ServiceWiringConfig {

    @Value("${auth.jwt.secret-base64}")
    private final String jwtSecretBase64;

    @Value("${auth.jwt.issuer}")
    private final String jwtIssuer;

    @Value("${auth.jwt.access-ttl-sec:900}")   // 15min
    private final int accessTtlSec;

    @Value("${auth.jwt.refresh-ttl-sec:1209600}") // 14days
    private final int refreshTtlSec;

    /** JwtProvider（4引数シグネチャに合わせる） */
    @Bean
    public JwtProvider jwtProvider() {
        return JwtProvider.fromBase64Secret(jwtSecretBase64, jwtIssuer, accessTtlSec, refreshTtlSec);
    }

    /** TokenIssuer 実装（JwtProviderに委譲） */
    @Bean
    public TokenIssuer tokenIssuer(JwtProvider jwtProvider) {
        return new TokenIssuerImpl(jwtProvider);
    }

    /** PasswordHasher（BCrypt）。AuthServiceImplで @Autowired 先に合わせる軽量インタフェースを提供するなら適宜置換 */
    @Bean
    public PasswordHasher passwordHasher() {
        return new PasswordHasher() {
            @Override public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt()); }
            @Override public boolean verify(String raw, String hashed) {
                try { return BCrypt.checkpw(raw, hashed); } catch (Exception e) { return false; }
            }
        };
    }

    /** 最小限のハッシュ契約（AuthServiceImplが期待するメソッド名に合わせる） */
    public interface PasswordHasher {
        String hash(String raw);
        boolean verify(String raw, String hashed);
    }
}
