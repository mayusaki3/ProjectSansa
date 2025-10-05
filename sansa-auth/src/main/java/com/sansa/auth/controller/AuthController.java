package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.PreRegisterRequest;
import com.sansa.auth.dto.Dtos.VerifyEmailRequest;
import com.sansa.auth.dto.Dtos.RegisterRequest;
import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/pre-register")
    public ResponseEntity<AuthResult> preRegister(@Valid @RequestBody PreRegisterRequest req) {
        // Dtos はレコード風アクセサを仮定（getEmail() ではなく email()）
        var res = service.preRegister(req.getEmail());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResult> verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        var res = service.verifyEmail(req.getEmail(), req.getCode());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResult> register(@Valid @RequestBody RegisterRequest req) {
        var res = service.register(req.getPreRegId(), req.getAccountId(), req.getLanguage());
        return ResponseEntity.ok(res);
    }
}
