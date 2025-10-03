package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.TotpVerifyRequest;
import com.sansa.auth.dto.Dtos.EmailOtpRequest;
import com.sansa.auth.dto.Dtos.EmailOtpVerifyRequest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/mfa")
public class MfaController {
    @PostMapping("/totp/verify")
    public ResponseEntity<?> totpVerify(@Valid @RequestBody TotpVerifyRequest req) {
        if (!"000000".equals(req.code())) {
            return ResponseEntity.status(401).body(Map.of("error","mfa_invalid"));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/email/request")
    public ResponseEntity<?> emailRequest(@Valid @RequestBody EmailOtpRequest req) {
        return ResponseEntity.ok(Map.of("sent", true));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerify(@Valid @RequestBody EmailOtpVerifyRequest req) {
        if (!"000000".equals(req.code())) {
            return ResponseEntity.status(401).body(Map.of("error","mfa_invalid"));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
