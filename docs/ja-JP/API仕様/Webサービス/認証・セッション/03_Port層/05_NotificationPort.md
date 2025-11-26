[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > NotificationPort

# NotificationPort I/F 仕様

## 位置づけ・役割

NotificationPort は、メール以外の「即時通知チャネル」を抽象化する Port I/F です。

初版で想定する用途（将来も見据えた設計）:

- 異常ログイン検知のユーザー通知（例: プッシュ通知/アプリ内通知）
- 管理者向けアラート（ロック/レート制限多発など）
- 将来的な SNS 連携・Project Sansa 内通知（`type="sansa"`） など

MailPort が「Email」に特化しているのに対し、NotificationPort は「チャネルを問わない汎用通知」の窓口とする。

## インターフェース定義

^^^java
public interface NotificationPort {

    /**
     * 単一の通知を送信する。
     * チャネル種別や優先度は request 内のフィールドで制御する。
     *
     * @param request 通知リクエスト
     * @return 送信結果
     */
    NotificationPortResult send(NotificationRequest request);
}
^^^

- `NotificationRequest` / `NotificationPortResult` の詳細は DTO ドキュメントを参照。

## NotificationRequest の概要

代表的なフィールド（DTO 側仕様に準拠した概要）:

- `channel`: 通知チャネル（例: `"push"`, `"web"`, `"slack"`, `"sansa"` など）
- `recipient`: 宛先（ユーザーID, デバイスID, Webhook URL 等、チャネルごとに解釈）
- `title`: 通知タイトル（ユーザー向け表示用）
- `body`: 通知本文（短いテキスト）
- `severity`: 通知レベル（INFO/WARN/ALERT 等）
- `metadata`: 任意の付加情報（JSON マップ）

sansa-auth から見た典型的な使い方:

- アカウントロック発生時:
  - `channel="sansa"` / `recipient=ユーザーID` / `severity="ALERT"`
- レート制限超過時（管理者向け）:
  - `channel="sansa"` or `"slack"` / `recipient=管理グループID` / `severity="WARN"`

## NotificationPortResult とエラー処理

`NotificationPortResult` と `PortErrorResult` の位置づけ:

- `NotificationPortResult.success`: 通知がチャネルレベルで受理されたかどうか。
- `NotificationPortResult.error`: 失敗時の詳細（`PortErrorResult`）を保持。
- `NotificationPortResult.providerMessageId`: 外部サービスが付与する ID（利用する場合）。

設計方針:

- 認証フローに必須ではない通知（例: 異常ログインのユーザー通知）は、
  通知失敗があっても認証フロー自体は継続できる設計とする。
- 管理者向けのクリティカルなアラートは、ログ/メトリクスと組み合わせて監視する。
- `NotificationPortResult` が失敗でも、処理自体はロールバックしない（認証/ロック自体は成立させる）。

## MailPort との役割分担

- MailPort:
  - ユーザー向けの「メール」を送る。
  - 事前登録/パスワードリセット/MFA コードなど「ユーザ操作に直結する」通知。
- NotificationPort:
  - メール以外のチャネル（アプリ内通知/外部サービス連携等）。
  - 監査/アラート寄りの通知や、UX 向上のための補助通知。

将来的な拡張例:

- Push 通知基盤（Firebase Cloud Messaging 等）を背後にぶら下げる。
- Project Sansa のコアに「通知センター」を持ち、`channel="sansa"` をそれにルーティングする。

## 典型シーケンス例

### アカウントロック発生時

1. AuthService がログイン失敗回数を判定し、ロック閾値を超えたと判断。
2. AuthResultType.LOCKED を設定し、呼び出し元に返却。
3. 同時に NotificationPort.send を呼び出し:
   - ユーザー向け: `channel="sansa"`, `recipient=userId`, `severity="ALERT"`
   - 管理者向け: `channel="sansa"` or `"slack"`, `recipient=adminGroup`, `severity="WARN"`
4. NotificationPortResult が失敗した場合はログに記録しつつ、認証ロジックの結果（ロック）は維持。

## 関連ドキュメント

- [Port DTO: NotificationPort](./DTO_05_NotificationPort.md)
- [Port DTO: PortErrorResult](./DTO_06_PortErrorResult.md)
- [AuthService I/F 仕様](../02_Application層/01_AuthService.md)
- [SessionService I/F 仕様](../02_Application層/03_SessionService.md)
- [MfaService I/F 仕様](../02_Application層/02_MfaService.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > NotificationPort
