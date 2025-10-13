package com.sansa.auth.dto.sessions;

import lombok.Value;
import lombok.Builder;

/**
 * POST /auth/logout のリクエストDTO
 * 仕様: 05_セッション管理.md 「D) POST /auth/logout → LogoutRequest」参照。:contentReference[oaicite:18]{index=18}
 *
 * 両フィールドは任意:
 * - refreshToken: RT指定でそのセッションを失効
 * - sessionId: 明示IDで対象指定（どちらも省略時は“現セッション”扱いは実装依存）
 */
@Value
@Builder
public class LogoutRequest {
    String refreshToken;
    String sessionId;
}
