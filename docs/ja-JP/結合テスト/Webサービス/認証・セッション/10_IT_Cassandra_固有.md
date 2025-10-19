[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 10. IT Cassandra 固有

# 10. IT Cassandra 固有

本書は **Cassandra プロファイル**（`-Dspring.profiles.active=cassandra`）で実行する結合テストのうち、
インメモリ実装では再現できない **Cassandra 固有**の観点・手順・期待結果を定義します。  

---

## 1. 目的とスコープ

- **目的**: 本番/検証で Cassandra を用いる際に、スキーマ/クエリ/TTL/一貫性/可用性に関わる振る舞いを担保する。
- **対象**: `sansa-auth` の **永続化層**に依存した機能（セッション、トークン、レート制限、MFA、WebAuthn 資格情報、プリレジストコード等）。
- **非対象**: コントローラのフォーマット検証など **DB 非依存**のケースは従来 IT に委譲。

---

## 2. 事前準備

### 2.1 環境
- Apache Cassandra 4.x（クラスタ 1～3 ノードで可）
- Java 21, Maven 3.9+
- Spring Boot 3.3+

### 2.2 プロファイル
- `application-cassandra.yml` を参照（接続先 / Keyspace / Consistency / TTL）
- 実行例：
  ```bash
  mvn -q -Dspring.profiles.active=cassandra verify
  ```

### 2.3 スキーマ準備
- データ定義: [データ定義](../../../データ定義/目次.md)（テーブル仕様書）
- CQL 配置方針（例）:
  ```text
  /cql
    ├─ 000_init_keyspace.cql
    ├─ 010_tables.cql
    ├─ 020_indexes.cql
    └─ 900_drop_all.cql
  ```
- マイグレーションツールは将来 `cql` 直下に配置（`README.md` で手順を定義）。

---

## 3. Cassandra 固有観点（実装差分）

| 観点 | 期待 / 差分 | 備考 |
|---|---|---|
| 最終一貫性 | 読取直後の可視性（**read-your-writes**）は CL に依存。 | 後続テストは **適切な CL** または **短い待機**で安定化。 |
| TTL | Token/コード/レートバケット等は **テーブル側 TTL** または **col TTL** を使用。 | 期限切れの削除タイミングは非同期（Compaction）。 |
| パーティション設計 | セッション/レート制限は **ホットパーティション**回避。 | パーティションキーの分散を確認。 |
| アイテム一意性 | 予約 ID/チャレンジの **軽量トランザクション( LWT )** での重複防止。 | `IF NOT EXISTS` / `APPLY BATCH` 等 |
| 二重消費 | コードの **消費フラグ**更新は LWT で同時更新に耐性。 | 冪等性の確認を追加。 |
| 参照整合 | ユーザ削除/ログアウト時の関連データ消し漏れを **整合性クエリ**で検証。 | 二次インデックス/マテリアライズドビュー利用時は注意。 |

---

## 4. テスト構成

- 既存 IT: `sansa-auth/src/test/java/com/sansa/auth/it`
- **Cassandra 固有ケース**は同パッケージ配下に `*CassandraIT` として配置、`@ActiveProfiles("cassandra")`。
- **テスト番号体系**: `IT-CA-xxx`（CA = Cassandra）

---

## 5. テストケース（番号付き）

### 5.1 Keyspace/Schema 基本

- **IT-CA-001** Keyspace 存在 & バージョン合致（起動時に例外なし）  
- **IT-CA-002** 主要テーブル存在（列/型/TTL 設定が仕様通り）  
- **IT-CA-003** 二次インデックス/マテビュー定義の有無確認（必要なら）  

### 5.2 TTL と期限管理

- **IT-CA-010** Pre-register コード TTL 経過で 404（**自然消滅**確認）  
- **IT-CA-011** RefreshToken TTL 経過で 401 `/token/expired`  
- **IT-CA-012** レートリミットバケット TTL 経過で カウント自動リセット  
- **IT-CA-013** TTL 直後アクセスの境界（±数秒）で期待エラー/成功を確認  

### 5.3 LWT（軽量トランザクション）

- **IT-CA-020** Pre-register コードの **一度きり消費**：並行消費 2 リクエスト → 片方のみ成功  
- **IT-CA-021** セッション作成の **重複防止**（同一キーで LWT）  
- **IT-CA-022** MFA リカバリコード **一括発行**後の **単回消費**（並行 2 リクエストで片方 409/400）  

### 5.4 一貫性レベルと可視性

- **IT-CA-030** `CL=LOCAL_QUORUM` で **read-your-writes** 成立  
- **IT-CA-031** `CL=ONE` で直後読取のばらつき → **リトライ/待機**戦略適用で安定化  
- **IT-CA-032** 書込み成功直後の別ノード読取での可視性差を計測（ログ/メトリクス）  

### 5.5 パーティション/スキャン特性

- **IT-CA-040** セッション一覧の **分散**確認（多数ユーザ/多数セッションを投入）  
- **IT-CA-041** レート制限キーの **パーティションスパイク**が無い（ホットキー回避）  
- **IT-CA-042** ページング取得（`pagingState`）の安定性（連続ページで重複/欠落なし）  

### 5.6 冪等・再送・同時更新

- **IT-CA-050** `logout_all` 多重実行の冪等性（**token_version++** は一度のみ増加）  
- **IT-CA-051** RefreshToken **再利用検知**：並行で同 RT を使う → 片方のみ成功、他方は `/token/reused`  
- **IT-CA-052** Email OTP 送信の **レース**（同時送信で最新だけ有効）  

### 5.7 WebAuthn / MFA 資格情報

- **IT-CA-060** WebAuthn Credential 保存→参照→削除（Cassandra 実ストア）  
- **IT-CA-061** signCount **インクリメント**の整合（直列/並列で期待値）  
- **IT-CA-062** TOTP Secret の保存/有効化/検証（DB 経由で一貫）  

### 5.8 監査・整合クリーンアップ

- **IT-CA-070** 監査ログ（Cassandra ストア採用時）の書込み・読取り  
- **IT-CA-071** ユーザ削除時のぶら下がりデータ無し（セッション/資格情報/コードの参照整合）  

---

## 6. 実行手順

1. **CQL 適用**（`/cql` 直下の初期化スクリプト）  
2. **アプリ起動** or **IT 実行**:  
   ```bash
   mvn -q -Dspring.profiles.active=cassandra verify
   ```
3. 失敗時は **Consistency / Timeout / Retry ポリシー**を確認。

---

## 7. 期待結果サマリ（抜粋）

- TTL 期限切れは **自然消滅**または **不可視**になり、API は 400/401/404 を返す。  
- LWT を用いた **一度きり消費/重複防止**は並行要求下でも整合。  
- ページングと分散は **欠落なし＆ホットパーティション回避**。  
- `logout_all` は冪等、RefreshToken 再利用検知は **確実に 401**。  

---

## 8. トラブルシュート

| 症状 | 代表原因 | 対処 |
|---|---|---|
| 直後に読めない | CL=ONE での伝播遅延 | CL を引き上げ、短い待機 or リトライ |
| TTL 超過なのに読める | Tombstone/Compaction 遅延 | アプリ側で **期限判定**を併用 |
| 並行で二重成功 | LWT 未適用 | `IF NOT EXISTS` / 条件更新へ修正 |
| ページング欠落/重複 | ソート/パーティション設計不備 | パーティションキー/クラスタリングキー見直し |

---

## 9. 付録

### 9.1 推奨 CQL 断片（例）
```sql
-- TTL 付きトークンテーブル例
CREATE TABLE IF NOT EXISTS auth_tokens (
  user_id text,
  token_id text,
  issued_at timestamp,
  expires_at timestamp,
  PRIMARY KEY ((user_id), issued_at, token_id)
) WITH default_time_to_live = 0;

-- 消費の一意制約（LWT）
INSERT INTO recovery_codes(user_id, code, issued_at) VALUES(?,?,?)
IF NOT EXISTS;
```

### 9.2 テスト命名規約
- `IT-CA-<3桁>` + 具体名（例: `IT-CA-011_refresh_ttl_expired`）

---
[目次](../../../目次.md) > 結合テスト > Webサービス > [認証・セッション 結合テスト 目次](目次.md) > 10. IT Cassandra 固有
