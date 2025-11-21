[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: MailPort

# Port DTO: MailPort

## 位置づけ
Application層から MailPort 実装へ渡す/受け取るDTO群。

---

## VerificationMail

```
name: VerificationMail
fields:
  - to: string
  - purpose: string               // "preReg" | "passwordReset" | "mfa"
  - code: string                  // 検証コード（URLに埋め込む場合は別途テンプレ側で処理）
  - locale: string?               // 例: "ja-JP"
  - resendKey: string?            // 再送レート制限キー（ユーザーID＋用途など）
  - templateId: string?           // 省略時は用途既定のテンプレート
  - context: object?              // 表示名など任意差し込み
```

---

## TemplatedMail

```
name: TemplatedMail
fields:
  - to: string
  - templateId: string
  - locale: string?
  - data: object?                 // 任意差し込み
  - policy: NotificationPolicy?   // レート/優先度等（NotificationPortのDTOと共通）
```

---

## MailSendResult

```
name: MailSendResult
fields:
  - accepted: boolean             // 送信受付
  - messageId: string?            // プロバイダのメッセージID
  - retryAfter: string?           // ISO8601/RFC3339 次回試行時刻（例: "2025-11-21T07:12:00Z"）
  - errorCode: string?            // "BOUNCE" | "TEMP_UNAVAILABLE" | ...
  - warnings: string[]?
```

---

## 注意・補足

```
- セキュリティ: SPF/DKIM/DMARC前提。テンプレ変数は必ずエスケープ。
- 個人情報: channelHintなどでマスキング前提（宛先表示は部分伏せ）。
- レート制限: resendKeyやpolicyを利用して再送抑制を行う。
```

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: MailPort
