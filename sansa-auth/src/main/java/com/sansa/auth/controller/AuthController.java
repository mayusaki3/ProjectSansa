package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.PreRegisterRequest;
import com.sansa.auth.dto.Dtos.VerifyEmailRequest;
import com.sansa.auth.dto.Dtos.RegisterRequest;

import com.sansa.auth.service.Services;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Services svc;

    public AuthController(Services svc) { this.svc = svc; }

    @PostMapping("/pre-register")
    public ResponseEntity<?> preRegister(@Valid @RequestBody PreRegisterRequest req) {
        Map<String, Object> res = svc.preRegister(req.email());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        return ResponseEntity.ok(svc.verifyEmail(req.email(), req.code()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        var u = svc.register(req.preRegId(), req.accountId(), req.language());
        return ResponseEntity.ok(Map.of("userId", u.userId.toString(), "accountId", u.accountId, "email", u.email, "emailVerified", u.emailVerified));
    }

    @PostMapping("/logout_all")
    public ResponseEntity<?> logoutAll(@RequestParam String accountId) {
        return ResponseEntity.ok(svc.logoutAll(accountId));
    }
}
