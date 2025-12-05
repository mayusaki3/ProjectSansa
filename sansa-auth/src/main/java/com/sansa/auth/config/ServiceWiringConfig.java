package com.sansa.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * サービス層の共通設定クラス。
 *
 * 現時点では、パスワードハッシュ用の Argon2 エンコーダのみを
 * Spring Bean として公開する。
 *
 * AuthService / MfaService / SessionService / TokenFacade / PasswordPort などの
 * サービス配線は、ポート定義と実装が固まり次第、別途追加する。
 */
@Configuration
public class ServiceWiringConfig {

    /**
     * アカウントパスワードのハッシュに使用する Argon2 エンコーダ。
     *
     * パラメータは後でチューニング可能なように、
     * とりあえず Spring Security 5.8 想定のデフォルト値を利用する。
     */
    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
