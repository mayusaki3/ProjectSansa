package com.sansa.auth.dto.sessions;

import lombok.Value;
import lombok.Builder;
import java.util.List;

/**
 * GET /auth/session, GET /sessions の要素DTO
 * 仕様: 05_セッション管理.md 「A) GET /auth/session → SessionInfo」参照。:contentReference[oaicite:13]{index=13}
 */
@Value
@Builder
public class SessionInfo {
    /** セッションが有効か */
    boolean active;
    /** セッションID */
    String sessionId;
    /** 発行日時（ISO-8601文字列） */
    String issuedAt;
    /** 最終アクティブ（ISO-8601文字列） */
    String lastActive;
    /** 失効時刻（ISO-8601文字列） */
    String expiresAt;
    /** 認証手段（amr）例: ["pwd","mfa"] */
    List<String> amr;
    /** ユーザー概要 */
    UserSummary user;

    /**
     * ユーザー概要
     * 仕様: userId, email, displayName。:contentReference[oaicite:14]{index=14}
     */
    @Value
    @Builder
    public static class UserSummary {
        String userId;
        String email;
        String displayName;
    }
}
