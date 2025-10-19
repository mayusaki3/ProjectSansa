[目次](../目次.md) > [データ定義 目次](目次.md) > users
# users（ユーザー）

**用途**: アカウント基本情報。高頻度検索のため**逆引きテーブル**を併設。

### 物理テーブル
```sql
-- 主表
CREATE TABLE IF NOT EXISTS sansa_auth.users (
  user_id     uuid PRIMARY KEY,
  account_id  text,
  email       text,
  status      text,        -- ACTIVE / PENDING / LOCKED / DELETED
  password    text,        -- ハッシュ（PBKDF2/BCrypt/Argon2 等）
  locale      text,        -- 既定言語
  created_at  timestamp,
  updated_at  timestamp
);

-- 逆引き（account_id → user）
CREATE TABLE IF NOT EXISTS sansa_auth.users_by_account (
  account_id  text PRIMARY KEY,
  user_id     uuid
);

-- 逆引き（email → user）
CREATE TABLE IF NOT EXISTS sansa_auth.users_by_email (
  email       text PRIMARY KEY,
  user_id     uuid,
  domain      text        -- ドメインブロック/統計用
);
```

### ポイント
- **一意性**は `users_by_account`, `users_by_email` の **PRIMARY KEY** 衝突で保証
- 削除時は **主表 + 逆引き**の両方を削除

---
[目次](../目次.md) > [データ定義 目次](目次.md) > users