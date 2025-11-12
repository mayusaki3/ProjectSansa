package com.sansa.auth.service.port;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * トークン生成・ローテート・ブラックリスト登録を抽象化。
 * AuthServiceImpl / MfaServiceImpl から呼ばれる。
 */
public interface TokenFacade {

    /** 発行結果DTO（AuthServiceImpl で参照される） */
    class Tokens {
        public String accessToken;
        public String refreshToken;
        public String sessionId;
    }

    /** ローテート結果（AuthServiceImpl で参照される） */
    class RotateResult {
        public String newRefreshToken;
        public Instant rotatedAt;
    }

    Tokens issueTokens(String userId,
                       int tokenVersion,
                       Instant now,
                       Duration accessTtl,
                       Duration refreshTtl,
                       String sessionId,
                       List<String> amr);

    RotateResult rotateRefreshToken(String refreshToken, Instant now);

    void blacklistRefreshToken(String refreshToken, Instant now);

    /** MFA直後の追加AMRで再発行するためのヘルパ（MfaServiceImpl から呼ばれる） */
    Tokens issueAfterAuth(String userId, List<String> amr);
}
