[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 06. IT トークン（TTL・リフレッシュ・改竄・tv）

# 06. トークン（TTL / リフレッシュ / 改竄 / tv）

### IT-06-001: AT の TTL 満了
- Given: `exp` の短い発行 or 時刻進め
- Then: 保護APIへ 401（`*token_expired`）

### IT-06-002: RT でリフレッシュ成功
- When: `POST /auth/token/refresh` `{ refreshToken }`
- Then:
  - 200、`tokens.accessToken`/`tokens.refreshToken` を返却（**新RT**）
  - `tv` は現行値（logout_all 未実行なら増えない）
  - 旧RT を用いた再リフレッシュ試行は 401 `/token/reused`

### IT-06-003: RT 改竄
- Then: 401（署名検証失敗 or 形式不正）

### IT-06-004: logout_all 後の RT 使用
- Given: `POST /auth/logout_all` 実行で `tv++`
- When: `POST /auth/token/refresh`
- Then: 401、`type="https://errors.sansa.dev/token/reused"`

### IT-06-005: kid/鍵束切替
- Given: `kid` ローテーション
- Then: 旧トークンは有効期間内OK、新発行は新`kid`

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 06. IT トークン（TTL・リフレッシュ・改竄・tv）