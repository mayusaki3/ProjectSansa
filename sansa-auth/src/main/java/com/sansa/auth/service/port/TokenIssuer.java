package com.sansa.auth.service.port;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 実トークン生成器（JWT等）を抽象化。
 * 実装は環境依存で良い。ここではインタフェースのみ。
 */
public interface TokenIssuer {
    String issueAccessToken(String userId, int tokenVersion, Instant now, Duration accessTtl, List<String> amr);
    String issueRefreshToken(String userId, int tokenVersion, Instant now, Duration refreshTtl);
}
