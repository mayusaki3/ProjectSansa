[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 09. Mail メール処理


# 09. Mail メール処理（テンプレート & パーサ）

本書は **認証・セッション領域のメールテンプレートとパーサ**を対象とし、  
`.eml` → 構造化データ比較 による回帰テストを定義する。

---

## 1. 目的

- テンプレート差し込み値の正しさ
- 多言語出力（ja-JP / en-US）
- URL署名（verify / reset / revoke）および TTL メタ確認
- multipart（text/plain / text/html）

---

## 2. スコープ

対象テンプレート：

| テンプレートID | 用途 |
|---|---|
| email-verify | メールアドレス確認 |
| password-reset | パスワードリセット |
| session-revoked | セッション無効化通知 |

将来追加（参考）：  
`mfa-setup`, `mfa-recovery`, `webauthn-register-done`, `security-alert`

---

## 3. 前提条件

- `.eml` フィクスチャと期待 `.json` を `tests/fixtures_eml`, `tests/expected_json` に配置
- Python パーサによる構造化比較

---

## 4. テストケース（UT）

### M01:UT-09-001: email-verify 基本（ja-JP）
- `.eml` を JSON 化 → expected と一致
- 件名 / 差し込み名前 / verify URL / TTL / multipart

### M01:UT-09-002: email-verify 基本（en-US）
- ロケール切替
- phrase & subject & link ラベル差分を確認

### M01:UT-09-003: password-reset（ja-JP）
- reset URL と署名
- TTL メタ（ヘッダ or 本文タグ）

### M01:UT-09-004: password-reset（en-US）
- i18n 差分確認

### M01:UT-09-005: session-revoked（ja-JP）
- セッションIDまたは context 情報（存在する場合）
- revoke link

### M01:UT-09-006: session-revoked（en-US）

### M01:UT-09-007: multipart 構成
- text/plain + text/html の両方存在

### M01:UT-09-008: ヘッダ無し fallback
- `X-Template-Id`, `X-Locale`, `X-Token-Exp` が本文にある場合のパース

### M01:UT-09-009: URL 正規表現妥当性
- `/auth/verify`, `/auth/password/reset`, `/sessions/revoke`

## 5. テストケース2（UT）： テンプレート/i18n
### M01:UT-09-010: パスワードリセット通知（init）
- 言語別テンプレート、リンク/トークン埋め込み

### M01:UT-09-011: 送達失敗時のリトライ/記録

### M01:UT-09-012: レート制限と共通ヘッダ

---

## 6. 成否基準

- 期待 JSON と完全一致
- i18n 差分網羅
- TTL, URL, multipart が正しい

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 09. Mail メール処理