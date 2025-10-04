[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 05. トークン（TTL・失効・リフレッシュ）
# 05. トークン（TTL・失効・リフレッシュ）

**観点**: トークン（署名・TTL・失効・リフレッシュ）

### テストケース
- **[IT-05-001] access TTL 満了で失効**  
  **Pre**: 短TTL（例：30秒）設定  
  **Step**: 期限経過後 `/auth/me`  
  **Expect**: `401`, `AUTH.TOKEN_EXPIRED`

- **[IT-05-002] refresh による再発行**  
  **Step**: `POST /auth/token/refresh` with `refreshToken`  
  **Expect**: `200`、新しい `accessToken`

- **[IT-05-003] 改竄トークン拒否**  
  **Expect**: `401`, `AUTH.TOKEN_INVALID`

- **[IT-05-004] logout-all 後の refresh も拒否**  
  **Expect**: `401`, `AUTH.REFRESH_REVOKED`

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 05. トークン（TTL・失効・リフレッシュ）