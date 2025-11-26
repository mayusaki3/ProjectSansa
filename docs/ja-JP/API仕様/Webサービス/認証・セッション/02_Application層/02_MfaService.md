[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MfaService

# MfaService I/F 仕様

## 概要
ログイン中の Multi-Factor Authentication（TOTP/Email/Recovery/WebAuthn-Sign）を統括するドメインサービス。  
LoginResult の result=MFA_REQUIRED 時に、MfaInfo を生成し、検証 API で結果を返す。

## 主要機能

### TOTP
- enrol (secret 発行)
- activate (初回6桁コードで有効化)
- verify (6桁コード検証)
- disable（将来の仕様として拡張可能）

### Email-MFA
- issue（メールコード送信：MailPort を使用）
- verify（入力コード検証）
- resend（レート制限に従い再送）

### Recovery Code
- issue（複数コード生成）
- list（マスキングして返す）
- consume（1回利用）

### WebAuthn（SIGN）
- challenge生成
- signature検証

## 戻り値 DTO
- 成功：AuthResult（汎用）
- MFA要求時：LoginResult.mfa に設定する MfaInfo  
  → AuthResultType=MFA_REQUIRED

## 利用する Port
- Store（MfaStore / UserStore）
- MailPort（Email-MFA）
- NotificationPort（レート制限/ロック通知）
- TokenFacade（成功時トークン発行）

## 関連DTO
- [DTO_01_AuthResult_LoginResult.md](DTO_01_AuthResult_LoginResult.md)
- [DTO_05_MFA_TOTP_Email_Recovery_Issue.md](DTO_05_MFA_TOTP_Email_Recovery_Issue.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MfaService
