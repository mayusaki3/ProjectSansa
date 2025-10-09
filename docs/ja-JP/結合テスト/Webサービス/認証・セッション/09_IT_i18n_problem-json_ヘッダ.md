[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 09. IT i18n・problem-json・セキュリティヘッダ

# 09. i18n / problem+json / セキュリティヘッダ

## i18n
### IT-09-001: Accept-Language → Content-Language（成功応答）
- 例: `Accept-Language: ja-JP` で `/auth/login` 成功
- Then: `Content-Language: ja-JP`

### IT-09-002: Accept-Language → Content-Language（エラー応答）
- 例: バリデーションエラー
- Then: `problem+json` 本文の `title/detail` が指定言語、`Content-Language` 付与

## problem+json
### IT-09-003: エラーフォーマット共通確認
- 代表 API で `type/title/status/detail/errors[]/traceId` を確認

## セキュリティヘッダ
### IT-09-004: 認可必須エンドポイント未認証
- `/auth/session`, `/sessions`, `/webauthn/credentials` 等を AT なしで呼ぶ
- Then: 401、`WWW-Authenticate` 確認（必要に応じ）

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 09. IT i18n・problem-json・セキュリティヘッダ