[目次](../../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 01. IT 登録フロー

# 01. 登録フロー（/auth/pre-register → /auth/verify-email → /auth/register）

## M01:IT-01-001: pre-register（正常）
- Given: `POST /auth/pre-register` `{ email:"user1@example.com", language:"ja-JP" }`
- When: 実行
- Then: 200/202、`success=true`、`throttleMs`（任意）、`Content-Language` あり

## M01:IT-01-002: pre-register（ブロックドメイン）
- Given: `email:"bad@blocked.test"`
- Then: 400、`type=*invalid-argument`、`errors[0].field=="email"`

## M01:IT-01-003: verify-email（正常 → preRegId 取得）
- Given: 上記ユーザーに発行された最新 `code`
- When: `POST /auth/verify-email` `{ email, code }`
- Then: 200、`preRegId`（UUID文字列）、`expiresIn>0`

## M01:IT-01-004: verify-email（期限切れ/不一致）
- Given: 期限切れ or 不一致 `code`
- Then: 400、`type` in [`*expired`, `*invalid-code`]

## M01:IT-01-005: register（正常）
- Given: `preRegId`（未消費、未失効）、`accountId:"user1"`, `password:"P@ssw0rd!"`
- When: `POST /auth/register`
- Then: 201、`success=true`, `userId` 付与, `emailVerified=true`

## M01:IT-01-006: register（preRegId 二重使用）
- Given: 既に消費済みの `preRegId`
- Then: 410/400、`type` in [`*expired`, `*consumed`]

## M01:IT-01-007: accountId 重複
- Given: 既存 `accountId:"user1"`
- Then: 409、`type=*account_id_taken`

## M01:IT-01-008: verify-email 送信 → MailHog で件名/本文/コード検証（ja-JP / en-US）

### 前提
- `docker compose -f docker-compose.dev.yml up -d mailhog`
- `application-cassandra.yml`（または IT 用プロファイル）で SMTP を MailHog に向ける  
  `spring.mail.host=localhost`, `spring.mail.port=1025`, 認証不要

### 手順
1. `POST /auth/pre-register` で `language:"ja-JP"` を指定して送信（英語検証時は `"en-US"`）。
2. `POST /auth/verify-email` を呼ぶ前に、MailHog API から最新メールを取得  
   `GET http://localhost:8025/api/v2/messages`
3. 直近の1通を抽出し、以下を検証
   - 件名（ja）: `メールアドレスの確認コード` を含む
   - 件名（en）: `Verification code` を含む
   - 本文に 6 桁コード（正規表現 `\b\d{6}\b`）が含まれる
   - 本文に対象メールアドレスが含まれる
4. 取り出した 6 桁コードで `POST /auth/verify-email { email, code }` を呼び、成功（200 & `preRegId`）

### 期待結果
- 言語ごとの件名・本文ローカライズが一致
- 有効な `code` で 200/`preRegId` を返す（期限切れ・不一致は既存 M01:IT-01-004 で担保）

---
[目次](../../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 01. IT 登録フロー