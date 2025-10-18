package com.sansa.auth.service.impl;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.service.WebAuthnService;
import com.sansa.auth.store.Store;
import com.sansa.auth.store.Store.WebAuthnCredential;
import lombok.RequiredArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * /webauthn 配下のユースケース実装
 * 対応仕様: 03_WebAuthn.md
 * - GET  /webauthn/register/options → WebAuthnRegisterOptionsResponse
 * - POST /webauthn/register/verify  → WebAuthnRegisterVerifyResponse
 * - GET  /webauthn/challenge        → WebAuthnChallengeResponse
 * - POST /webauthn/assertion        → LoginResponse（amr+=["webauthn"]）
 * - GET  /webauthn/credentials      → WebAuthnCredentialListResponse
 * - DELETE /webauthn/credentials/{id} → 204/404
 */
@Service
@RequiredArgsConstructor
public class WebAuthnServiceImpl implements WebAuthnService {

    private final Store store;
    private final WebAuthnVerifier webAuthn;      // WebAuthn 低レベル検証の抽象
    private final TokenFacade tokenFacade;        // 認証成功時の AT/RT 発行・セッション生成

    // ---- register/options -----------------------------------------------------

    @Override
    public WebAuthnRegisterOptionsResponse registerOptions()
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        return webAuthn.prepareRegistrationOptions(userId);
    }

    // ---- register/verify ------------------------------------------------------

    @Override
    public WebAuthnRegisterVerifyResponse registerVerify(WebAuthnRegisterVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        var verified = webAuthn.verifyAttestation(req.getClientDataJSON(), req.getAttestationObject(), userId);
        // 検証OK → 保存
        store.saveWebAuthnCredential(new WebAuthnCredential(
                verified.getCredentialId(), userId, /*nickname*/null,
                verified.getAaguid(), verified.getTransports(),
                verified.getSignCount(), Instant.now()
        ));
        return WebAuthnRegisterVerifyResponse.builder()
                .credentialId(verified.getCredentialId())
                .publicKey(verified.getPublicKey())
                .aaguid(verified.getAaguid())
                .transports(verified.getTransports())
                .signCount(verified.getSignCount())
                .build();
    }

    // ---- challenge ------------------------------------------------------------

    @Override
    public WebAuthnChallengeResponse challenge()
            throws UnauthorizedException, BadRequestException {
        String rpId = webAuthn.getRpId();
        String challenge = webAuthn.issueAssertionChallenge();
        return WebAuthnChallengeResponse.builder()
                .challenge(challenge)
                .rpId(rpId)
                .timeout(60_000L)
                .userVerification("preferred")
                .build();
    }

    // ---- assertion ------------------------------------------------------------

    @Override
    public LoginResponse assertion(WebAuthnAssertionRequest req)
            throws UnauthorizedException, BadRequestException {
        var result = webAuthn.verifyAssertion(req.getId(), req.getClientDataJSON(),
                req.getAuthenticatorData(), req.getSignature(), req.getUserHandle());
        String userId = result.getUserId();

        // 認証成功 → セッション + トークン発行
        var out = tokenFacade.issueAfterAuth(userId, List.of("webauthn")); // amr+=["webauthn"]
        return LoginResponse.builder()
            .tokens(out)
            .build();
    }

    // ---- credentials list / revoke -------------------------------------------

    @Override
    public WebAuthnCredentialListResponse listCredentials() throws UnauthorizedException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        List<WebAuthnCredential> list = store.listWebAuthnCredentials(userId);
        return WebAuthnCredentialListResponse.builder()
                .credentials(list.stream().map(c -> WebAuthnCredentialSummary.builder()
                        .credentialId(c.credentialId())
                        .aaguid(c.aaguid())
                        .transports(c.transports())
                        .signCount((int) c.signCount())
                        .build()).toList())
                .build();
    }

    @Override
    public void deleteCredential(String credentialId)
            throws UnauthorizedException, NotFoundException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        // 存在チェック（存在しない→404）
        boolean exists = store.listWebAuthnCredentials(userId).stream()
                .anyMatch(c -> c.credentialId().equals(credentialId));
        if (!exists) throw new NotFoundException("credential not found");
        store.deleteWebAuthnCredential(userId, credentialId);
    }

    // ---- 補助抽象 ------------------------------------------------------------

    public interface WebAuthnVerifier {

        /** 
         * 登録オプション（チャレンジ等）を準備。
         */
        WebAuthnRegisterOptionsResponse prepareRegistrationOptions(String userId);

        /**
         * 登録アテステーションを検証。
         */
        AttestationVerified verifyAttestation(String clientDataJSON, String attestationObject, String userId)
                throws BadRequestException;

        /**
         * RP ID を取得。
         */
        String getRpId();

        /**
         * ダミーのチャレンジ文字列を発行。
         */
        String issueAssertionChallenge();

        /**
         * 認証アサーションを検証。
         */
        AssertionVerified verifyAssertion(String id, String clientDataJSON, String authenticatorData,
                                          String signature, String userHandle)
                throws BadRequestException;

        /**
         * 登録アテステーション検証結果
         */
        @Value @Builder
        class AttestationVerified {
            String credentialId;
            String userHandle;
            String publicKey;
            String aaguid;
            java.util.List<String> transports;
            Integer signCount;
            boolean counterInitialized;
        }

        /**
         * 認証アサーション検証結果
         */
        @Value @Builder
        class AssertionVerified {
            String userId;
            String credentialId;
            boolean counterUpdated;
        }
    }

    /**
     * 現在のリクエストコンテキスト
     */
    public static final class CurrentRequestContext {
        public static String getUserIdOrThrow() { throw new UnsupportedOperationException(); }
    }
}
