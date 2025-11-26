[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Store

# Store I/F 仕様

## 概要
認証・登録・セッション管理に用いる永続化ストアの抽象I/F。  
Repository 実装はインメモリ／Cassandra／RDB など任意だが、本 Port は Domain/Application が必要とする最小の契約を定義する。

## 位置づけ
- 層：Port（永続化依存の境界）
- 利用者：AuthService / MfaService / SessionService / WebAuthnService / TokenFacade

## 提供I/F
### PreRegistrationStore
- 保存 / 取得 / 検証ステータス更新  
- Email プリレジストレーション用（preRegId / email / code / expireAt）

### UserStore
- ユーザー基本情報（id, email, passwordHash, mfa設定 等）の CRUD
- email から検索、id から検索

### MfaStore
- TOTP secret の保存/取得
- Email-MFA code の発行/検証
- Recovery Code の発行/消費

### SessionStore
- セッション生成（sessionId, userId, ua, ip, expireAt）
- セッション検索 / 更新 / 無効化
- userId 単位の全セッション無効化

### WebAuthnStore
- credentialId / userId / publicKey / signCount の保存
- 認証時の signCount 更新

## 関連DTO
- [DTO_01_Store_Models.md](DTO_01_Store_Models.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Store
