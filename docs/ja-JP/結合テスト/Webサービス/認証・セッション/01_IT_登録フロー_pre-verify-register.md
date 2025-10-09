[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 01. IT 登録フロー

# 01. 登録フロー（/auth/pre-register → /auth/verify-email → /auth/register）

## IT-01-001: pre-register（正常）
- Given: `POST /auth/pre-register` `{ email:"user1@example.com", language:"ja-JP" }`
- When: 実行
- Then: 200/202、`success=true`、`throttleMs`（任意）、`Content-Language` あり

## IT-01-002: pre-register（ブロックドメイン）
- Given: `email:"bad@blocked.test"`
- Then: 400、`type=*invalid-argument`、`errors[0].field=="email"`

## IT-01-003: verify-email（正常 → preRegId 取得）
- Given: 上記ユーザーに発行された最新 `code`
- When: `POST /auth/verify-email` `{ email, code }`
- Then: 200、`preRegId`（UUID文字列）、`expiresIn>0`

## IT-01-004: verify-email（期限切れ/不一致）
- Given: 期限切れ or 不一致 `code`
- Then: 400、`type` in [`*expired`, `*invalid-code`]

## IT-01-005: register（正常）
- Given: `preRegId`（未消費、未失効）、`accountId:"user1"`, `password:"P@ssw0rd!"`
- When: `POST /auth/register`
- Then: 201、`success=true`, `userId` 付与, `emailVerified=true`

## IT-01-006: register（preRegId 二重使用）
- Given: 既に消費済みの `preRegId`
- Then: 410/400、`type` in [`*expired`, `*consumed`]

## IT-01-007: accountId 重複
- Given: 既存 `accountId:"user1"`
- Then: 409、`type=*account_id_taken`

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 01. IT 登録フロー