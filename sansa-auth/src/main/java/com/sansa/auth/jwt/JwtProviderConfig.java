package com.sansa.auth.jwt;

// Spring / Config
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 既存の JWT 実装に必要な import 群（プロジェクト既存の型に合わせる）
import java.time.Clock;

// ★修正点: 存在しない com.sansa.auth.session.port ではなく util を import
// import com.sansa.auth.session.port.TokenIssuer; // NG: 不存在
import com.sansa.auth.util.TokenIssuer;

// TokenIssuer の実装を Bean 登録するなら（既存構成に合わせて）:
import com.sansa.auth.service.port.impl.TokenIssuerImpl;

/**
 * JWT プロバイダ関連の Spring コンフィグ。
 *
 * ポイント:
 * - TokenIssuer は外部ライブラリ詳細から呼び出し側を分離するための薄いファサード。
 * - ここでは Bean 構成のみ行い、アルゴリズム詳細は TokenIssuerImpl 側へ委譲。
 */
@Configuration
public class JwtProviderConfig {

    /**
     * システム時計。テスト容易性のため Bean 化。
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

    /**
     * TokenIssuer の Bean。
     *
     * 注意:
     * - 既存の秘密鍵や署名アルゴリズムの注入が別 Bean である場合は、
     *   その Bean をコンストラクタに渡す形に変更してください。
     * - ここでは最小構成として Clock のみ注入する例を示す。
     */
    @Bean
    public TokenIssuer tokenIssuer(Clock clock) {
        // 既存の鍵/アルゴリズム Bean があるなら適宜差し替え:
        // return new TokenIssuerImpl(clock, signingKey, verifyKey, algorithm, issuer, audience);
        return new TokenIssuerImpl(clock);
    }
}
