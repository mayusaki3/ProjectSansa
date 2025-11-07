package com.sansa.auth.dto.sessions;

import lombok.Value;
import lombok.Builder;

/**
 * POST /auth/logout のレスポンスDTO
 * 仕様: 05_セッション管理.md 「D) POST /auth/logout → LogoutResponse(success)」参照。
 */
@Value
@Builder
public class LogoutResponse {
    boolean success;

    /**
     * ログアウト結果を返すファクトリメソッド。
     * @param success ログアウト処理で何らかの対象を失効させられた場合true
     * @return LogoutResponse
     */
    public static LogoutResponse ok(boolean success) {
        return LogoutResponse.builder()
                .success(success)
                .build();
    }
}
