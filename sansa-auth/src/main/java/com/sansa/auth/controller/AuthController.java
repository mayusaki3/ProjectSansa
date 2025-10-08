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
    public ResponseEntity<Dtos.AuthResult> preRegister(@RequestBody Dtos.PreRegisterRequest req) {
        String preRegId = service.preRegister(req.getEmail(), req.getLanguage());
        return ResponseEntity.ok(
            Dtos.AuthResult.ok("pre-registered",
                Map.of("preRegId", preRegId))
        );
    }

    // メールコード検証
    @PostMapping("/verify-email")
    public ResponseEntity<Dtos.AuthResult> verifyEmail(@RequestBody Dtos.VerifyEmailRequest req) {
        boolean ok = service.verifyEmail(req.getEmail(), req.getCode());
        return ResponseEntity.ok(
            Dtos.AuthResult.ok("verified",
                Map.of("email", req.getEmail()))
        );
    }

    // 本登録
    @PostMapping("/register")
    public ResponseEntity<Dtos.AuthResult> register(@RequestBody Dtos.RegisterRequest req) {
        Models.User user = service.register(req.getPreRegId(), req.getLanguage());
        return ResponseEntity.ok(
            Dtos.AuthResult.ok("registered",
                Map.of(
                    "userId", user.getId().toString(),
                    "accountId", user.getAccountId().toString(),
                    "email", user.getEmail()
                ))
        );
    }
}
