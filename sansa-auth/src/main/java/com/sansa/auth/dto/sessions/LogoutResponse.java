package com.sansa.auth.dto.sessions;

import lombok.Value;
import lombok.Builder;

/**
 * POST /auth/logout のレスポンスDTO
 * 仕様: 05_セッション管理.md 「D) POST /auth/logout → LogoutResponse(success)」参照。:contentReference[oaicite:20]{index=20}
 */
@Value
@Builder
public class LogoutResponse {
    boolean success;
}
