package com.sansa.auth.dto.auth;

import lombok.*;

/**
 * POST /auth/register のレスポンスDTO
 * 役割: 作成結果と内部ユーザーID等を返す。
 * 仕様: 01_ユーザー登録.md 「3) POST /auth/register → RegisterResponse」参照。:contentReference[oaicite:11]{index=11}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegisterResponse {
    /** 登録成功フラグ */
    private boolean success;
    /** 内部ユーザーID */
    private String userId;
    /** メール検証済みフラグ（通常 true） */
    private boolean emailVerified;
}
