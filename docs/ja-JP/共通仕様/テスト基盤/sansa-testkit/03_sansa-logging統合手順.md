[目次](../../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-logging 統合手順

# sansa-logging 統合手順

## 1. 目的

本書は、`sansa-logging` モジュールに  
**sansa-testkit のテスト番号出力・テスト補助機構を統合する手順**を定義する。

本統合の目的は以下の通り。

- テスト実行ログから **テスト番号（TC）を確実に確認可能**にする
- テスト仕様書と実行結果を **機械的に突合可能**にする
- テスト番号出力方式を **testkit に集約**し、属人的な実装を排除する
- 将来の CI / 品質監査に耐えうる **一貫したテスト証跡**を確保する

---

## 2. 前提条件

- プロジェクト：Project Sansa
- 対象モジュール：`sansa-logging`
- テストフレームワーク：JUnit 5
- ビルドツール：Maven
- Java：17 以上

---

## 3. テスト方針（選択肢A：推奨）

`sansa-logging` におけるテスト方針は以下を **正式ルール**とする。

### 3.1 テスト構造

- **1 テストメソッド = 1 テストケース**
- 各テストケースは **単一のテスト番号（TC）**を持つ
- テスト番号は **コード上に明示的に残す**
- テスト番号の出力は **sansa-testkit 経由で行う**

### 3.2 テスト番号形式

テスト番号は以下の形式を採用する。

```
LOGGING-COMMON-TC-XXX
```

- `XXX` は 3 桁連番
- テスト仕様書・テストコード・実行ログで **完全一致**していること

---

## 4. Maven 依存関係の追加

### 4.1 sansa-logging/pom.xml

`sansa-testkit` を **test scope** で追加する。

```
<dependency>
    <groupId>com.sansa</groupId>
    <artifactId>sansa-testkit</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
```

※ `sansa-logging` の main ソースコードから  
　`sansa-testkit` を参照してはならない。

---

## 5. テストコード実装ルール

### 5.1 禁止事項（重要）

以下の実装は **禁止**とする。

- `System.out.println("[TESTCASE] ...")` の直接使用
- `@DisplayName` にのみテスト番号を記載する運用
- 1 テストメソッドで複数テスト番号を暗黙的に扱う構造
- テスト番号が仕様書と一致しない状態でのコミット

---

### 5.2 推奨実装（sansa-testkit 使用）

#### 5.2.1 テスト番号出力

各テストメソッドの冒頭で  
**sansa-testkit のテスト番号出力機構**を使用する。

```
@Test
void shouldCreateAuditRecordWithRequiredFields() {
    TestPrinter printer = TestPrinter.forSingleCase(
        "LOGGING-COMMON",
        "TC-001"
    );

    printer.printStart();

    // テスト本体
}
```

- テスト番号は **必ず標準出力に出ること**
- Surefire / CI ログから grep 可能であることを前提とする

---

### 5.3 コメント記載ルール（必須）

各テストメソッドには、以下形式のコメントを **必ず付与する**。

```
/**
 * LOGGING-COMMON-TC-001
 *
 * AuditRecord が必須項目を保持して生成されることを確認する。
 */
@Test
void shouldCreateAuditRecordWithRequiredFields() {
    ...
}
```

- コメント内の TC：**仕様書との対応付け**
- 実行ログ上の TC：**実行証跡**

両者が揃って初めて「追跡可能なテスト」とみなす。

---

## 6. 既存テストの移行指針

### 6.1 対象クラス

- `AuditRecordTest`
- `SimpleAuditLoggerTest`

### 6.2 移行内容

| 項目 | 旧実装 | 新実装 |
|----|----|----|
| テスト番号出力 | println | sansa-testkit |
| テスト構造 | 任意 | 1メソッド1TC |
| @DisplayName | 使用 | 使用しない |
| TC 管理 | 暗黙 | 明示 |

---

## 7. 実行確認

### 7.1 実行コマンド

```
mvn --% -pl sansa-logging -DskipTests=false clean test
```

### 7.2 期待される出力例

```
[TESTCASE] LOGGING-COMMON-TC-001
[TESTCASE] LOGGING-COMMON-TC-002
[TESTCASE] LOGGING-COMMON-TC-101
```

- Surefire のテスト一覧とは独立して出力される
- CI ログからテスト番号単位で追跡可能

---

## 8. 将来拡張方針

- `sansa-auth` 等、他モジュールも同一方針で統合
- sansa-testkit 側で以下の拡張を検討可能
  - テスト開始／終了フック
  - SUCCESS / FAIL の統一出力
  - XML / JSON 形式でのテスト結果エクスポート

---

## 9. 本書の位置付け

本書は **sansa-testkit を用いた最初の実運用統合手順書**であり、  
以降の `03_sansa-xxx統合手順.md` 作成時の **基準ドキュメント**とする。

---
[目次](../../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-logging 統合手順
