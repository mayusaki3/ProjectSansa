[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 02. Service 登録フロー
# 02. Service 登録フロー（pre-register / verify-email / register）

## UT-02-001: pre-register 正常
- Given: `email=valid@example.com`
- When: `POST /auth/pre-register`
- Then:
  - 200/202、`success=true`
  - throttleMs（任意）返却

## UT-02-002: pre-register レート制限
- 同一 email + IP で連続実行
- Then: 429

## UT-02-003: verify-email 成功（preRegId 発行）
- Given: 正しい `email` と 最新 `code`
- Then:
  - 200、`VerifyEmailResponse.preRegId`（UUID文字列）
  - `expiresIn` > 0

## UT-02-004: verify-email 期限切れ -> 400
- Given: 期限切れ `code`
- Then: 400、`type=.../expired`

## UT-02-005: register 成功（ユーザー作成）
- Given: 有効 `preRegId`、`accountId=alice`
- Then: 201、`RegisterResponse.success=true`, `userId` 付与、`emailVerified=true`

## UT-02-006: register preRegId 使い回し -> 410/400
- 同じ `preRegId` を 2 回目に使用
- Then: 410（または 400 `expired/consumed`）

## UT-02-007: accountId 重複 -> 409
- 既存 `accountId` に登録
- Then: 409、`type=.../account_id_taken`

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 02. Service 登録フロー
