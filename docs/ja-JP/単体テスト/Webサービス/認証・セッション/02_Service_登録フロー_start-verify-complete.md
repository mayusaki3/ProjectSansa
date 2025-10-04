[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 02. Service 登録フロー
# 02. Service 登録フロー（start→verify→complete）

**対象**: `AuthService` 登録フロー `start → verify → complete` の状態遷移

### テストケース
- **[UT-02-001] 正常：start で challenge 発行**  
  **Arrange**: 未登録 ID/メール / メール送信モック  
  **Assert**: `challengeId` 生成、`REGISTER_START` 監査発火（監査モック verify）

- **[UT-02-002] verify 正コードで仮登録**  
  **Assert**: `REGISTER_VERIFY_OK`、再送レート制限カウンタ維持

- **[UT-02-003] verify 誤コード**  
  **Assert**: `InvalidVerificationCodeException`（回数超過時は `TooManyAttemptsException`）

- **[UT-02-004] complete 弱いパスワード**  
  **Assert**: `WeakPasswordException`

- **[UT-02-005] 重複（メール/ID）**  
  **Assert**: それぞれ `DuplicatedEmailException` / `DuplicatedAccountIdException`

---
[目次](../../../目次.md) > 単体テスト > Webサービス> [認証・セッション 単体テスト 目次](目次.md) > 02. Service 登録フロー
