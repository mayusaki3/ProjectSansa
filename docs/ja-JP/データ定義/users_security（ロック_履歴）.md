[目次](../目次.md) > [データ定義 目次](目次.md) > users_security
# users_security（ロック/履歴）

**用途**: 認証の運用状態（ロック/解錠・PW履歴N件）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.users_security (
  user_id     uuid PRIMARY KEY,
  locked      boolean,       -- ロック状態
  locked_at   timestamp,
  reason      text,
  updated_at  timestamp,
  pw_hist     list<text>     -- 過去N件のパスワードハッシュ（履歴禁止用）
);
```
### ポイント
- 履歴上限はアプリ側で管理（例：直近5件）
- ロック解除は監査 `auth_audit` と合わせて記録

---
[目次](../目次.md) > [データ定義 目次](目次.md) > users_security