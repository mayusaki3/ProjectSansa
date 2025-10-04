[目次](../目次.md) > [データ定義 目次](目次.md) > blocklists
# blocklists（ドメイン/アカウント）

**用途**: ドメイン/アカウントのブロック

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.block_domains (
  domain text PRIMARY KEY,
  reason text,
  created_at timestamp
);

CREATE TABLE IF NOT EXISTS sansa_auth.block_accounts (
  account_id text PRIMARY KEY,
  reason text,
  created_at timestamp
);
```
### 運用
- 登録/メール送信前にブロックテーブル照会
- ドメインは `users_by_email.domain` とも連携可能

---
[目次](../目次.md) > [データ定義 目次](目次.md) > blocklists