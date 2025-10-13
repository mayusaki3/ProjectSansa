package com.sansa.auth.dto.mfa;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /auth/mfa/recovery/issue のレスポンスDTO
 * 仕様: 04_MFA.md「Recovery issue → MfaRecoveryIssueResponse」
 * フィールド:
 *  - recoveryCodes: string[]（UIは“その場一度だけ表示”）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaRecoveryIssueResponse {
    /** 新規に発行されたリカバリーコード一覧（その場表示のみ） */
    private List<String> recoveryCodes;
}
