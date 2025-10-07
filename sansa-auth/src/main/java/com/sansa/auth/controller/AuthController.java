package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
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
    public ResponseEntity<Dtos.AuthResult> preRegister(@RequestBody @Valid Dtos.PreRegisterRequest req) {
        Dtos.AuthResult  res = service.preRegister(req.getEmail(), req.getLanguage());
        return ResponseEntity.ok(res);
    }

    // メールコード検証
    @PostMapping("/verify-email")
    public ResponseEntity<Dtos.AuthResult> verifyEmail(@RequestBody @Valid Dtos.VerifyEmailRequest req) {
        Dtos.AuthResult  res = service.verifyEmail(req.getPreRegId(), req.getCode());
        return ResponseEntity.ok(res);
    }

    // 本登録
    @PostMapping("/register")
    public ResponseEntity<Dtos.AuthResult> register(@RequestBody @Valid Dtos.RegisterRequest req) {
        // いまは RegisterRequest に preRegId/accountId/email/language を持たせています
        Dtos.AuthResult res = service.register(req.getPreRegId(), req.getAccountId(), req.getLanguage());
        return ResponseEntity.ok(res);
    }
}
