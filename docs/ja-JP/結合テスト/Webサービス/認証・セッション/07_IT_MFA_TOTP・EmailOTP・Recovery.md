[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 07. IT MFA（TOTP・Email OTP・Recovery）

# 07. MFA（TOTP / Email OTP / Recovery）

## TOTP

### M01:IT-07-001: enroll → activate → verify
- When: `POST /auth/mfa/totp/enroll` → `POST /auth/mfa/totp/activate` `{code}`
- Then: 200、以後 `POST /auth/mfa/totp/verify` で `LoginResponse.authenticated=true`

### M01:IT-07-002: verify（時計ずれ ±1 step 許容）
- Then: 200（境界値も確認）

## Email OTP

### M01:IT-07-003: send（レート制限）
- When: `POST /auth/mfa/email/send` を短時間に連打
- Then: 429、`Retry-After` / `RateLimM01:IT-*`

### M01:IT-07-004: verify（成功/不正/期限切れ）
- When: `POST /auth/mfa/email/verify` `{ challengeId, code }`
- Then: 200 / 400(`*invalid-code`) / 400(`*expired`)

## Recovery

### M01:IT-07-005: issue（その場一度だけ表示）
- When: `POST /auth/mfa/recovery/issue`
- Then: 200、`recoveryCodes[]`（保存しない）

### M01:IT-07-006: verify（消費/再利用不可）
- When: `POST /auth/mfa/recovery/verify` `{ challengeId, code }`
- Then: 成功=200（消費）、再利用=410/400

## M01:IT-07-007: Email OTP — 送信→受信→検証（TTL/再送間隔/レート制限含む）

### 前提
- MailHog 起動（`mailhog` サービス）
- SMTP を MailHog に向ける（host: localhost, port: 1025）

### 手順
1. `POST /auth/mfa/email/send` を実行
2. 直後に再送を複数回実行し、429 と `Retry-After`, `RateLimM01:IT-*` を確認
3. MailHog API から直近メールを取得し、本文の 6 桁コードを抽出（`\b\d{6}\b`）
4. `POST /auth/mfa/email/verify { challengeId, code }`
5. TTL 経過後のコードで再検証 → 400(`*expired`)
6. 不正コード → 400(`*invalid-code`)

### 期待結果
- レート制限ヘッダが適切
- 有効コードで 200、失効/不正は 400

---

## M01:IT-07-008: Recovery codes — 発行メールの“一度きり表示”の保証

### 手順
1. `POST /auth/mfa/recovery/issue` を実行し、返却 JSON の `recoveryCodes[]` を **その場でのみ** 取得
2. 直後に同 API を再実行
3. MailHog のメール本文にコード表示の有無がないこと（メールには「保存してください」等の案内のみ、**コード本体は含めない** 方針）

### 期待結果
- 1回目は 200 & `recoveryCodes[]`（レスポンスのみ）
- 2回目は 410/400（実装方針に合わせてどちらか）
- 送信メール本文に **リカバリーコードそのものが含まれない**（“一度きり表示”の原則維持）

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 07. IT MFA（TOTP・Email OTP・Recovery）