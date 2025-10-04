[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo インメモリ実装（契約テスト）
# 07. Repo インメモリ実装（契約テスト）

**対象**: Repo 契約テスト（インメモリ実装）

### テストケース
- **[UT-07-001] UserRepo 契約**  
  **Assert**: `save/findById/findByAccountId/findByEmail/delete` の期待どおりの整合

- **[UT-07-002] SessionRepo 契約**  
  **Assert**: `save/findById/findByUserId/delete/deleteByUserDevice` の整合

- **[UT-07-003] 監査記録 Repo 契約（任意）**  
  **Assert**: append / range query の整合

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo インメモリ実装（契約テスト）