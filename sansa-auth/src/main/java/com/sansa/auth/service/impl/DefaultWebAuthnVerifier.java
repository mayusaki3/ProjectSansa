package com.sansa.auth.service.impl;

import com.sansa.auth.dto.webauthn.WebAuthnRegisterOptionsResponse;
import com.sansa.auth.exception.BadRequestException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * デフォルトの WebAuthn 検証器（ダミー実装）。
 *
 * 目的:
 * - WebAuthn の実証/検証を“本物のライブラリ”に置き換える前のつなぎ／ITテスト用。
 * - {@link WebAuthnServiceImpl.WebAuthnVerifier} を実装し、Spring の DI で
 *   {@link WebAuthnServiceImpl} から注入できるようにする。
 *
 * 注意:
 * - ここでは暗号学的な真正性検証は一切行いません（本番では必ず置き換えてください）。
 * - インターフェースのメソッド・シグネチャはプロジェクト側（添付の WebAuthnServiceImpl）に
 *   合わせています。現在のエラーが示すとおり verifyAssertion(...) は String x 5 の引数です。
 */
@Component
@Primary // 複数候補があってもこの実装を優先（テスト向け）
public class DefaultWebAuthnVerifier implements WebAuthnServiceImpl.WebAuthnVerifier {

    /** 
     * 登録オプション（チャレンジ等）を準備。
     */
    @Override
    public WebAuthnRegisterOptionsResponse prepareRegistrationOptions(String userId) {
        return WebAuthnRegisterOptionsResponse.builder()
                .rpId("example.com")
                .challenge("dummy-registration-challenge")
                .build();
    }

    /**
     * 登録アテステーションを検証。
     */
    @Override
    public AttestationVerified verifyAttestation(String clientDataJSON, String attestationObject, String userId)
            throws BadRequestException {
        return AttestationVerified.builder()
            .credentialId("dummy-credential-id")
            .userHandle("dummy-user-handle")
            .counterInitialized(true)
            .build();
    }

    /**
     * RP ID を取得。
     */
    @Override
    public String getRpId() {
        return "example.com";
    }

    /**
     * ダミーのチャレンジ文字列を発行。
     */
    @Override
    public String issueAssertionChallenge() {
        return "dummy-assertion-challenge";
    }

    /**
     * 認証アサーションを検証。
     */
    @Override
    public AssertionVerified verifyAssertion(String id, String clientDataJSON, String authenticatorData,
                                             String signature, String userHandle)
            throws BadRequestException {
        return AssertionVerified.builder()
                .credentialId(id)
                .counterUpdated(true)
                .build();
    }


//     @Override
//     public AssertionVerified verifyAssertion(/* args */) {
//         return AssertionVerified.builder()
//             .credentialId(credId)
//             .counterUpdated(true)
//             .build();
//     }
}
