package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.TokenIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenFacadeImpl implements TokenFacade {

    private final Store store;
    private final TokenIssuer tokenIssuer;

    // 本来は設定値から取得
    private static final Duration DEFAULT_ACCESS_TTL = Duration.ofMinutes(15);
    private static final Duration DEFAULT_REFRESH_TTL = Duration.ofDays(7);

    @Override
    public Tokens issueTokens(
            String userId,
            int tokenVersion,
            Instant now,
            Duration accessTtl,
            Duration refreshTtl,
            String sessionId
    ) {
        Duration atTtl = accessTtl != null ? accessTtl : DEFAULT_ACCESS_TTL;
        Duration rtTtl = refreshTtl != null ? refreshTtl : DEFAULT_REFRESH_TTL;

        // RT用の一意なID(JTI)生成
        String refreshId = tokenIssuer.newRefreshId();

        String accessToken = tokenIssuer.issueAccessToken(userId, tokenVersion, now, atTtl, sessionId);
        String refreshToken = tokenIssuer.issueRefreshToken(userId, tokenVersion, now, rtTtl, refreshId);

        Instant atExp = now.plus(atTtl);
        Instant rtExp = now.plus(rtTtl);

        // RT一意性は rotateRefreshToken 時に検証する想定。
        // 必要ならここで store.registerRefreshToken(userId, refreshId, rtExp) を追加する。

        return new Tokens(accessToken, refreshToken, atExp, rtExp, sessionId);
    }

    @Override
    public RotateResult rotateRefreshToken(String refreshToken, Instant now) {
        // 1. パース・署名検証
        var parsed = tokenIssuer.parseRefreshToken(refreshToken);
        if (!parsed.valid()) {
            if (parsed.expired()) {
                return RotateResult.expired("token_expired");
            }
            return RotateResult.invalid("invalid_token");
        }

        String userId = parsed.userId();
        int tokenVersionInToken = parsed.tokenVersion();
        String oldRtId = parsed.refreshId();

        // 2. 現在のtv確認
        int currentTv = store.getTokenVersion(userId);
        if (tokenVersionInToken != currentTv) {
            // すでにtvが進んでいる → 全端末失効後 or 再利用済み
            return RotateResult.reused("token_reused");
        }

        // 3. 新しいRT ID発行
        String newRtId = tokenIssuer.newRefreshId();

        // 4. RTローテーション (Store側で old→new を更新。falseなら再利用検知)
        boolean rotated = store.rotateRefreshToken(userId, oldRtId, newRtId, now);
        if (!rotated) {
            // 再利用検知 → tv++
            store.incrementTokenVersion(userId);
            return RotateResult.reused("token_reused");
        }

        // 5. 新AT/RT発行
        String sessionId = parsed.sessionId();
        var tokens = issueTokens(
                userId,
                currentTv,
                now,
                DEFAULT_ACCESS_TTL,
                DEFAULT_REFRESH_TTL,
                sessionId
        );
        return RotateResult.success(tokens);
    }

    @Override
    public void blacklistRefreshToken(String refreshTokenId, Instant expiresAt) {
        // 09_token_blacklist.md に合わせたストアI/Fを将来追加:
        // store.blacklistToken(refreshTokenId, expiresAt);
        // 現状は未使用なら no-op 実装でも良いが、本番実装では必ずStore連携する。
    }
}
