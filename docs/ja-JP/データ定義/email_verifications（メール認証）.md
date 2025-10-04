[目次](../目次.md) > [データ定義 目次](目次.md) > email_verifications
# email_verifications（メール認証）

**用途**: 登録時のメールコード検証（多要素：メール）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.email_verifications (
  challenge_id  uuid PRIMARY KEY,
  email         text,
  account_id    text,
  code          text,
  created_at    timestamp
) WITH default_time_to_live = 900; -- 15分
```
### ポイント
- TTL で自然失効
- コード誤り回数は `rate_limits` にて制御

---
[目次](../目次.md) > [データ定義 目次](目次.md) > email_verifications