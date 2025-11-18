[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 06. Util MFA（TOTP・メールOTP・Recovery）

# 06. Util MFA（TOTP・メールOTP・Recovery）

## TOTP
### M01:UT-06-001: enroll（secret/otpauth URI 発行）
- `POST /auth/mfa/totp/enroll`
- Then: 200、`secret`, `uri` 返却

### M01:UT-06-002: activate（初回コード検証）
- `POST /auth/mfa/totp/activate` `{ "code":"123456" }`
- Then: 200（時計ずれ ±1 step 許容）

### M01:UT-06-003: verify（ログイン時）
- `POST /auth/mfa/totp/verify` `{ "challengeId":"...", "code":"..." }`
- Then: 200、`LoginResponse.authenticated=true`, `amr` に `"mfa"`

## Email OTP
### M01:UT-06-004: 送信（レート制限）
- `POST /auth/mfa/email/send` を連打
- Then: 429

### M01:UT-06-005: 検証成功/失敗/期限切れ
- `POST /auth/mfa/email/verify`
- Then: 成功=200、不正=400（`invalid-code`）、期限切れ=400（`expired`）

## Recovery
### M01:UT-06-006: 発行（その場のみ表示）
- `POST /auth/mfa/recovery/issue`
- Then: 200、`recoveryCodes[]` 返却（保存されない前提）

### M01:UT-06-007: 検証
- `POST /auth/mfa/recovery/verify` `{ "challengeId":"...", "code":"..." }`
- Then: 200（成功時は消費）、不正=400、再利用=410/400

## TOTPパラメータ
### M01:UT-06-008: 既定値の適用
- Given: algorithm/digits/period の指定なし
- Then: algorithm=SHA1, digits=6, period=30 が適用

### M01:UT-06-009: 値検証（許容集合/範囲外）
- algorithm ∈ {SHA1,SHA256,SHA512} 以外 → 400
- digits ∈ {6,8} 以外 → 400
- period は 15/30/60（または仕様で定めた範囲に丸め/拒否）→ 不正は 400

### M01:UT-06-010: 互換（null/0等）
- null/0/未定義 → 既定値へフォールバック（上記と同値になること）

### M01:UT-06-011: otpauth URL 生成の正当性
- otpauth://totp/... に algorithm,digits,period が反映されること

### M01:UT-06-012: QRはUI層で生成（サーバはotpauth文字列を返却）
- サーバはPNG等のバイナリ生成を行わない前提の検証（仕様方針Aに合わせる）

## メールコード
### M01:UT-06-013: コード仕様
- 桁数/文字集合（例：6桁数字）、TTL、照合の上限回数

### M01:UT-06-014: 再送
- 直前コードの無効化/上書き、連続再送のレート制限

## リカバリコード
### M01:UT-06-015: 発行個数と形式
- 例：10 個、XXXX-XXXX 形式

### M01:UT-06-016: 使い捨て/再発行
- 使用済みは無効、再発行時は旧コード全失効

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 06. Util MFA（TOTP・メールOTP・Recovery）