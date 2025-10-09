[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 02. IT WebAuthn 登録・認証・管理

# 02. WebAuthn（/webauthn）

## A) 登録（enroll）

### IT-02-001: register/options（正常）
- Given: ログイン済み（ATあり）
- When: `GET /webauthn/register/options`
- Then: 200、`challenge`, `rpId`, `user`, `pubKeyCredParams[]`, `attestation=="none"`

### IT-02-002: register/verify（正常）
- Given: 正当な `clientDataJSON`, `attestationObject`
- When: `POST /webauthn/register/verify`
- Then: 200、`credentialId`, `publicKey`, `aaguid?`, `transports?`, `signCount?`

### IT-02-003: register/verify（RP不一致 or 検証失敗）
- Then: 400、`type=*invalid_assertion`

## B) 認証（assertion）

### IT-02-004: challenge（正常）
- When: `GET /webauthn/challenge`
- Then: 200、`challenge`, `rpId`, `userVerification=="preferred"`

### IT-02-005: assertion（成功）
- When: `POST /webauthn/assertion`（正当な `id/clientDataJSON/authenticatorData/signature`）
- Then: 200、`LoginResponse.authenticated=true`, `amr` に `"webauthn"`

### IT-02-006: assertion（成功だがMFA必須）
- Given: リスクルールが高判定
- Then: 200、`authenticated=false`, `mfaRequired=true`, `mfa.challengeId` 付与

### IT-02-007: assertion（署名不正）
- Then: 400、`type=*invalid_assertion`

## C) 管理

### IT-02-008: credentials 一覧
- When: `GET /webauthn/credentials`
- Then: 200、`credentials[].credentialId` など

### IT-02-009: credential 失効
- When: `DELETE /webauthn/credentials/{credentialId}`
- Then: 204（以後の assertion は失敗）

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 02. IT WebAuthn 登録・認証・管理