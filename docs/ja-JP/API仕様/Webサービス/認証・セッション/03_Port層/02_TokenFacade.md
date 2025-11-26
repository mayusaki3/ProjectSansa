[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > TokenFacade

# TokenFacade I/F 仕様

## 概要
アクセス／リフレッシュトークンの発行・検証・ローテーションを担う暗号化/署名コンポーネント。  
JWT などの具体実装はインフラ側にあり、Domain/Application はこの Port を通じて利用する。

## 役割
- LoginResult を生成する際の JWT/RefreshToken 発行
- リフレッシュトークンの検証（署名・失効・期限）
- トークンローテーション（ワンタイム／連続利用防止）
- セッションIDとの統合

## 提供I/F
### issueTokens(userId, sessionId, amr[]) → LoginTokens
- 新規ログイン時に Access/Refresh を発行

### rotate(refreshToken) → RotateResult
- RefreshToken を検証し、新しいペアを発行
- ローテーションルール（1回限り/Reuse検知）に合わせて発行

### parseRefresh(refreshToken) → RefreshParseResult
- 署名検証後、userId/sessionId を抽出
- 無効 or 期限切れ時はエラー DTO を返す

## 関連DTO
- [DTO_02_TokenFacade_Tokens_RotateResult.md](DTO_02_TokenFacade_Tokens_RotateResult.md)  
- [DTO_03_TokenIssuer_RefreshParseResult.md](DTO_03_TokenIssuer_RefreshParseResult.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > TokenFacade
