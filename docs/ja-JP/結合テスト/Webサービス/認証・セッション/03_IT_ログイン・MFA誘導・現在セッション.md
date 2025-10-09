[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 03. IT ログイン・MFA誘導・現在セッション

# 03. ログイン（Password / WebAuthn）・MFA誘導・現在セッション

## パスワード

### IT-03-001: password 成功
- When: `POST /auth/login` `{ identifier:"user1", password:"P@ssw0rd!" }`
- Then: 200、`authenticated=true`, `tokens.accessToken/refreshToken`, `amr=["pwd"]`, `session.sessionId`

### IT-03-002: password → MFA 必須
- Given: ユーザーが MFA 有効
- Then: 200、`authenticated=false`, `mfaRequired=true`, `mfa.challengeId`

### IT-03-003: password 失敗
- Then: 401、`type=*invalid-credentials`

## WebAuthn（認証は 02 参照）

## 現在セッション

### IT-03-004: GET /auth/session（有効）
- When: ATで呼び出し
- Then: 200、`active=true`, `amr`、`expiresAt`、`user.userId`

### IT-03-005: GET /auth/session（無効）
- Given: 失効したAT
- Then: 401

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 03. IT ログイン・MFA誘導・現在セッション