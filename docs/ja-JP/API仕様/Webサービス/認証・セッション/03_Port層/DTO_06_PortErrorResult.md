[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: PortErrorResult

# Port DTO: PortErrorResult

## 位置づけ
MailPort / NotificationPort など、外部サービスへアクセスするPort層で共通利用するエラーDTO。  
外部サービス呼び出し失敗を「ドメインエラー」と分離し、運用・監視・リトライ制御のために利用する。

## PortErrorResult

name: PortErrorResult  
fields:

- errorCode: string # エラーコード（例: "io_error" / "timeout" / "auth_failed" / "quota_exceeded"）
- message: string? # 人間可読な概要メッセージ（ログ・監視向け）
- retryable: boolean # true=再試行で改善する可能性がある（ネットワーク障害、一時的な5xxなど）
- retryAfter: string(ISO8601)? # 再試行推奨時刻。RateLimit応答等から取得した値を格納
- causeType: string? # "client" / "server" / "network" / "config" など原因種別のざっくりした分類
- provider: string? # バックエンドサービス名（例: "ses" / "sendgrid" / "twilio" / "fcm"）
- statusCode: int? # HTTPステータス等、下位サービスのステータスコード
- raw: object? # 下位サービスからのレスポンスのうち、保持してよい範囲の情報（PII除去前提）
- auditRefId: string? # 監査相関ID（アプリ側で採番したIDをそのまま返してもよい）

### 利用方針

- Port実装は、外部サービスの例外/エラー応答を受け取った際に PortErrorResult を生成する。
  - 例外をそのまま上位へ投げるのではなく、可能な範囲で `errorCode` / `retryable` / `causeType` を正規化する。
- Application層は、PortErrorResult を受け取った場合:
  - 監査ログ・運用ログに `errorCode` / `provider` / `statusCode` / `auditRefId` を記録。
  - `retryable` と `retryAfter` に基づき、リトライ方針（自動再試行 or 手動対応）を決定。
  - エンドユーザーには、PortErrorResult の詳細を露出させず、一般的なエラーメッセージのみ返す。

### MailPort / NotificationPort との関連

- MailPort:
  - メール送信失敗時に PortErrorResult を生成し、Application層へ返却またはログ出力に利用。
- NotificationPort:
  - 通知キュー投入失敗、外部Pushサービス障害等を PortErrorResult で表現。
- 将来の Port（例: SmsPort, WebhookPort 等）も同じ PortErrorResult を共有し、監視とエラーハンドリングを一元化する。

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: PortErrorResult
