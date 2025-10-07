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
    @PostMapping("/api/auth/pre-register")
    public ResponseEntity<Dtos.AuthResult> preRegister(@RequestBody @Valid Dtos.PreRegisterRequest req) {
        var res = service.preRegister(req.getEmail(), req.getLanguage());
        return ResponseEntity.ok(res);
    }

    // メールコード検証
    @PostMapping("/api/auth/verify-email")
    public ResponseEntity<Dtos.AuthResult> verifyEmail(@RequestBody @Valid Dtos.VerifyEmailRequest req) {
        var res = service.verifyEmail(req.getPreRegId(), req.getCode());
        return ResponseEntity.ok(res);
    }

    // 本登録
    @PostMapping("/api/auth/register")
    public ResponseEntity<Models.User> register(@RequestBody @Valid Dtos.RegisterRequest req) {
        var user = service.register(req.getPreRegId(), req.getAccountId(), req.getLanguage());
        return ResponseEntity.ok(user);     // ★ ここは User を返す
    }
}
