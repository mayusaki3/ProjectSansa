[目次](../目次.md) > [データ定義 目次](目次.md) > rate_limits
# rate_limits（レート制限）

**用途**: ログイン失敗・メール送信などのレート制限

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.rate_limits (
  bucket      text,    -- 'login_fail','email_send' 等
  key         text,    -- 'userId:ip' 等の複合キー文字列
  window_start timestamp,
  count       int,
  PRIMARY KEY ((bucket, key), window_start)
) WITH default_time_to_live = 1200; -- 20分
```
### パターン
- インクリメント：同一 window_start で `count+1`（時間窓はアプリ側で丸め）
- 超過判定：取得→しきい値比較

---
[目次](../目次.md) > [データ定義 目次](目次.md) > rate_limits