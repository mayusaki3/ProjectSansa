package com.sansa.auth.controller;

import com.sansa.auth.dto.login.*;
import com.sansa.auth.dto.mfa.*;
import com.sansa.auth.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * /auth/mfa 配下：TOTP / Email OTP / Recovery
 * verify 成功時は LoginResponse（authenticated=true, amrに"mfa"）を返す想定。
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class MfaController {

    private final MfaService mfaService;

    // --- TOTP ---
    // enroll: secret/otpauth URI を払い出し
    @PostMapping("/auth/mfa/totp/enroll")
    public MfaTotpEnrollResponse totpEnroll() {
        return mfaService.totpEnroll();
    }

    // activate: 初回コードで有効化（200 / bodyなし可）
    @PostMapping("/auth/mfa/totp/activate")
    public ResponseEntity<Void> totpActivate(@Valid @RequestBody MfaTotpActivateRequest req) {
        mfaService.totpActivate(req);
        return ResponseEntity.ok().build(); // 200
    }

    // verify: 認証フローの検証（LoginResponse を返す）
    @PostMapping("/auth/mfa/totp/verify")
    public LoginResponse totpVerify(@Valid @RequestBody MfaTotpVerifyRequest req) {
        return mfaService.totpVerify(req);
    }

    // --- Email OTP ---
    // send: 送信（429レート制限、ここは202受理でOK）
    @PostMapping("/auth/mfa/email/send")
    public ResponseEntity<Void> emailSend(@RequestBody(required = false) MfaEmailSendRequest req) {
        mfaService.emailSend(req);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // 202
    }

    // verify: 検証（LoginResponse を返す）
    @PostMapping("/auth/mfa/email/verify")
    public LoginResponse emailVerify(@Valid @RequestBody MfaEmailVerifyRequest req) {
        return mfaService.emailVerify(req);
    }

    // --- Recovery ---
    // issue: その場表示のみ（既存はハッシュ保存推奨）
    @PostMapping("/auth/mfa/recovery/issue")
    public MfaRecoveryIssueResponse recoveryIssue() {
        return mfaService.recoveryIssue();
    }

    // verify: 検証（消費・再利用不可）
    @PostMapping("/auth/mfa/recovery/verify")
    public LoginResponse recoveryVerify(@Valid @RequestBody MfaRecoveryVerifyRequest req) {
        return mfaService.recoveryVerify(req);
    }
}
