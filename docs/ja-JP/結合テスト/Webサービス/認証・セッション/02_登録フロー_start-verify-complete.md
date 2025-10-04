[目次](../../../目次.md) > 結合テスト > Webサービス> [認証・セッション 結合テスト 目次](目次.md) > 02. 登録フロー
# 02. 登録フロー（start→verify→complete）

**観点**: 多段フローの状態遷移（start → verify → complete）

### テストケース
- **[IT-02-001] 正常：start→verify→complete で本登録**  
  **Pre**: 未登録の ID/メール  
  **Step**:  
  1. `/auth/register/start` → `200` で `challengeId` 取得  
  2. `/auth/register/verify` 正しい `code` → `200` 仮登録状態へ  
  3. `/auth/register/complete` パスワード設定 → `201` 本登録  
  **Expect**: ユーザー状態 `ACTIVE`、監査に `REGISTER_*` 記録

- **[IT-02-002] verify 誤コード**  
  **Expect**: `400`, `AUTH.INVALID_VERIFICATION_CODE`（回数上限付近で `429`）

- **[IT-02-003] complete 弱いパスワード**  
  **Expect**: `400`, `AUTH.WEAK_PASSWORD`

- **[IT-02-004] 同一メールの再登録阻止**  
  **Expect**: start 段階で `409`, `AUTH.EMAIL_DUPLICATED`

---
[目次](../../../目次.md) > 結合テスト > Webサービス> [認証・セッション 結合テスト 目次](目次.md) > 02. 登録フロー