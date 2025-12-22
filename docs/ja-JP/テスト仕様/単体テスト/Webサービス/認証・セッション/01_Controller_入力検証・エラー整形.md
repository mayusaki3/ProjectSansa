[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形

# 01. Controller 入力検証・エラー整形

対象: `/auth/pre-register`, `/auth/verify-email`, `/auth/register`, `/auth/login`, `/auth/session`, `/sessions/*`, `/auth/logout(_all)`, `/webauthn/*`, `/auth/mfa/*`  
共通期待: `application/problem+json` でのエラー整形、`Content-Language` の返却。

## 監査ログに関する共通ルール

本ドキュメントで扱う Controller 入力検証エラー（HTTP 400 系）は、
すべて監査ログの対象外とする。

理由：
- ユーザー操作の失敗ではなく、入力不備であるため
- 監査ログは「意味のある操作結果」に限定するため

例外：
- 認証失敗（401 invalid-credentials）は監査対象とする

## AUTH-CTRL-UT-001: pre-register の email 空文字 -> 400
- Given: `POST /auth/pre-register` body `{ "email": "" }`
- When: 実行
- Then:
  - 400、`Content-Type=application/problem+json`
  - `type` が `.../invalid-argument`
  - `errors[0].field=="email"`, `errors[0].reason` に `blank` など

## AUTH-CTRL-UT-002: pre-register の language フォーマット不正 -> 400
- body `{ "email": "a@b.com", "language": "jp_JP" }`
- Then: 400、`errors[0].field=="language"`

## AUTH-CTRL-UT-003: verify-email の code 桁不足 -> 400
- body `{ "email": "a@b.com", "code": "123" }`（min 6）
- Then: 400、`type=.../invalid-argument`
- `errors[0].field=="code"` を確認

## AUTH-CTRL-UT-004: register の preRegId 欠落 -> 400
- body `{ "accountId":"alice" }`
- Then: 400、`errors[0].field=="preRegId"`

## AUTH-CTRL-UT-005: login の identifier 未指定 -> 400
- body `{ "password":"pass" }`
- Then: 400、`errors[0].field=="identifier"`

## AUTH-CTRL-UT-006: login 失敗 -> 401
- body `{ "identifier":"alice", "password":"wrong" }`
- Then: 401、`type=.../invalid-credentials`
- 監査ログ：対象  
  （eventType = LOGIN_FAILED）

## AUTH-CTRL-UT-007: i18n ヘッダ反映
- Given: `Accept-Language: ja-JP` で `/auth/login`
- Then: 成功・失敗問わず `Content-Language: ja-JP`（fallbackは仕様準拠）

## AUTH-CTRL-UT-008: WebAuthn assertion の必須フィールド欠落 -> 400
- body `{ "id": "", "clientDataJSON": "", "authenticatorData":"", "signature":"" }`
- Then: 400、`invalid-argument`

## AUTH-CTRL-UT-009: セッション個別失効: ID 不正 -> 404（Controller）
- `DELETE /sessions/not-found`
- Then: 404、`type=.../session_not_found`

## AUTH-CTRL-UT-010: レート制限 -> 429
- pre-register を短時間に連打
- Then: 429、`RateLimit-Remaining` などのヘッダ/`Retry-After`
- 監査ログ：対象外  
  （レート制限は原則監査対象外）

---
[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形