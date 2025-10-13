// src/main/java/com/sansa/auth/controller/WebAuthnController.java
package com.sansa.auth.controller;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.webauthn.WebAuthnAssertionRequest;
import com.sansa.auth.dto.webauthn.WebAuthnChallengeResponse;
import com.sansa.auth.dto.webauthn.WebAuthnCredentialListResponse;
import com.sansa.auth.dto.webauthn.WebAuthnRegisterOptionsResponse;
import com.sansa.auth.dto.webauthn.WebAuthnRegisterVerifyRequest;
import com.sansa.auth.dto.webauthn.WebAuthnRegisterVerifyResponse;
import com.sansa.auth.service.WebAuthnService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * API仕様に沿ったWebAuthnエンドポイント群。
 * 返却型と引数はサービスIF(WebAuthnService)と厳密一致。
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class WebAuthnController {

    private final WebAuthnService webauthn;

    /** GET /webauthn/register/options */
    @GetMapping("/webauthn/register/options")
    public WebAuthnRegisterOptionsResponse registerOptions() {
        return webauthn.registerOptions();
    }

    /** POST /webauthn/register/verify */
    @PostMapping("/webauthn/register/verify")
    public WebAuthnRegisterVerifyResponse registerVerify(@RequestBody @Valid WebAuthnRegisterVerifyRequest req) {
        return webauthn.registerVerify(req);
    }

    /** GET /webauthn/challenge */
    @GetMapping("/webauthn/challenge")
    public WebAuthnChallengeResponse challenge() {
        return webauthn.challenge();
    }

    /** POST /webauthn/assertion */
    @PostMapping("/webauthn/assertion")
    public LoginResponse assertion(@RequestBody @Valid WebAuthnAssertionRequest req) {
        return webauthn.assertion(req);
    }

    /** GET /webauthn/credentials */
    @GetMapping("/webauthn/credentials")
    public WebAuthnCredentialListResponse listCredentials() {
        return webauthn.listCredentials();
    }

    /** DELETE /webauthn/credentials/{credentialId} */
    @DeleteMapping("/webauthn/credentials/{credentialId}")
    public void deleteCredential(@PathVariable @NotBlank String credentialId) {
        webauthn.deleteCredential(credentialId);
        // 仕様は204/200いずれでも妥当。voidで204(No Content)を返す。
    }
}
