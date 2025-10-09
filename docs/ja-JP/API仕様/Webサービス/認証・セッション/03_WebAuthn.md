[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) >  WebAuthn（パスキー）

# WebAuthn（パスキー）

**/webauthn** 配下に、登録（enroll）と認証（assertion）を分離して定義する。  
既定値: `attestation="none"`, `userVerification="preferred"`。

## エンドポイント

| # | 概要 | Method / Path |
|---|---|---|
| 1 | 登録オプション取得 | `GET /webauthn/register/options` |
| 2 | 登録検証 | `POST /webauthn/register/verify` |
| 3 | 認証チャレンジ取得 | `GET /webauthn/challenge` |
| 4 | 認証アサーション検証 | `POST /webauthn/assertion` |
| 5 | 登録済みクレデンシャル一覧 | `GET /webauthn/credentials` |
| 6 | クレデンシャル失効 | `DELETE /webauthn/credentials/{credentialId}` |

## リクエスト/レスポンス DTO（ドキュメント定義）

### A) 登録（ユーザーひも付け）

**`GET /webauthn/register/options` → WebAuthnRegisterOptionsResponse**
| フィールド | 型 | 説明 |
|---|---|---|
| challenge | string | Base64url |
| rpId | string | Relying Party ID |
| user | string | RP user.id（内部IDをエンコード） |
| pubKeyCredParams | object[] | `[{type:"public-key", alg:-7}, ...]` |
| attestation | string | 既定 `"none"` |

**`POST /webauthn/register/verify` → WebAuthnRegisterVerifyResponse**
| フィールド | 型 | 説明 |
|---|---|---|
| credentialId | string | Base64url |
| publicKey | string | COSE Key（表現は実装準拠） |
| aaguid | string | 任意 |
| transports | string[] | 任意 |
| signCount | number | 任意 |

**検証入力**
- `clientDataJSON`, `attestationObject`（必須）

**Status**: 200/400/409/5xx

### B) 認証

**`GET /webauthn/challenge` → WebAuthnChallengeResponse**
- `challenge`, `rpId`, `timeout`, `userVerification="preferred"`

**`POST /webauthn/assertion` → LoginResponse**
- 入力: `id`, `clientDataJSON`, `authenticatorData`, `signature`, `userHandle?`
- 成功: `authenticated=true`, `amr+=["webauthn"]`
- MFA必要: `authenticated=false`, `mfaRequired=true`

### C) 管理

**`GET /webauthn/credentials` → WebAuthnCredentialListResponse**
- 配列: `credentialId`, `aaguid?`, `transports?`, `signCount?`

**`DELETE /webauthn/credentials/{credentialId}`**
- 204 / 404

## ポリシー
- `attestation="none"` を原則（プライバシ保護）
- 将来 `userVerification="required"` への切替可（リスク評価に応じて）

## エラーモデル（例）
- `invalid_assertion`, `unregistered_credential`, `rp_mismatch`

## 参照
- [ログイン](02_ログイン.md)
- [セッション管理](05_セッション管理.md)

---
[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) >  WebAuthn（パスキー）
