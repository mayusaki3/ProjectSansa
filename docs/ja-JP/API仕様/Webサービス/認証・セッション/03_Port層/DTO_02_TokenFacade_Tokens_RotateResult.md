[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: TokenFacade（Tokens / RotateResult）

# Port DTO: TokenFacade（Tokens / RotateResult）

位置づけ:
トークン発行ポート(TokenFacade)が返す結果DTO。

## Tokens
- accessToken : string（必須）
- refreshToken : string（必須）
- sessionId : string（必須）
- expiresIn : number（任意）
- refreshExpiresAt : string(ISO8601)（任意）

## RotateResult
- newRefreshToken : string（必須）
- rotatedAt : string(ISO8601)（必須）
- refreshExpiresAt : string(ISO8601)（任意）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: TokenFacade（Tokens / RotateResult）
