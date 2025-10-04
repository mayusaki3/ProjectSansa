[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 03. Service ログイン・セッション管理
# 03. Service ログイン・セッション管理

**対象**: `AuthService` ログイン / セッション（多端末可、MFA判定は別ドキュメント）

### テストケース
- **[UT-03-001] 正常ログイン**  
  **Arrange**: 正しい認証情報、デバイスID あり  
  **Assert**: `access/refresh/sessionId` 生成、`LOGIN_OK` 監査

- **[UT-03-002] 誤パスワード**  
  **Assert**: `InvalidCredentialsException`、失敗レート制限カウント +1

- **[UT-03-003] 多端末ログイン**  
  **Arrange**: A/B で login を2回呼ぶ  
  **Assert**: 2つのセッションが保存、各デバイスIDと紐付く

---
[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 03. Service ログイン・セッション管理