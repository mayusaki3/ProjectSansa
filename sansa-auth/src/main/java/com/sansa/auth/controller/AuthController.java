package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // 事前登録（メールアドレスで開始）
    @PostMapping("/pre-register")
    public ResponseEntity<Dtos.AuthResult> preRegister(@Valid @RequestBody Dtos.PreRegisterRequest req) {
        return ResponseEntity.ok(service.preRegister(req));
    }

    // メールコード検証
    @PostMapping("/verify-email")
    public ResponseEntity<Dtos.AuthResult> verifyEmail(@Valid @RequestBody Dtos.VerifyEmailRequest req) {
        return ResponseEntity.ok(service.verifyEmail(req));
    }

    // 本登録
    @PostMapping("/register")
    public ResponseEntity<Dtos.AuthResult> register(@Valid @RequestBody Dtos.RegisterRequest req) {
        return ResponseEntity.ok(service.register(req));
    }
}
