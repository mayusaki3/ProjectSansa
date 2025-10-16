package com.sansa.auth.service.impl;

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
     * 認証(assertion)の“検証”ダミー。
     *
     * 実装方針:
     * - 受け取ったパラメータの妥当性チェックや署名検証は行いません。
     * - テストで扱いやすいように、ユーザーID相当として最後の引数（userHandle を想定）
     *   が non-blank ならそれを返し、空なら credentialId（第4引数）を返します。
     *
     * パラメータの意味（プロジェクトの定義に依存。エラーログから 5 引数である点のみ確定）:
     *  - 第1引数: clientDataJSON 等
     *  - 第2引数: authenticatorData 等
     *  - 第3引数: signature 等
     *  - 第4引数: credentialId 等
     *  - 第5引数: userHandle 等（存在すればそのままユーザーIDとして返す）
     */
    @Override
    public WebAuthnServiceImpl.AssertionVerified verifyAssertion(
            String arg1,
            String arg2,
            String arg3,
            String credentialId,
            String userHandle
    ) {
        // userHandle が空なら credentialId を userId として返すダミー実装
        return (userHandle != null && !userHandle.isBlank()) ? userHandle : credentialId;
    }

    /*
     * 補足:
     * - 添付コードの断片では verifyAttestation(...) の存在が確定していないため未実装。
     *   もし {@code WebAuthnServiceImpl.WebAuthnVerifier} に登録(Attestation)検証メソッドが
     *   追加されている場合は、同様にダミーで実装してください（例: credentialId をそのまま返す等）。
     *
     * 例（インターフェースにある場合のみ追加）:
     *
     * @Override
     * public WebAuthnServiceImpl.AttestationVerified verifyAttestation(
     *         String origin, String rpId, String challenge,
     *         String clientDataJSON, String attestationObject
     * ) {
     *     final String credentialId = attestationObject; // 実際は CBOR 解析などが必要
     *     return () -> credentialId;
     * }
     */
}
