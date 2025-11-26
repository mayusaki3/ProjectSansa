[目次](../../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 11. IT Mail メール処理E2E

# 11. IT Mail メール処理E2E（SMTP / URLフロー）

本書は **実メール送信→取得→リンク有効性→トークン評価** を検証する。

---

## 1. 目的

- 認証フローでメールが正しく送信・取得できること
- verify / reset / revoke のリンクが動作すること
- トークンが一度きりで失効すること
- TTL に応じて期限切れ処理が行われること
- Accept-Language に応じてテンプレートが切り替わること

---

## 2. スコープ

| 対象 | 内容 |
|---|---|
| email-verify | 登録確認 |
| password-reset | パスワード再設定 |
| session-revoked | セッション無効化通知 |

SMTP: MailHog  
受信: MailHog API

アプリ起動プロファイル: `inmem`（Cassandra 版は別途 IT 10 で扱う）

---

## 3. 前提条件

- アプリケーションが起動済み
- MailHog が起動済み（SMTP: 1025 / WebUI: 8025）
- Test対象ユーザはテストケース内で作成・破棄
- トークンはテスト専用キーで署名されること

補足: 実行手順と docker 起動方法は  
**00_IT_方針と前提_テスト方法.md に集約**する（本章では記載しない）

---

## 4. テストケース（Given / When / Then）

### M01:IT-11-001: register → email-verify → verify 成功
- Given: 新規ユーザ登録
- When: MailHogから email-verify URL を取得しアクセス
- Then: 200 / セッション確立 / ログイン状態

### M01:IT-11-002: verify トークン一度きり
- Given: 上記 verified
- When: 同URL再アクセス
- Then: 401 or 410

### M01:IT-11-003: verify トークン期限
- Given: TTL 経過
- When: URLアクセス
- Then: 410（token expired）

### M01:IT-11-004: forgot → reset → reset 成功
- Given: /auth/password/forgot
- When: reset URLから新PW設定
- Then: 200 / 新PWでログイン可能

### M01:IT-11-005: reset トークン一度きり
- Given: reset 済み
- When: 同URL再アクセス
- Then: 401

### M01:IT-11-006: reset トークン期限
- Given: TTL 経過
- When: reset URL アクセス
- Then: 410

### M01:IT-11-007: session revoke → 通知メール
- Given: 複数セッション発行
- When: /sessions/revoke_all
- Then: revoke メール送信 / 旧tokenで 401

### M01:IT-11-008: 署名アルゴリズム・署名検証
- Given: JWT / HMAC / RSA のいずれか（プロファイル切替）
- When: MailHog から token 抽出
- Then: 署名構造一致 / テスト用鍵で検証可能

### M01:IT-11-009: exp claim とヘッダ一致
- Given: メール生成
- When: X-Token-Exp / JWT exp 比較
- Then: 一致

### M01:IT-11-010: reset.init通知メール送信
- When: 正常メール送信リクエスト
- Then: MailHog に1通、件名/本文テンプレ一致

### M01:IT-11-011: reset.init RateLimit
- When: 同一宛先に連続リクエスト
- Then: 429 + Retry-After

### M01:IT-11-012: reset.complete 完了通知
- When: パスワード再設定完了
- Then: 完了報告メールが送信される

---

## 5. 成否基準

| 観点 | OK条件 |
|---|---|
メール送信 | MailHogに expected 件数が到達 |
i18n | Accept-Language に応じて件名/本文が切替 |
URL動作 | verify/reset/revoke 全て成功 |
ワンタイム性 | 同URL再利用で 401/410 |
TTL | exp 超過で 410 |
署名 | JWT構造＆テスト鍵で検証成功 |

---
[目次](../../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 11. IT Mail メール処理E2E
