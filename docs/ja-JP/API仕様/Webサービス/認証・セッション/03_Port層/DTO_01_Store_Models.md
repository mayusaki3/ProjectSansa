[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: Store Models

# Port DTO: Store Models

位置づけ:
Storeポートで使用する永続化データ構造。

## User
- userId : string（必須）
- email : string（必須）
- displayName : string（任意）
- language : string（任意）
- admin : boolean（任意）
- passwordHash : string（任意）

## PreRegistration
- preRegId : string（必須）
- email : string（必須）
- language : string（任意）
- expiresAt : string(ISO8601)（必須）
- verified : boolean（任意）

## Session
- sessionId : string（必須）
- userId : string（必須）
- createdAt : string(ISO8601)（必須）
- refreshId : string（任意）
- refreshExpiresAt : string(ISO8601)（任意）
- ipAddress : string（任意）
- userAgent : string（任意）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > Port DTO: Store Models
