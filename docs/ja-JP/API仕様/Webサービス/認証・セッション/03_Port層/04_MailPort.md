[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MailPort

# MailPort I/F 仕様

## 位置づけ・役割

MailPort は、sansa-auth から外部メール送信基盤へのアクセスを抽象化する Port I/F です。

初版での主な用途:

- 事前登録メール（メールアドレス検証リンク/コードの送信）
- パスワードリセット用メール
- Email ベースの MFA コード送信（TOTP ではなくワンタイムコードをメールで送る場合）

実際の送信先:

- 開発環境: MailHog 等のテスト用 SMTP サーバー
- 本番環境: 商用メールサービス（SendGrid 等）または自前 SMTP

アプリケーション層からは「テンプレートキー＋パラメータ」で呼び出し、本文組み立てやロギングは MailPort 実装側に委譲する。

## インターフェース定義

```java
public interface MailPort {

    /**
     * メールテンプレートを指定して送信する。
     * 事前登録メール/パスワードリセット/MFA コード送信など、
     * 用途ごとにテンプレートを切り替える。
     *
     * @param request テンプレートベースの送信リクエスト
     * @return 送信結果
     */
    MailPortResult sendTemplateMail(TemplateMailRequest request);

    /**
     * 生テキスト/HTML を直接指定して送信する。
     * 管理ツールや一部の例外通知など、テンプレート管理外の用途向け。
     *
     * @param request 直接内容を指定する送信リクエスト
     * @return 送信結果
     */
    MailPortResult sendDirectMail(DirectMailRequest request);
}
```

- `TemplateMailRequest` / `DirectMailRequest` / `MailPortResult` の詳細は DTO ドキュメントを参照。

## メソッド仕様詳細

### sendTemplateMail(TemplateMailRequest request)

用途:

- 事前登録メール送信
- パスワードリセットメール送信
- MFA コードを含んだメール送信

主なフィールド（概要）:

- `TemplateMailRequest.templateKey`:
  - 使用するテンプレートを一意に表すキー。
  - 言語別テンプレートにも対応できる設計とし、`locale` 等を含める。
- `TemplateMailRequest.to`:
  - 宛先メールアドレス（複数可）。
- `TemplateMailRequest.variables`:
  - テンプレート中に差し込むプレースホルダ値（例: `{"code":"123456","displayName":"User"}`）。

動作:

- MailPort 実装は `templateKey` / `locale` に応じたテンプレートを取得し、`variables` を適用して本文を生成。
- `MailPortResult` には成功/失敗、および `PortErrorResult` によるエラー詳細（ログ用）を格納。
- テスト環境では MailHog をターゲットとし、IT での E2E 確認を行う。

### sendDirectMail(DirectMailRequest request)

用途:

- 管理者向け一時的なお知らせ
- テンプレート定義がまだない、一時的な障害通知など

主なフィールド（概要）:

- `DirectMailRequest.to` / `cc` / `bcc`
- `DirectMailRequest.subject`
- `DirectMailRequest.textBody` / `htmlBody`

動作:

- 呼び出し元が本文をすべて組み立てる。
- MailPort 実装はヘッダの付与（`Message-ID` など）と送信のみを担当する。

## MailPortResult とエラー処理

`MailPortResult` と `PortErrorResult` の位置づけ（DTO 側仕様に準拠）:

- `MailPortResult.success`: 送信処理がポートレベルでは成功したか。
- `MailPortResult.error`: 失敗時に `PortErrorResult` をセット。
- `MailPortResult.providerMessageId`: 外部プロバイダが付与するメッセージ ID（存在する場合）。

設計方針:

- 呼び出し元（AuthService 等）からは「メール送信の成否」を bool として扱えるようにする。
- 送信失敗時:
  - 認証フロー継続が不可能な場合（事前登録メール送信に失敗など）は、AuthResult でエラーを返す。
  - 監査用エラーコード等は `PortErrorResult` に保持し、ログ/アラートに使用。

## テンプレートとローカライズ

テンプレートキー例（設計案）:

- `REGISTRATION_PRECONFIRM` / `REGISTRATION_COMPLETED`
- `PASSWORD_RESET`
- `MFA_EMAIL_CODE`

ローカライズ:

- `TemplateMailRequest` に `locale` を含めるか、または `MailTemplateKey` 内に言語情報を含める。
- Project Sansa の言語設定との対応は AuthService 側で解決し、MailPort は「言語を指定されたテンプレートキー」として扱う。

## セキュリティ・プライバシ配慮

- 個人情報を本文に含めすぎない（フルメールアドレス/電話番号をそのまま本文に書かないなど）。
- 再送可能回数・有効期限は MFA / パスワードリセットのドメインロジック側で制御し、MailPort はあくまで「送れるかどうか」だけを担当。
- テンプレート内にシークレット情報（APIキー等）を仕込まない。

## 関連ドキュメント

- [Port DTO: MailPort](./DTO_04_MailPort.md)
- [Port DTO: NotificationPort](./DTO_05_NotificationPort.md)
- [Port DTO: PortErrorResult](./DTO_06_PortErrorResult.md)
- [AuthService I/F 仕様](../02_Application層/01_AuthService.md)
- [MfaService I/F 仕様](../02_Application層/02_MfaService.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > MailPort
