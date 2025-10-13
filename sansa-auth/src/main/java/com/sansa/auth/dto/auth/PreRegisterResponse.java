package com.sansa.auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /auth/pre-register のレスポンスDTO
 * 役割: 送信受理可否と再試行待機ヒント（ms）を返す。
 * 仕様: 01_ユーザー登録.md 「1) POST /auth/pre-register → PreRegisterResponse」参照。:contentReference[oaicite:3]{index=3}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PreRegisterResponse {
    /** 送信処理を受理したか */
    private boolean success;
    /** レート制御の待機ヒント（ミリ秒。0なら不要） */
    private long throttleMs;
}
