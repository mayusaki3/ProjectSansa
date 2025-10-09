[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 03. Service ログイン・WebAuthn・セッション

# 03. Service ログイン・WebAuthn・セッション

## A) Password ログイン

### UT-03-001: パスワード成功
- Given: `identifier=alice`, `password=correct`
- Then: 200、`LoginResponse.authenticated=true`
  - `tokens.accessToken/refreshToken` 付与
  - `amr=["pwd"]`
  - `session.sessionId` あり

### UT-03-002: パスワードで MFA 必須
- Given: ユーザーが MFA 有効
- Then: 200、`authenticated=false`, `mfaRequired=true`
  - `mfa.challengeId` 付与、`mfa.factors` に `["totp" | "email_otp" | ...]`

## B) WebAuthn 認証

### UT-03-003: 認証チャレンジ取得
- `GET /webauthn/challenge`
- Then: 200、`challenge`, `rpId`, `userVerification="preferred"`

### UT-03-004: アサーション成功
- `POST /webauthn/assertion`（正当な `id/clientDataJSON/authenticatorData/signature`）
- Then: 200、`authenticated=true`, `amr` に `"webauthn"` を含む

### UT-03-005: アサーション成功だが MFA 必須
- リスクポリシーで要MFA
- Then: 200、`authenticated=false`, `mfaRequired=true`, `mfa.challengeId` 付与

### UT-03-006: アサーション不正 -> 400
- 署名検証失敗
- Then: 400、`type=.../invalid_assertion`

## C) セッション

### UT-03-007: 現在セッション取得
- `GET /auth/session`（AT 必要）
- Then: 200、`SessionInfo.active=true`、`amr`・`expiresAt` あり

### UT-03-008: 複数セッション列挙
- `GET /sessions`
- Then: 200、`sessions: SessionInfo[]`（2件以上）

### UT-03-009: セッション個別失効（存在するID）
- `DELETE /sessions/{sessionId}`
- Then: 204（以降 `/auth/session` で対象は無効）

### UT-03-010: セッション個別失効（未存在） -> 404
- Then: 404、`type=.../session_not_found`

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 03. Service ログイン・WebAuthn・セッション