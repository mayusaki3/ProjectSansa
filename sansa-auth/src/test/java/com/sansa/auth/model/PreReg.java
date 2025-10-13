package com.sansa.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * テスト用の PreReg（仮登録）ドメイン簡易モデル。
 *
 * 役割:
 *   - メール検証～登録へ進むための preRegId と TTL を扱う
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReg {
    private String preRegId;
    private String email;
    /** 期限（エポックミリ秒） */
    private long expiresAt;
    /** 1回消費フラグ */
    private boolean consumed;
}
