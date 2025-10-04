[目次](../目次.md) > [データ定義 目次](目次.md) > mfa
# mfa（TOTP/メール）

**用途**: TOTP/メール切替の状態を保持

### 物理テーブル
```sql
-- TOTP 状態
CREATE TABLE IF NOT EXISTS sansa_auth.mfa_totp (
  user_id     uuid PRIMARY KEY,
  secret      text,
  enabled     boolean,
  updated_at  timestamp
);

-- 一時的なメールMFA切替フラグ（TTLで戻る）
CREATE TABLE IF NOT EXISTS sansa_auth.mfa_temp_switch (
  user_id     uuid PRIMARY KEY,
  method      text,       -- email
  created_at  timestamp
) WITH default_time_to_live = 86400; -- 24h
```

---
[目次](../目次.md) > [データ定義 目次](目次.md) > mfa