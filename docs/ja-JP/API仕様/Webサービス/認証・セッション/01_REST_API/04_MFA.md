[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MFA

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
| secret | string | Base32 で符号化された TOTP シークレット（生成直後のみ返却） |
| uri | string | `otpauth://totp/...` |
| issuer | string | Authenticator登録用の発行者名（サービス名） |
| account | string | ユーザー識別表示（登録メールアドレス等） |
| algorithm | string | 既定値: SHA1 |
| digits | int | 既定値: 6 |
| period | int | 既定値: 30 |

#### 各項目の補足

- **algorithm**: TOTP 生成時に用いる HMAC のハッシュ方式。RFC 6238 に準拠し、`SHA1` / `SHA256` / `SHA512` を想定。サーバと認証アプリで一致している必要がある（互換性重視の既定は `SHA1`）。
- **digits**: 生成されるワンタイムパスワードの桁数。一般的に `6`（既定）または `8`。サーバ検証側と認証アプリで一致している必要がある（例：6桁は 1,000,000 通り）。
- **period**: ワンタイムパスワードの有効時間（秒）。既定は `30` 秒。サーバ検証時は時刻ずれ吸収のため ±1 ステップ程度の許容を推奨。

**`POST /auth/mfa/totp/activate`**
| フィールド | 型 | 必須 |
|---|---|---|
| code | string | ✅（6〜10桁、`TOTP_SKEW_STEPS=±1`） |

**`POST /auth/mfa/totp/verify`**
| フィールド | 型 | 必須 |
|---|---|---|
| challengeId | string | ✅ |
| code | string | ✅ |

#### セキュリティ
- レート制限: アカウント＋IP 複合で 5/min 程度。
- 監査イベント: MFA_TOTP_ENROLL, MFA_TOTP_VERIFY_SUCCESS/FAIL, MFA_TOTP_ENABLED/DISABLED。
- ログには secret / uri を出力しない。

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
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MFA
