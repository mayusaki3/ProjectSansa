[目次](../目次.md) > [データ定義 目次](目次.md) > token_blacklist
# token_blacklist（失効トークン）

**用途**: 既発行アクセストークン/リフレッシュを**個別失効**（全端末ログアウト等）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.token_blacklist (
  jti         text PRIMARY KEY,   -- JWT ID など一意識別子
  user_id     uuid,
  reason      text,
  created_at  timestamp
) WITH default_time_to_live = 604800; -- 7日
```
### 運用
- 検証時に `jti` を参照し NG で拒否
- 長期保持が不要なら TTL を短めに設定

---
[目次](../目次.md) > [データ定義 目次](目次.md) > token_blacklist