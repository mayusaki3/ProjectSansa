package com.sansa.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * テスト用の User ドメイン簡易モデル。
 *
 * 役割:
 *   - 旧テスト資産の互換のための最小モデル
 *
 * 注意:
 *   - 本番コードの DTO / Store.User とは別物。テスト側の独立モデル。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String accountId;
    private String email;
    private String displayName;

    // 追加が必要になればここに生やす（テスト限定）
}
