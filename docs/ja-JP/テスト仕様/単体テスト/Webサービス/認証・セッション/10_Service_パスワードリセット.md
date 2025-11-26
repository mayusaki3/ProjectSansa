[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 10. Service パスワードリセット

# 10. Service パスワードリセット

## 前提
- API仕様: `06_パスワードリセット.md` に準拠
  - 想定エンドポイント（例）:
    - `POST /auth/password/reset/init`（メール送信）
    - `POST /auth/password/reset/verify`（トークン/コード検証）
    - `POST /auth/password/reset/complete`（新パスワード確定）
- セキュリティ方針:
  - アカウント存在有無の秘匿（initは常に200系）
  - トークンTTL/回数制限、乱数強度、監査ログ
  - 完了時に **全端末無効化（token_version++）** を行うこと（= logout_all と等価の効果）
    - 既存挙動の参照: ログアウト・全端末無効化の期待と検証観点（M01:UT-04）、token_version の検証（M01:UT-05）。:contentReference[oaicite:2]{index=2} :contentReference[oaicite:3]{index=3}

## テスト一覧

### M01:UT-10-001 init: 既存メール → 200 + メール送信キュー
- Given: 登録メール
- When: `POST /auth/password/reset/init`
- Then: 200
- And: メール送信要求が生成（テンプレート/i18n差し込みOK）
- And: 監査ログ（種別、対象ユーザー、IP/UA）

### M01:UT-10-002 init: 未登録メール → 200（秘匿）
- Given: 未登録メール
- When/Then: 200、メール送信は**しない**
- And: 同等の応答時間レンジを維持（タイミング攻撃対策）

### M01:UT-10-003 verify: 有効トークン → 200 + 継続可
- Given: 有効な検証トークン/コード
- When: `POST /auth/password/reset/verify`
- Then: 200（以降 complete を許可する一時状態）

### M01:UT-10-004 complete: 強度OK → 200 + 全端末無効化(token_version++)
- Given: 検証済み状態 + 強度ポリシーに適合する新パスワード
- When: `POST /auth/password/reset/complete`
- Then: 200、サーバ側でパスワード更新
- And: **全端末無効化（logout_all 相当）**
  - 旧AT/RTは401、refresh不可、`tv`は旧+1で新発行される（M01:UT-04/05に準拠）。:contentReference[oaicite:4]{index=4} :contentReference[oaicite:5]{index=5}

### M01:UT-10-005 complete: パスワード強度NG → 400
- Given: 弱い/ポリシー違反の新パスワード
- Then: 400（エラーコード/i18nメッセージ検証）

### M01:UT-10-006 verify: 期限切れ/改竄トークン → 400/401
- Given: TTL切れ/署名不正/改竄
- Then: 400 or 401（仕様のエラー型に合わせて）

### M01:UT-10-007 トークン使い回し → 初回のみ成功、以降は無効
- Given: 同一トークン二度使用
- Then: 1回目成功、2回目は 400/409/410 のいずれか（仕様に合わせる）

### M01:UT-10-008 レート制限（init / verify / complete）
- Given: 連続試行
- Then: 429（X-RateLimit ヘッダ/i18nメッセージ）

### M01:UT-10-009 メールテンプレート/i18n
- Given: 言語=ja-JP / en-US
- Then: 件名/本文のプレースホルダ展開、禁則処理（URL, トークン埋め込み）

### M01:UT-10-010 監査ログ/通知
- Then: 重要イベント（init/complete）は必ず監査ログ、必要なら管理通知

---

## 実装ヒント（非テスト）
- complete 成功時は **必ず** token_version をインクリメントする実装へ（= logout_all と同一の失効効果）。既存の検証観点に統合可能。:contentReference[oaicite:6]{index=6} :contentReference[oaicite:7]{index=7}

---
[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 10. Service パスワードリセット
