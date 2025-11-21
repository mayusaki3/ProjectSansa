[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: Auth系 Requests / Responses

# DTO: Auth系 Requests/Responses（Application層）

位置づけ:
AuthService の Command/Result と 1対1対応する Application層 DTO。

## PreRegisterRequest / Response
- Request:
  - email : string（必須）
  - language : string（必須）
- Response:
  - AuthResult（別定義）

## VerifyEmailRequest / Response
- Request:
  - preRegId : string（必須）
  - code : string（必須）
- Response:
  - AuthResult

## RegisterRequest / Response
- Request:
  - preRegId : string（必須）
  - passwordHash : string（必須）
  - displayName : string（任意）
- Response:
  - AuthResult

## LogoutRequest / Response
- Request:
  - sessionId : string（任意）
  - refreshToken : string（任意）
- Response:
  - なし（Controller層で {success} を返却）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: Auth系 Requests / Responses
