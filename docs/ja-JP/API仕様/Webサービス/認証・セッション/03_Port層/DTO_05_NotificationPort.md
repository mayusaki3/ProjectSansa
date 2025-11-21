[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: NotificationPort

# Port DTO: NotificationPort

## 位置づけ
マルチチャネル通知（mail/sms/push/inapp/sansa）共通の要求/結果DTO。初版は mail のみ実装。

---

## NotificationRequest

```
name: NotificationRequest
fields:
  - channel: string               // "mail" | "sms" | "push" | "inapp" | "sansa"
  - to: string                    // 宛先（チャネル依存: メール/電話/ユーザーID等）
  - templateId: string
  - locale: string?
  - data: object?                 // 差し込みデータ
  - policy: NotificationPolicy?
```

---

## NotificationPolicy

```
name: NotificationPolicy
fields:
  - rateLimitKey: string?         // レート制限キー（ユーザーID＋種別など）
  - priority: string?             // "low" | "normal" | "high"
  - scheduleAt: string?           // 遅延送達（ISO8601/RFC3339）
  - retryBackoff: string?         // "none" | "exponential"
```

---

## NotificationResult

```
name: NotificationResult
fields:
  - accepted: boolean
  - providerMessageId: string?
  - retryAfter: string?           // 次回試行時刻（ISO8601/RFC3339）
  - errorCode: string?
  - warnings: string[]?
```

---

## 注意・補足

```
- 初版は channel="mail" のみ実装（内部的にMailPortを利用可）。
- UTでは NotificationRequest→MailPort呼び出しのI/F整合をモックで確認。
- 将来 SMS/Push/Sansa を段階的に拡張。
```

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: NotificationPort
