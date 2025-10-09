[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 06. Util MFA（TOTP・メールOTP・Recovery）

# 06. Util MFA（TOTP・メールOTP・Recovery）

## TOTP
### UT-06-001: enroll（secret/otpauth URI 発行）
- `POST /auth/mfa/totp/enroll`
- Then: 200、`secret`, `uri` 返却

### UT-06-002: activate（初回コード検証）
- `POST /auth/mfa/totp/activate` `{ "code":"123456" }`
- Then: 200（時計ずれ ±1 step 許容）

### UT-06-003: verify（ログイン時）
- `POST /auth/mfa/totp/verify` `{ "challengeId":"...", "code":"..." }`
- Then: 200、`LoginResponse.authenticated=true`, `amr` に `"mfa"`

## Email OTP
### UT-06-004: 送信（レート制限）
- `POST /auth/mfa/email/send` を連打
- Then: 429

### UT-06-005: 検証成功/失敗/期限切れ
- `POST /auth/mfa/email/verify`
- Then: 成功=200、不正=400（`invalid-code`）、期限切れ=400（`expired`）

## Recovery
### UT-06-006: 発行（その場のみ表示）
- `POST /auth/mfa/recovery/issue`
- Then: 200、`recoveryCodes[]` 返却（保存されない前提）

### UT-06-007: 検証
- `POST /auth/mfa/recovery/verify` `{ "challengeId":"...", "code":"..." }`
- Then: 200（成功時は消費）、不正=400、再利用=410/400

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 06. Util MFA（TOTP・メールOTP・Recovery）