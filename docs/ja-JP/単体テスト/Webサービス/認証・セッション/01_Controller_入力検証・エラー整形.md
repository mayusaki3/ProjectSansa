[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形

# 01. Controller 入力検証・エラー整形

対象: `/auth/pre-register`, `/auth/verify-email`, `/auth/register`, `/auth/login`, `/auth/session`, `/sessions/*`, `/auth/logout(_all)`, `/webauthn/*`, `/auth/mfa/*`  
共通期待: `application/problem+json` でのエラー整形、`Content-Language` の返却。

## UT-01-001: pre-register の email 空文字 -> 400
- Given: `POST /auth/pre-register` body `{ "email": "" }`
- When: 実行
- Then:
  - 400、`Content-Type=application/problem+json`
  - `type` が `.../invalid-argument`
  - `errors[0].field=="email"`, `errors[0].reason` に `blank` など

## UT-01-002: pre-register の language フォーマット不正 -> 400
- body `{ "email": "a@b.com", "language": "jp_JP" }`
- Then: 400、`errors[0].field=="language"`

## UT-01-003: verify-email の code 桁不足 -> 400
- body `{ "email": "a@b.com", "code": "123" }`（min 6）
- Then: 400、`type=.../invalid-code`

## UT-01-004: register の preRegId 欠落 -> 400
- body `{ "accountId":"alice" }`
- Then: 400、`errors[0].field=="preRegId"`

## UT-01-005: login の identifier 未指定 -> 400
- body `{ "password":"pass" }`
- Then: 400、`errors[0].field=="identifier"`

## UT-01-006: login 失敗 -> 401
- body `{ "identifier":"alice", "password":"wrong" }`
- Then: 401、`type=.../invalid-credentials`

## UT-01-007: i18n ヘッダ反映
- Given: `Accept-Language: ja-JP` で `/auth/login`
- Then: 成功・失敗問わず `Content-Language: ja-JP`（fallbackは仕様準拠）

## UT-01-008: WebAuthn assertion の必須フィールド欠落 -> 400
- body `{ "id": "", "clientDataJSON": "", "authenticatorData":"", "signature":"" }`
- Then: 400、`invalid-argument`

## UT-01-009: セッション個別失効: ID 不正 -> 404（Controller）
- `DELETE /sessions/not-found`
- Then: 404、`type=.../session_not_found`

## UT-01-010: レート制限 -> 429
- pre-register を短時間に連打
- Then: 429、`RateLimit-Remaining` などのヘッダ/`Retry-After`

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 01. Controller 入力検証・エラー整形