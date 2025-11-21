[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: LoginRequest / LoginResponse

# DTO: LoginRequest / LoginResponse（Application層）

位置づけ:
AuthService.login の入出力DTO。Controller層でREST LoginRequest/LoginResponseへ変換。

## LoginRequest
- email : string（必須）
- password : string（必須）
- userAgent : string（任意）
- ipAddress : string（任意）

## LoginResponse
Application層では LoginResult（別DTO）を返す。  
Controller層でREST変換時は次に対応:

- result=SUCCESS → authenticated=true, tokens, user, session
- result=MFA_REQUIRED → authenticated=false, mfaRequired=true, mfa
- result=FAILED → HTTP 401/423

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: LoginRequest / LoginResponse
