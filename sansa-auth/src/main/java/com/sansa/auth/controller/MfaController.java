package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.service.MfaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mfa")
public class MfaController {

    private final MfaService service;

    public MfaController(MfaService service) {
        this.service = service;
    }

    // TOTP 検証: リクエストボディを DTO で受け取る
    @PostMapping("/totp/verify")
    public ResponseEntity<Dtos.AuthResult> verifyTotp(@Valid @RequestBody Dtos.TotpVerifyRequest req) {
        return ResponseEntity.ok(service.verifyTotp(req));
    }

    // Email OTP 送信: email をパラメータで受け取る（Dtos.EmailOtpSendRequest は存在しないため）
    @PostMapping("/email/send")
    public ResponseEntity<Dtos.AuthResult> sendEmailOtp(@RequestParam("email") String email) {
        return ResponseEntity.ok(service.sendEmailOtp(email));
    }

    // Email OTP 検証: DTO で受け取る（Dtos.EmailOtpVerifyRequest は既存）
    @PostMapping("/email/verify")
    public ResponseEntity<Dtos.AuthResult> verifyEmailOtp(@Valid @RequestBody Dtos.EmailOtpVerifyRequest req) {
        return ResponseEntity.ok(service.verifyEmailOtp(req));
    }
}
