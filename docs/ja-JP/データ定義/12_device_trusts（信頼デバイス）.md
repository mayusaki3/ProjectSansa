[目次](../目次.md) > [データ定義 目次](目次.md) > device_trusts
# device_trusts（信頼デバイス）

**用途**: 信頼済みデバイス（MFA スキップ期間の付与など）

### 物理テーブル
```sql
CREATE TABLE IF NOT EXISTS sansa_auth.device_trusts (
  user_id     uuid,
  device_id   text,
  trusted_at  timestamp,
  expires_at  timestamp,
  PRIMARY KEY ((user_id), device_id)
);
```
### 運用
- 期限切れはアプリ側クリーン or TTL に置換可
- MFA 要求間隔（「前回から一定時間」）の判定に使用

---
[目次](../目次.md) > [データ定義 目次](目次.md) > device_trusts