package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
import com.sansa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // 事前登録（メールアドレスで開始）
    @PostMapping("/pre-register")
    public ResponseEntity<Map<String,Object>> preRegister(@RequestBody Dtos.PreRegisterRequest req) {
        Map<String,Object> body = service.preRegister(req.getEmail(), req.getLanguage());
        return ResponseEntity.ok(body);
    }

    // メールコード検証
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String,Object>> verifyEmail(@RequestBody Dtos.VerifyEmailRequest req) {
        Map<String,Object> body = service.verifyEmail(req.getEmail(), req.getCode()); // ← getEmail()/getCode()
        return ResponseEntity.ok(body);
    }

    // 本登録
    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody Dtos.RegisterRequest req) {
        Map<String,Object> body = service.register(req.getPreRegId(), req.getLanguage());
        return ResponseEntity.ok(body);
    }
}
