package com.sansa.auth.dto.sessions;

import lombok.Value;
import lombok.Builder;
import java.util.List;

/**
 * GET /sessions のレスポンスDTO
 * 仕様: 05_セッション管理.md 「B) GET /sessions → sessions: SessionInfo[]」参照。:contentReference[oaicite:16]{index=16}
 */
@Value
@Builder
public class SessionsListResponse {
    /** 自アカウントのセッション一覧 */
    List<SessionInfo> sessions;
}
