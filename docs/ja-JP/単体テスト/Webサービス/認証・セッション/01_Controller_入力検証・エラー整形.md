[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形
# 01. Controller 入力検証・エラー整形

**対象**: `AuthController` の入力検証・例外→エラー応答整形 / `@Valid`・`@ControllerAdvice`

### テストケース
- **[UT-01-001] ID未入力で 400 + フィールドエラー**  
  **Arrange**: `POST /auth/register/start` body `{"accountId":"", "email":"u@example.com"}`  
  **Assert**: 400, body.code=`AUTH.INVALID_REQUEST`, errors に `accountId`

- **[UT-01-002] メール不正形式**  
  **Assert**: 400, `AUTH.INVALID_EMAIL_FORMAT`

- **[UT-01-003] 例外ハンドラ：ドメインブロック**  
  **Arrange**: Service から `EmailDomainBlockedException` をスローさせる（Mockito）  
  **Assert**: 400, `AUTH.EMAIL_DOMAIN_BLOCKED`

---
[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形