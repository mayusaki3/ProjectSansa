[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > AuthService

# AuthService I/F 仕様

## 概要
ユーザー登録、ログイン、パスワードリセット等を統括するアプリケーションサービス。

## インターフェース
```java
public interface AuthService {
    AuthResult preRegister(PreRegisterCommand cmd);
    AuthResult verifyEmail(VerifyEmailCommand cmd);
    AuthResult register(RegisterCommand cmd);
    LoginResult login(LoginCommand cmd);
    void logout(LogoutCommand cmd);
}
```

## 戻り値
- AuthResult：登録・検証結果を表すドメインモデル
- LoginResult：ログイン成功時にトークン等を含む

## Command/Result 一覧
- Commands:
  - PreRegisterCommand { email:string, language:string }
  - VerifyEmailCommand { preRegId:string, code:string }
  - RegisterCommand { preRegId:string, passwordHash:string, displayName?:string }
  - LoginCommand { email:string, password:string, userAgent?:string, ipAddress?:string }
  - LogoutCommand { sessionId?:string, refreshToken?:string }

- Results:
  - AuthResult （登録系・検証系の汎用結果。詳細は「[DTO: AuthResult / LoginResult](DTO_01_AuthResult_LoginResult.md)」を参照）
  - LoginResult（ログイン成功時の結果。詳細は「[DTO: AuthResult / LoginResult](DTO_01_AuthResult_LoginResult.md)」を参照）

## REST DTO との対応（抜粋）
- POST /auth/login → LoginResult.tokens は REST の LoginResponse.tokens に対応
- POST /auth/logout → 戻り値なし（Controllerで `LogoutResponse{success}` を返す）※RESTの統一方針に合わせる

### 依存DTO
- [DTO: AuthResult / LoginResult](./DTO_01_AuthResult_LoginResult.md)
- LoginResult が参照する DTO:
  - LoginTokens（トークン）
  - SessionInfo（セッション）
  - UserSummary（ユーザー要約）
  - MfaInfo（MFA情報）

## 補足
- Controller層では、REST API入出力DTO ↔ Command/Result 変換を行う。
- 認証失敗時は AuthError（共通エラー定義）を返す。

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > AuthService
