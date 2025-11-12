package com.sansa.auth.service;

import com.sansa.auth.dto.webauthn.*;

public interface WebAuthnService {

    // 登録開始: userId を受け取り、RP が生成した options をJSON文字列で返すDTOへ詰める
    WebAuthnRegisterOptionsResponse registerOptions(String userId);

    // 登録検証
    void verifyRegister(WebAuthnRegisterVerifyRequest req);

    // 認証開始（assertion options）
    WebAuthnChallengeResponse assertionOptions(String userId);

    // 認証検証
    void verifyAssertion(WebAuthnAssertionRequest req);

    // 資格情報一覧 / 削除
    WebAuthnCredentialListResponse listCredentials(String userId);
    void deleteCredential(String credentialId);
}
