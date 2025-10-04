[目次](../目次.md) > [データ定義 目次](目次.md) > auth_audit
# auth_audit（監査ログ）

**用途**: 監査ログ（ユーザー/時間範囲で取得）

### 物理テーブル
```sql
-- ユーザーごとの時系列
CREATE TABLE IF NOT EXISTS sansa_auth.auth_audit (
  user_id   uuid,
  yyyymmdd  text,       -- 日単位パーティション（例: 2025-10-04）
  ts        timeuuid,   -- 時系列（降順取得用に timeuuid）
  action    text,       -- REGISTER_START / LOGIN_OK / LOGOUT_ALL / ...
  ip        text,
  ua        text,
  result    text,       -- OK / NG / REJECTED など
  detail    text,
  PRIMARY KEY ((user_id, yyyymmdd), ts)
) WITH CLUSTERING ORDER BY (ts DESC);
```
### 取得例
- 当日：`SELECT * FROM auth_audit WHERE user_id=? AND yyyymmdd='2025-10-04' LIMIT 100;`
- 期間：アプリ側で日付リストを回し **範囲集約**

---
[目次](../目次.md) > [データ定義 目次](目次.md) > auth_audit