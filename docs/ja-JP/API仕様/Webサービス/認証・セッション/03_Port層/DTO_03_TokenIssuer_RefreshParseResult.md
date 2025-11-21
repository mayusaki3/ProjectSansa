[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: TokenIssuer（RefreshParseResult）

# Port DTO: TokenIssuer（RefreshParseResult）

位置づけ:
JWT等のトークン頒布ポートが返す解析結果DTO。

## RefreshParseResult
- userId : string（必須）
- refreshId : string（必須）
- issuedAt : string(ISO8601)（任意）
- expiresAt : string(ISO8601)（任意）
- sessionId : string（任意）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: TokenIssuer（RefreshParseResult）
