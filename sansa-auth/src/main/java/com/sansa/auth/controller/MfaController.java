package com.sansa.auth.controller;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.mfa.MfaEmailVerifyRequest;
import com.sansa.auth.dto.mfa.MfaRecoveryVerifyRequest;
import com.sansa.auth.dto.mfa.MfaTotpEnrollResponse;
import com.sansa.auth.dto.mfa.MfaTotpVerifyRequest;
import com.sansa.auth.service.MfaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MFA 関連 API
 * - ここでは Service インターフェースのシグネチャに合わせることを最優先。
 * - サービスから LoginResponse を返す想定に統一（void を返していた箇所を修正）
 */
@RestController
@RequestMapping("/api/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfa;

    @PostMapping("/totp/enroll")
    public ResponseEntity<MfaTotpEnrollResponse> totpEnroll() {
        // 実装はサービス側。ここではシグネチャを合わせる。
        MfaTotpEnrollResponse res = mfa.totpEnroll();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/totp/verify")
    public ResponseEntity<LoginResponse> totpVerify(@RequestBody @Valid MfaTotpVerifyRequest req) {
        // 以前 void を返す実装だったためコンパイルエラーになっていた
        LoginResponse res = mfa.totpVerify(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<LoginResponse> emailVerify(@RequestBody @Valid MfaEmailVerifyRequest req) {
        LoginResponse res = mfa.emailVerify(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/recovery/verify")
    public ResponseEntity<LoginResponse> recoveryVerify(@RequestBody @Valid MfaRecoveryVerifyRequest req) {
        LoginResponse res = mfa.recoveryVerify(req);
        return ResponseEntity.ok(res);
    }
}
