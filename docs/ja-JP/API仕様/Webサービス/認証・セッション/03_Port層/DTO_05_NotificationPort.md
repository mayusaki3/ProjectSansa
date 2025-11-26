[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: NotificationPort

# Port DTO: NotificationPort

## 位置づけ
Application層から NotificationPort 実装へ渡す/受け取るDTO群。  
異常ログイン・アカウントロック・レート制限など、ユーザー/管理者向けの即時通知を扱う。

## NotificationMessage

汎用的な通知メッセージDTO。チャンネル種別に依存しない共通情報を保持する。

name: NotificationMessage  
fields:

- id: string? # 通知ID（省略時はPort実装側で採番してもよい）
- to: string # 宛先識別子（ユーザーID、メールアドレス、電話番号などチャネル依存）
- channel: string # "email" / "push" / "sms" / "webhook" など、通知チャネル種別
- type: string # 通知種別（例: "abnormalLogin" / "accountLocked" / "rateLimited" / "mfaAlert"）
- locale: string? # 例: "ja-JP"。省略時はユーザーの言語設定にフォールバック
- templateId: string? # テンプレート識別子（MailPort等と連携する場合に使用）
- title: string? # 通知タイトル（チャネルにより使用/非使用を切り替え）
- message: string? # 事前に組み立て済みの文面。テンプレート使用時は省略可
- variables: object? # テンプレートに埋め込む変数（IP、UA、場所情報など）
- severity: string? # "info" / "warning" / "critical" などの重要度
- auditRefId: string? # 監査相関ID（監査ログ・セキュリティログとひも付けるためのID）
- createdAt: string(ISO8601)? # 通知生成時刻（省略可。Port側で付与してもよい）

### 想定ユースケース
- 異常ログイン検知 → ユーザーに警告通知 / 管理者にアラート
- 多数のログイン失敗 → アカウントロック通知
- レート制限発動 → 一時的に試行をブロックした旨を通知
- MFA 設定変更 / 解除 → セキュリティ関連の状態変化通知

## NotificationResult

NotificationPort 実装が Application層へ返す結果DTO。

name: NotificationResult  
fields:

- success: boolean # true=通知受付成功（送信またはキュー投入完了）
- error: PortErrorResult? # 失敗時の詳細情報。成功時は null/省略

### 利用方針
- Application層は `success=false` かつ `error.retryable=true` の場合、必要に応じてリトライポリシーを適用する。
- エンドユーザーには PortErrorResult の詳細を直接返さず、監査ログ・運用監視向けの情報として扱う。

## Port間の役割分担（参考）

- MailPort: メール送信そのものを担当（事前登録メール、PWリセット、Email-MFA 等）
- NotificationPort: 「どのイベントを誰に、どのチャネルで通知するか」の制御を担当
  - 必要に応じて MailPort や外部Pushサービス等を内部で呼び出す
  - サービス全体の通知ポリシー（重要度・チャネル選択・レート制限との連携）を集約

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: NotificationPort
