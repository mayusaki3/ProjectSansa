package com.sansa.auth.controller;

import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        var res = auth.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid TokenRefreshRequest req) {
        var res = auth.refresh(req);
        return ResponseEntity.ok(res); // ← tokens ではなく res を返す
    }
}
