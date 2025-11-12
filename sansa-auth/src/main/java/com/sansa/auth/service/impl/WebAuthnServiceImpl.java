package com.sansa.auth.service.impl;

import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.service.WebAuthnService;
import com.sansa.auth.store.Store;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 最低限のダミー実装。
 * - options はJSON文字列のままDTOへ格納（実処理は後で差し替え可）
 * - Credential 周りは Store を経由
 */
public class WebAuthnServiceImpl implements WebAuthnService {

    private final Store store;

    public WebAuthnServiceImpl(Store store) {
        this.store = Objects.requireNonNull(store);
    }

    @Override
    public WebAuthnRegisterOptionsResponse registerOptions(String userId) {
        // 本来はサーバ生成の challenge 等を含む JSON を作る
        WebAuthnRegisterOptionsResponse resp = new WebAuthnRegisterOptionsResponse();
        resp.setOptionsJson("{\"rp\":{\"name\":\"ProjectSansa\"},\"user\":{\"id\":\"" + userId + "\"}}");
        return resp;
    }

    @Override
    public void verifyRegister(WebAuthnRegisterVerifyRequest req) {
        // TODO: attestation 検証。ダミー登録
        Store.WebAuthnCredential c = new Store.WebAuthnCredential();
        c.setId(req.getCredentialId());
        c.setUserId(req.getUserId());
        c.setName(req.getDeviceName());
        c.setPublicKey("PUBKEY_DUMMY");
        c.setSignCount(0);
        c.setCreatedAt(Instant.now());
        store.saveWebAuthnCredential(c);
    }

    @Override
    public WebAuthnChallengeResponse assertionOptions(String userId) {
        WebAuthnChallengeResponse resp = new WebAuthnChallengeResponse();
        resp.setOptionsJson("{\"challenge\":\"CHALLENGE_DUMMY\",\"userId\":\"" + userId + "\"}");
        return resp;
    }

    @Override
    public void verifyAssertion(WebAuthnAssertionRequest req) {
        // TODO: assertion 検証。signCount 更新など
        store.updateWebAuthnCredentialOnSign(req.getCredentialId(), req.getNewSignCount(), Instant.now());
    }

    @Override
    public WebAuthnCredentialListResponse listCredentials(String userId) {
        WebAuthnCredentialListResponse resp = new WebAuthnCredentialListResponse();
        List<WebAuthnCredentialSummary> list = new ArrayList<>();
        for (Store.WebAuthnCredential c : store.listWebAuthnCredentials(userId)) {
            WebAuthnCredentialSummary s = new WebAuthnCredentialSummary();
            s.setId(c.getId());
            s.setName(c.getName()); // エラーの setName(...) 対応
            list.add(s);
        }
        resp.setCredentials(list);
        return resp;
    }

    @Override
    public void deleteCredential(String credentialId) {
        store.deleteWebAuthnCredential(credentialId);
    }
}
