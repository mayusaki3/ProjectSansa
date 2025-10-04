[目次](../目次.md) > [データ定義 目次](目次.md) > email_change_requests
# email_change_requests（メール変更）

**用途**: メール変更の二段階認証（旧/新メール）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.email_change_requests (
  request_id   uuid PRIMARY KEY,
  user_id      uuid,
  old_email    text,
  new_email    text,
  code         text,
  created_at   timestamp
) WITH default_time_to_live = 1800; -- 30分
```
### フロー
1. 申請 → 旧/新メールへ通知
2. コード検証後に `users` と逆引きテーブルを更新

---
[目次](../目次.md) > [データ定義 目次](目次.md) > email_change_requests