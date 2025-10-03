package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.LoginResponse;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.service.Services;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webauthn")
public class WebAuthnController {
    private final Services svc;
    public WebAuthnController(Services svc) { this.svc = svc; }

    @GetMapping("/challenge")
    public ResponseEntity<?> challenge() {
        return ResponseEntity.ok(svc.webAuthnChallenge());
    }

    @PostMapping("/assertion")
    public ResponseEntity<?> assertion(@RequestParam String accountId) {
        Session s = svc.loginWithAssertion(accountId);
        String at = svc.signAccess(s.userId, s.tokenVersion, 900);
        String rt = svc.signAccess(s.userId, s.tokenVersion, 2592000);
        return ResponseEntity.ok(new LoginResponse(at, rt, 900));
    }
}
