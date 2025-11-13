package com.sansa.auth.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * JWT関連の共通設定クラス。
 *
 * 現段階ではシステム時刻の依存注入のみを担当。
 * TokenIssuer 等のBean定義は service.port 側で行う構成に統一。
 */
@Configuration
public class JwtProviderConfig {

    /**
     * システムUTC時計をBean化。
     * - テスト時に Clock.fixed(...) で差し替え可能。
     * - TokenIssuerImpl などで発行時刻を統一的に扱える。
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
