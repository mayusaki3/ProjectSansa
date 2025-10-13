package com.sansa.auth.dto.mfa;

import lombok.Data;

/**
 * POST /auth/mfa/email/send のリクエストDTO
 * 仕様: 04_MFA.md「Email send」
 * 備考: ボディは実装方針により省略可のため、現状フィールドなしの空DTO。
 *       将来、言語/送信先などのメタ情報を追加する拡張余地あり。
 */
@Data
public class MfaEmailSendRequest {
    // ボディ省略可（仕様）につき、現状フィールドなし
}
