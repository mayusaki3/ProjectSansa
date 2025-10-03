[目次](../../../目次.md) > API仕様 > [Webサービス](../../Webサービス・目次.md) > 認証・セッション > WebAuthn（パスキー）
# WebAuthn（パスキー）

## 登録（複数端末対応）
- `GET /webauthn/register/options` → `{ challenge, rpId, user, pubKeyCredParams, attestation: "none" }`
- 端末で新規パスキー作成 → `POST /webauthn/register/verify { clientDataJSON, attestationObject }`
- 成功: 資格情報（`credentialId`, `publicKey`, `aaguid`, `transports`, `signCount`）を保存

## 認証
- `GET /webauthn/challenge` → `{ challenge, rpId, userVerification }`
- `POST /webauthn/assertion { id, clientDataJSON, authenticatorData, signature, userHandle? }`

## 設計
- RP ID/Origin は運用で固定（例: `auth.sansa.example`）
- User Verification: `preferred`（将来 `required` 切替可）
- Attestation: 既定 `none`

## 管理
- `GET /webauthn/credentials`（一覧）
- `POST /webauthn/credential/revoke { credentialId }`（失効）

## 参照
- [ログイン](./ログイン.md)
- [セッション管理](./セッション管理.md)

---
[目次](../../../目次.md) > API仕様 > [Webサービス](../../Webサービス・目次.md) > 認証・セッション > WebAuthn（パスキー）
