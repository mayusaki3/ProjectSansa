[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: MailPort

# Port DTO: MailPort

## 位置づけ
Application層から MailPort 実装へ渡す/受け取るDTO群。

## VerificationMail
メール検証（事前登録、PWリセット、Email-MFA など）に用いる送信DTO。

```
name: VerificationMail
fields:
- to: string                      # 宛先メールアドレス
- purpose: string                 # "preReg" | "passwordReset" | "mfa"
- code: string                    # 検証コード（URLに埋め込む場合はテンプレート側で処理）
- locale: string?                 # 例: "ja-JP"
- resendKey: string?              # 再送レート制限キー（ユーザーID＋用途など）
- templateId: string?             # 省略時は用途既定テンプレート
- context: object?                # 表示文など任意差し込み
- auditRefId: string?             # 監査相関ID（追跡・監査ログ連携）
```

## TemplatedMail
文面テンプレートを直接指定して送るためのDTO。

```
name: TemplatedMail
fields:
- to: string
- templateId: string              # テンプレート識別子
- locale: string?                 # 例: "ja-JP"
- variables: object?              # 埋め込み用変数
- resendKey: string?              # レート制限キー
- auditRefId: string?             # 監査相関ID
```

### テンプレート選択・ロケール決定ルール
- `templateId` 指定時: そのテンプレートを使用。  
- `templateId` 省略時: `purpose` に紐づく既定テンプレートを使用。  
- `locale` 省略時: ユーザー設定ロケール → サーバー既定ロケール の順で解決する。  

## MailSendResult
メール送信結果DTO。

```
name: MailSendResult
fields:
- success: boolean
- messageId: string?              # 実装依存の送信ID
- error: PortErrorResult?         # 失敗時の共通エラーDTO
- retryAfter: string?             # ISO8601。429/一時失敗時の再試行目安
```

## セキュリティ/運用メモ
- コンテンツはHTML/テキストともに XSS/HTMLインジェクション対策を行う。  
- 送信ドメインは SPF/DKIM/DMARC を整備。  
- レート制限・再送制御は `resendKey` をキーに MailPort 実装側で行う。  
- 監査: 重要操作送信時は `auditRefId` を必ず付与する。  

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: MailPort
