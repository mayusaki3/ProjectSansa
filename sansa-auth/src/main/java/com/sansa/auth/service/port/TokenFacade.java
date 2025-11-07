package com.sansa.auth.service.port;

import java.time.Duration;
import java.time.Instant;

public interface TokenFacade {

    /**
     * アクセストークン／リフレッシュトークン一式
     */
    record Tokens(
            String accessToken,
            String refreshToken,
            Instant accessTokenExpiresAt,
            Instant refreshTokenExpiresAt,
            String sessionId
    ) {}

    /**
     * RTローテーション結果
     */
    record RotateResult(
            boolean ok,
            Tokens tokens,
            boolean tokenExpired,
            boolean tokenReused,
            String error // 任意の内部メッセージ
    ) {
        public static RotateResult success(Tokens tokens) {
            return new RotateResult(true, tokens, false, false, null);
        }

        public static RotateResult expired(String msg) {
            return new RotateResult(false, null, true, false, msg);
        }

        public static RotateResult reused(String msg) {
            return new RotateResult(false, null, false, true, msg);
        }

        public static RotateResult invalid(String msg) {
            return new RotateResult(false, null, false, false, msg);
        }
    }

    /**
     * ログイン／登録時:
     * userId + tokenVersion(tv) + TTLを元に AT/RT を発行。
     * sessionId は呼び出し側(AuthService)で採番済みのものを受け取る。
     */
    Tokens issueTokens(
            String userId,
            int tokenVersion,
            Instant now,
            Duration accessTtl,
            Duration refreshTtl,
            String sessionId
    );

    /**
     * RTローテーション:
     * - refreshToken を検証
     * - 有効なら新しい RT/AT を発行し、Store.rotateRefreshToken(...) で一意性を更新
     * - 再利用検知時は tv++（Store.incrementTokenVersion）まで含めて処理し、reused を返却
     * - 期限切れは expired を返却
     * - 不正トークン等は invalid を返却
     */
    RotateResult rotateRefreshToken(String refreshToken, Instant now);

    /**
     * 任意の RT(JTI) を失効させるためのフック。
     * /auth/logout などで使用。
     * 具体的な保持方法は実装側と Store に委譲。
     */
    void blacklistRefreshToken(String refreshTokenId, Instant expiresAt);
}
