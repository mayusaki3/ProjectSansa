[目次](../目次.md) > [データ定義 目次](目次.md) > sessions
# sessions（セッション）

**用途**: ログインセッション管理（多端末可）

### 物理テーブル
```sql
-- 主キーは session_id。個別無効化に最適
CREATE TABLE IF NOT EXISTS sansa_auth.sessions (
  session_id    uuid PRIMARY KEY,
  user_id       uuid,
  device_id     text,
  ip            text,
  ua            text,
  issued_at     timestamp,
  expires_at    timestamp,      -- 参照用（ソフト制御）
  refresh_token text            -- 失効時の照合用（必要なら暗号化）
);

-- ユーザー別にセッションを列挙/一括削除するためのビュー
CREATE TABLE IF NOT EXISTS sansa_auth.sessions_by_user (
  user_id       uuid,
  device_id     text,
  session_id    uuid,
  issued_at     timestamp,
  PRIMARY KEY ((user_id), device_id, session_id)
) WITH CLUSTERING ORDER BY (device_id ASC, session_id DESC);
```

### 操作指針
- 単一ログアウト：`DELETE FROM sessions WHERE session_id=?`
- 全端末ログアウト：`SELECT session_id FROM sessions_by_user WHERE user_id=?` → `sessions` を一括削除

---
[目次](../目次.md) > [データ定義 目次](目次.md) > sessions