[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > WebAuthnService

# WebAuthnService I/F 仕様

## 概要
パスキー（WebAuthn）認証の登録・認証フローを担うサービス。  
Controller は WebAuthn REST API とブラウザの navigator.credentials を接続し、本サービスは Challenge 生成と署名検証を行う。

## 主要機能

### registerOptions(userId) → PublicKeyCredentialCreationOptions
- パスキー登録用 challenge を生成
- WebAuthnStore に challenge を紐づけ保存

### register(userId, attestationResponse) → AuthResult
- Attestation を検証（署名/Origin/RP ID）
- 新規 credentialId/publicKey/signCount を保存
- 結果として AuthResult.success を返す

### signinOptions(userId) → PublicKeyCredentialRequestOptions
- 認証フロー開始用 challenge を生成

### signin(userId, assertionResponse) → AuthResult
- Assertion/Signature を検証
- signCount を増分して保存（リプレイ防止）
- 認証成功で AuthResult.success を返す
- MFA_REQUIRED と統合する場合は AuthResultType で表現可能

## 戻り値 DTO
- AuthResult
- LoginResult（WebAuthnログイン省略可：AuthService との連携で使用）

## 利用する Port
- WebAuthnStore（Store）
- NotificationPort（異常検知通知は将来拡張）

## 関連DTO
- WebAuthn登録/認証 REST DTO（別ドキュメント）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > WebAuthnService
