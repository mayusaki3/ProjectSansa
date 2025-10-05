package com.sansa.auth.controller;

import com.sansa.auth.dto.Dtos.LoginResponse;
import com.sansa.auth.dto.Dtos.TokenPair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webauthn")
public class WebAuthnController {

    @GetMapping("/challenge")
    public ResponseEntity<LoginResponse> challenge() {
        // 既存の LoginResponse シグネチャ:
        // LoginResponse(String requestId, boolean success, String message, TokenPair tokenPair)
        var res = new LoginResponse(
                "webauthn-challenge",
                true,
                "OK",
                null // ここではトークン未発行
        );
        return ResponseEntity.ok(res);
    }
}
