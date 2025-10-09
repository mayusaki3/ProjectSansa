[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 07. IT MFA（TOTP・Email OTP・Recovery）

# 07. MFA（TOTP / Email OTP / Recovery）

## TOTP

### IT-07-001: enroll → activate → verify
- When: `POST /auth/mfa/totp/enroll` → `POST /auth/mfa/totp/activate` `{code}`
- Then: 200、以後 `POST /auth/mfa/totp/verify` で `LoginResponse.authenticated=true`

### IT-07-002: verify（時計ずれ ±1 step 許容）
- Then: 200（境界値も確認）

## Email OTP

### IT-07-003: send（レート制限）
- When: `POST /auth/mfa/email/send` を短時間に連打
- Then: 429、`Retry-After` / `RateLimit-*`

### IT-07-004: verify（成功/不正/期限切れ）
- When: `POST /auth/mfa/email/verify` `{ challengeId, code }`
- Then: 200 / 400(`*invalid-code`) / 400(`*expired`)

## Recovery

### IT-07-005: issue（その場一度だけ表示）
- When: `POST /auth/mfa/recovery/issue`
- Then: 200、`recoveryCodes[]`（保存しない）

### IT-07-006: verify（消費/再利用不可）
- When: `POST /auth/mfa/recovery/verify` `{ challengeId, code }`
- Then: 成功=200（消費）、再利用=410/400

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 07. IT MFA（TOTP・Email OTP・Recovery）