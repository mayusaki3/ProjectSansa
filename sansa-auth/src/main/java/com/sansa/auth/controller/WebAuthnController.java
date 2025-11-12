package com.sansa.auth.controller;

import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.service.WebAuthnService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * サービスIFに合わせた呼び出しに統一。
 * 以前の compile error はメソッド名／引数不一致が原因。
 */
public class WebAuthnController {

    private final WebAuthnService webauthn;

    public WebAuthnController(WebAuthnService webauthn) {
        this.webauthn = webauthn;
    }

    public WebAuthnRegisterOptionsResponse registerOptions(@NotBlank String userId) {
        return webauthn.registerOptions(userId);
    }

    public void registerVerify(@Valid WebAuthnRegisterVerifyRequest req) {
        webauthn.verifyRegister(req);
    }

    public WebAuthnChallengeResponse challenge(@NotBlank String userId) {
        return webauthn.assertionOptions(userId);
    }

    public void assertion(@Valid WebAuthnAssertionRequest req) {
        webauthn.verifyAssertion(req);
    }

    public WebAuthnCredentialListResponse listCredentials(@NotBlank String userId) {
        return webauthn.listCredentials(userId);
    }

    public void deleteCredential(@NotBlank String credentialId) {
        webauthn.deleteCredential(credentialId);
    }
}
