package com.sansa.auth.service.impl;

import com.sansa.auth.dto.webauthn.WebAuthnRegisterOptionsResponse;
import com.sansa.auth.dto.webauthn.WebAuthnCredentialListResponse;
import com.sansa.auth.dto.webauthn.WebAuthnCredentialListResponse.Credential;
import com.sansa.auth.service.WebAuthnService;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.WebAuthnServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WebAuthn（パスキー / セキュリティキー）登録・認証サービス実装。
 *
 * 主な責務：
 *  - 登録オプションの発行
 *  - 登録済み認証情報の保存・削除・一覧取得
 *  - 認証チャレンジの検証
 *
 * 各操作は Store を介して永続層（インメモリ / Cassandra 等）にアクセスする。
 */
@Service
@Transactional
public class WebAuthnServiceImpl implements WebAuthnService {

    private final Store store;
    private final WebAuthnServer webAuthnServer;

    public WebAuthnServiceImpl(Store store, WebAuthnServer webAuthnServer) {
        this.store = store;
        this.webAuthnServer = webAuthnServer;
    }

    // ==========================================================
    // 登録オプションの発行
    // ==========================================================

    /**
     * 新規登録オプションを生成。
     *
     * @param userId 対象ユーザーID
     * @param username 表示名（アカウント名）
     * @return 登録オプションレスポンス
     */
    @Override
    public WebAuthnRegisterOptionsResponse generateRegisterOptions(String userId, String username) {
        var options = webAuthnServer.createRegistrationOptions(userId, username);
        return new WebAuthnRegisterOptionsResponse(options);
    }

    // ==========================================================
    // 認証情報の登録 / 削除 / 一覧
    // ==========================================================

    /**
     * 登録済み認証情報を永続化する。
     *
     * @param userId ユーザーID
     * @param credentialId クレデンシャルID
     * @param publicKey 公開鍵
     * @param signCount サインカウンタ
     * @param label 任意の識別ラベル
     */
    @Override
    public void saveCredential(String userId, String credentialId,
                               byte[] publicKey, long signCount, String label) {
        store.createWebAuthnCredential(userId, credentialId, publicKey, signCount, label);
    }

    /**
     * 登録済みクレデンシャルを削除。
     *
     * @param userId ユーザーID
     * @param credentialId 削除対象クレデンシャルID
     * @return 削除成功時true
     */
    @Override
    public boolean deleteCredential(String userId, String credentialId) {
        return store.deleteWebAuthnCredential(userId, credentialId);
    }

    /**
     * ユーザーの登録済みクレデンシャル一覧を取得。
     *
     * @param userId 対象ユーザーID
     * @return 登録済みクレデンシャル一覧レスポンス
     */
    @Override
    public WebAuthnCredentialListResponse listCredentials(String userId) {
        var creds = store.listWebAuthnCredentials(userId);
        List<Credential> dtoList = creds.stream()
                .map(c -> new Credential(
                        c.getCredentialId(),
                        c.getLabel(),
                        c.getCreatedAt(),
                        c.getSignCount()))
                .collect(Collectors.toList());
        return new WebAuthnCredentialListResponse(dtoList);
    }

    // ==========================================================
    // 認証チャレンジ関連
    // ==========================================================

    /**
     * 認証チャレンジを生成する。
     *
     * @param userId 対象ユーザーID
     * @return 生成されたチャレンジデータ
     */
    @Override
    public byte[] generateChallenge(String userId) {
        return webAuthnServer.createAuthenticationChallenge(userId);
    }

    /**
     * クライアントからの応答を検証する。
     *
     * @param userId ユーザーID
     * @param credentialId クレデンシャルID
     * @param clientDataJSON クライアント送信データ
     * @param authenticatorData オーセンティケータデータ
     * @param signature 署名
     * @return 検証成功時 true
     */
    @Override
    public boolean verifyAssertion(String userId, String credentialId,
                                   byte[] clientDataJSON, byte[] authenticatorData, byte[] signature) {
        var credential = store.findWebAuthnCredential(userId, credentialId);
        if (credential == null) return false;
        return webAuthnServer.verifyAuthentication(
                credential.getPublicKey(),
                clientDataJSON,
                authenticatorData,
                signature,
                credential.getSignCount());
    }
}
