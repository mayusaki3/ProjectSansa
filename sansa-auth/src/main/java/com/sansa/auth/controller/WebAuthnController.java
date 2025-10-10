package com.sansa.auth.controller;

import com.sansa.auth.dto.login.*;
import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * /webauthn 配下：登録（options→verify）/ 認証（challenge→assertion）/ 資格情報管理
 */
@RestController
@RequestMapping("/webauthn")
@RequiredArgsConstructor
@Validated
public class WebAuthnController {

    private final WebAuthnService webAuthnService;

    // 認証：チャレンジ取得
    @GetMapping("/challenge")
    public WebAuthnChallengeResponse challenge() {
        return webAuthnService.challenge();
    }

    // 認証：アサーション検証（成功→ LoginResponse）
    @PostMapping("/assertion")
    public LoginResponse assertion(@Valid @RequestBody WebAuthnAssertionRequest req) {
        return webAuthnService.assertion(req);
    }

    // 登録：オプション取得
    @GetMapping("/register/options")
    public WebAuthnRegisterOptionsResponse registerOptions() {
        return webAuthnService.registerOptions();
    }

    // 登録：検証
    @PostMapping("/register/verify")
    public WebAuthnRegisterVerifyResponse registerVerify(@Valid @RequestBody WebAuthnRegisterVerifyRequest req) {
        return webAuthnService.registerVerify(req);
    }

    // 管理：クレデンシャル一覧
    @GetMapping("/credentials")
    public WebAuthnCredentialListResponse listCredentials() {
        return webAuthnService.listCredentials();
    }

    // 管理：クレデンシャル失効
    @DeleteMapping("/credentials/{credentialId}")
    public ResponseEntity<Void> revokeCredential(@PathVariable String credentialId) {
        webAuthnService.revokeCredential(credentialId);
        return ResponseEntity.noContent().build(); // 204
    }
}
