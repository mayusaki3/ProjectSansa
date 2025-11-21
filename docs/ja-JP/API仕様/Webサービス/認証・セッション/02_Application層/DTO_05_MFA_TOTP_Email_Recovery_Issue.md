[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: MFA（TOTP/Email/Recovery/Issue）

# DTO: MFA（TOTP/Email/Recovery/Issue）（Application層）

位置づけ:
MfaService の各操作に対応する Application層 DTO。

## 共通 MfaInfo
- type : string（必須） `"totp" | "email" | "recovery" | "webauthn" | "fido" | "sms" | "sansa"`
- transactionId : string（任意）
- remain : int（任意）
- retryAfter : string(ISO8601)（任意）
- resendAvailableAt : string(ISO8601)（任意）
- channelHint : string（任意）
- hints : string[]（任意）

## TOTP
- EnrollResponse: { secret, qrUri }
- ActivateRequest: { code }
- VerifyRequest: { code, transactionId? }

## Email OTP
- SendRequest: { transactionId? }
- VerifyRequest: { code, transactionId? }

## Recovery
- IssueResponse: { codes[] }
- VerifyRequest: { code, transactionId? }

## MfaIssue
- IssueResponse: { mfa:MfaInfo }

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: MFA（TOTP/Email/Recovery/Issue）
