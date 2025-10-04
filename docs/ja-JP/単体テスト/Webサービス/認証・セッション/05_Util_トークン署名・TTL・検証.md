[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 05. Util トークン署名・TTL・検証
# 05. Util トークン署名・TTL・検証

**対象**: `TokenService` などの Util（署名・TTL・検証・改竄検出）

### テストケース
- **[UT-05-001] 正常発行→検証OK**  
  **Assert**: `subject, scopes, exp` が往復一致

- **[UT-05-002] TTL 満了で失効**  
  **Arrange**: 短TTL / 時計モック  
  **Assert**: `TokenExpiredException`

- **[UT-05-003] 改竄検出**  
  **Assert**: `TokenInvalidException`

---
[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 05. Util トークン署名・TTL・検証