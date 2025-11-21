[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: LoginTokens / SessionInfo / UserSummary

# DTO: LoginTokens / SessionInfo / UserSummary（Application層）

位置づけ:
Application層サービスの戻り値（例: LoginResult）で参照される共通DTO。

## LoginTokens
- フィールド:
  - accessToken : string（必須） … アクセストークン（JWT想定）
  - refreshToken : string（必須） … リフレッシュトークン
  - expiresIn : number（任意） … アクセストークン有効秒数
  - tokenType : string（任意） … 例 "Bearer"

## SessionInfo
- フィールド:
  - sessionId : string（必須） … セッション識別子
  - createdAt : string(ISO8601)（必須） … 作成時刻（UTC）
  - refreshExpiresAt : string(ISO8601)（任意） … RT失効時刻
  - ipAddress : string（任意） … 発行元IP
  - userAgent : string（任意） … 発行元UA

## UserSummary
- フィールド:
  - userId : string（必須） … ユーザーID
  - email : string（必須） … メールアドレス
  - displayName : string（任意） … 表示名
  - language : string（任意） … 表示言語
  - admin : boolean（任意） … 管理者フラグ

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: LoginTokens / SessionInfo / UserSummary
