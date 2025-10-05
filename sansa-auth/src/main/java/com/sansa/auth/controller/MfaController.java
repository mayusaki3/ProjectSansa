package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.TotpVerifyRequest;
import com.sansa.auth.dto.Dtos.EmailOtpRequest;
import com.sansa.auth.dto.Dtos.EmailOtpVerifyRequest;
import com.sansa.auth.dto.Dtos.AuthResult;
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

    @PostMapping("/totp/verify")
    public ResponseEntity<AuthResult> verifyTotp(@Valid @RequestBody TotpVerifyRequest req) {
        var res = service.verifyTotp(req.getCode());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/email/send")
    public ResponseEntity<AuthResult> sendEmailOtp(@Valid @RequestBody EmailOtpRequest req) {
        var res = service.sendEmailOtp(req.getEmail());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<AuthResult> verifyEmailOtp(@Valid @RequestBody EmailOtpVerifyRequest req) {
        var res = service.verifyEmailOtp(req.getEmail(), req.getCode());
        return ResponseEntity.ok(res);
    }
}
