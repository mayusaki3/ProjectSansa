[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 05. Util トークン署名・TTL・検証

# 05. Util トークン署名・TTL・検証

## UT-05-001: 署名検証成功（正しい鍵・アルゴリズム）
- Given: 正しい `kid`/鍵束
- Then: AT/RT の署名検証 OK

## UT-05-002: 署名検証失敗（鍵不一致）
- Then: 401

## UT-05-003: TTL 満了で無効
- Given: `exp` を過去に
- Then: 検証で 401（`type=.../token_expired`）

## UT-05-004: token_version 埋め込み
- Given: `tv=10` で発行
- When: `logout_all` 実行（`tv=11` に更新）
- Then: 旧トークン検証時に **不一致で 401**

### UT-05-005: TokenRefreshResponse の `tv` 整合
- Given: 現在の `token_version=K`
- When: 正常に `/auth/token/refresh`
- Then: レスポンス `tv==K`

### UT-05-006: `/token/reused` 検知後の旧トークン無効化
- Given: 再利用検知で `tv` が **K→K+1**
- Then:
  - 旧AT/旧RT 検証は 401（`/token/invalid` or `/token/reused`）
  - 新規発行トークンには `tv=K+1` が埋め込まれる

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 05. Util トークン署名・TTL・検証