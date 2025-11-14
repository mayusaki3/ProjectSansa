package com.sansa.auth.service.port;

import java.time.Instant;

/**
 * JWT 発行・検証ポート
 */
public interface TokenIssuer {

    record AccessTokens(String token, String jti, Instant expiresAt) {}
    record RefreshTokens(String token, String jti, Instant expiresAt) {}

    /** refresh のパース結果（必要最小セットに統一） */
    record RefreshParseResult(String userId, String refreshId) {}

    AccessTokens issueAccess(String userId, String sessionId, String accessJti, String[] scopes, int ttlSeconds);
    RefreshTokens issueRefresh(String userId, String sessionId, String refreshJti, int ttlSeconds);
    RefreshParseResult parseRefresh(String refreshToken);
}
