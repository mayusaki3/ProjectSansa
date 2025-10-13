package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.mfa.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.UnauthorizedException;

/**
 * 多要素認証（TOTP / Email OTP / Recovery）（/auth/mfa 配下）。
 * 参照仕様: 04_MFA.md（TOTP, Email OTP, Recovery）※ログイン時の成功レスポンスは LoginResponse に統一 :contentReference[oaicite:20]{index=20}
 */
public interface MfaService {

    // ---- TOTP ----

    /** TOTP 秘密鍵の発行（登録）。POST /auth/mfa/totp/enroll
     *  仕様: 04_MFA.md 「TOTP enroll → MfaTotpEnrollResponse」 :contentReference[oaicite:21]{index=21}
     */
    MfaTotpEnrollResponse totpEnroll()
            throws UnauthorizedException, BadRequestException;

    /** TOTP 有効化（初回コード確認）。POST /auth/mfa/totp/activate
     *  仕様: 04_MFA.md 「TOTP activate（code必須）」 :contentReference[oaicite:22]{index=22}
     */
    void totpActivate(MfaTotpActivateRequest req)
            throws UnauthorizedException, BadRequestException;

    /** TOTP 検証（ログイン時）。POST /auth/mfa/totp/verify
     *  仕様: 04_MFA.md 「TOTP verify → LoginResponse」 :contentReference[oaicite:23]{index=23}
     */
    LoginResponse totpVerify(MfaTotpVerifyRequest req)
            throws UnauthorizedException, BadRequestException;

    // ---- Email OTP ----

    /** Email OTP 送信。POST /auth/mfa/email/send
     *  仕様: 04_MFA.md 「Email send（ボディ省略可）」 :contentReference[oaicite:24]{index=24}
     */
    void emailSend(MfaEmailSendRequest req)
            throws UnauthorizedException, BadRequestException;

    /** Email OTP 検証（ログイン時）。POST /auth/mfa/email/verify
     *  仕様: 04_MFA.md 「Email verify（challengeId, code）→ LoginResponse」 :contentReference[oaicite:25]{index=25}
     */
    LoginResponse emailVerify(MfaEmailVerifyRequest req)
            throws UnauthorizedException, BadRequestException;

    // ---- Recovery Code ----

    /** リカバリーコード発行。POST /auth/mfa/recovery/issue
     *  仕様: 04_MFA.md 「recovery/issue → MfaRecoveryIssueResponse」 :contentReference[oaicite:26]{index=26}
     */
    MfaRecoveryIssueResponse recoveryIssue()
            throws UnauthorizedException, BadRequestException;

    /** リカバリーコード検証（ログイン時）。POST /auth/mfa/recovery/verify
     *  仕様: 04_MFA.md 「recovery/verify（challengeId, code）→ LoginResponse」 :contentReference[oaicite:27]{index=27}
     */
    LoginResponse recoveryVerify(MfaRecoveryVerifyRequest req)
            throws UnauthorizedException, BadRequestException;
}
