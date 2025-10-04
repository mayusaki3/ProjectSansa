[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) > MFA
# MFA（TOTP 優先 + メールOTPフォールバック）

## ポリシー
- 既定は **TOTP**。アプリ利用不可時のみ **メールOTP** を一時フォールバック。
- 高リスク時は `email_otp` を提供しない。

## エンドポイント
- `POST /auth/mfa/totp/enroll` / `POST /auth/mfa/totp/activate` / `POST /auth/mfa/totp/verify`
- `POST /auth/mfa/email/request` / `POST /auth/mfa/email/verify`
- （任意）`POST /auth/mfa/recovery/issue` / `POST /auth/mfa/recovery/verify`

## 既定値
- `TOTP_SKEW_STEPS=±1`, `EMAIL_OTP_TTL=5m`, `MFA_MAX_AGE=30d`, `MFA_EMAIL_RATE_LIMIT=1/60s,10/day`

## 参照
- [ログイン](02_ログイン.md)
- [セッション管理](05_セッション管理.md)s

---
[目次](../../../目次.md) > API仕様 > Webサービス> [認証・セッション 目次](目次.md) > MFA
