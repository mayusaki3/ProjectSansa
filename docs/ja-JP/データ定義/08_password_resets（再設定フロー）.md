[目次](../目次.md) > [データ定義 目次](目次.md) > password_resets
# password_resets（再設定フロー）

**用途**: パスワード再設定フロー（メール経由でワンタイムリンク/コード）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.password_resets (
  request_id   uuid PRIMARY KEY,
  user_id      uuid,
  email        text,
  code         text,        -- またはトークン（ハッシュ保存推奨）
  created_at   timestamp
) WITH default_time_to_live = 1800; -- 30分
```
### ポイント
- コード/トークンは**ハッシュ化**して保存し漏洩耐性を確保
- 再設定完了後は関連リクエストを即時削除

---
[目次](../目次.md) > [データ定義 目次](目次.md) > password_resets