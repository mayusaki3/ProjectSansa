[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) > MFA

# 多要素認証（MFA）

本章は TOTP / Email OTP / Recovery Code を定義する。  
**ログイン時のレスポンスは「02_ログイン.md」の `LoginResponse` に統一**。

## エンドポイント（例）

| # | 概要 | Method / Path |
|---|---|---|
| 1 | TOTP 秘密鍵の発行（登録） | `POST /auth/mfa/totp/enroll` |
| 2 | TOTP 有効化（初回コード確認） | `POST /auth/mfa/totp/activate` |
| 3 | TOTP コード検証（ログイン時） | `POST /auth/mfa/totp/verify` |
| 4 | Email OTP 送信 | `POST /auth/mfa/email/send` |
| 5 | Email OTP 検証 | `POST /auth/mfa/email/verify` |
| 6 | リカバリーコード発行 | `POST /auth/mfa/recovery/issue` |
| 7 | リカバリーコード検証 | `POST /auth/mfa/recovery/verify` |

## DTO（ドキュメント定義）

### TOTP
**`POST /auth/mfa/totp/enroll` → MfaTotpEnrollResponse**
| フィールド | 型 | 説明 |
|---|---|---|
| secret | string | 表示用（UI は QR を提示） |
| uri | string | `otpauth://totp/...` |

**`POST /auth/mfa/totp/activate`**
| フィールド | 型 | 必須 |
|---|---|---|
| code | string | ✅（6〜10桁、`TOTP_SKEW_STEPS=±1`） |

**`POST /auth/mfa/totp/verify`**
| フィールド | 型 | 必須 |
|---|---|---|
| challengeId | string | ✅ |
| code | string | ✅ |

### Email OTP
**`POST /auth/mfa/email/send`**（ボディは実装方針により省略可）

**`POST /auth/mfa/email/verify`**
| フィールド | 型 | 必須 |
|---|---|---|
| challengeId | string | ✅ |
| code | string | ✅（TTL=5m） |

### Recovery
**`POST /auth/mfa/recovery/issue` → MfaRecoveryIssueResponse**
- `recoveryCodes: string[]`（UI は**その場一度だけ表示**）

**`POST /auth/mfa/recovery/verify`**
| フィールド | 型 | 必須 |
|---|---|---|
| challengeId | string | ✅ |
| code | string | ✅ |

## ステータスコード
- 200: 成功
- 400: `invalid_code` / `expired`
- 401: 未認証（セッション外 API の場合）
- 409: 既に有効化済み 等
- 429, 5xx

## ポリシー
- TOTP の時計ずれ許容: `±1 step`
- Email OTP の再送間隔と 1日上限をレート制限に明記
- Recovery Code の再発行は**既存コードを全失効**してから新規発行

## エラーモデル
- `mfa_required`, `totp_not_enrolled`, `invalid_code`, `rate_limited`

## 参照
- [ログイン](02_ログイン.md)
- [セッション管理](05_セッション管理.md)

---
[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) > MFA
