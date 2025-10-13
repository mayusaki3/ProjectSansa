package com.sansa.auth.dto.auth;

import lombok.*;

/**
 * POST /auth/verify-email のレスポンスDTO
 * 役割: 登録続行のための preRegId と有効秒数を返す。
 * 仕様: 01_ユーザー登録.md 「2) POST /auth/verify-email → VerifyEmailResponse」参照。:contentReference[oaicite:7]{index=7}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VerifyEmailResponse {
    /** 登録続行のための一時ID（UUID文字列） */
    private String preRegId;
    /** preRegId の残存有効秒数 */
    private int expiresIn;
}
