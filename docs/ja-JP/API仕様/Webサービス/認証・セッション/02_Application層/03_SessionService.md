[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > SessionService

# SessionService I/F 仕様

## 概要
ログインセッションの作成／更新／破棄を統括するアプリケーションサービス。  
TokenFacade が発行する RefreshToken と sessionId を連動させて、REST レイヤに SessionInfo を提供する。

## 主な機能
### createSession(userId, userAgent, ipAddress) → sessionId
- Login 成功時に呼ばれる
- SessionStore に永続化

### getSession(sessionId) → SessionInfo
- REST /auth/me や /auth/session で利用

### invalidate(sessionId)
- ログアウト（単一端末）

### invalidateAll(userId)
- 全端末ログアウト

## 戻り値 DTO
- SessionInfo（sessionId, lastSeenAt, expireAt）
- AuthResult（必要時）

## 利用する Port
- SessionStore（Store）
- TokenFacade（セッション統合）

## 関連DTO
- [DTO_02_LoginTokens_SessionInfo_UserSummary.md](DTO_02_LoginTokens_SessionInfo_UserSummary.md)

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > SessionService
