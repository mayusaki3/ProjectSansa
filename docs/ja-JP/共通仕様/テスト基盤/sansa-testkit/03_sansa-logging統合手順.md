[目次](../../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-logging 統合手順

# sansa-logging 統合手順

## 1. 位置づけ

`sansa-logging` は Project Sansa の **共通ロギング基盤**であり、  
業務ロジックとは独立したインフラ層モジュールである。

---

## 2. 現状のテスト方針（暫定）

- テストは JUnit により実行
- 各テストケースの冒頭で以下を出力する

```java
System.out.println("[TESTCASE] LOGGING-COMMON-TC-001");
```

- テスト番号はコメントおよび標準出力で明示

---

## 3. 将来方針（testkit 統合）

`sansa-logging` は以下の段階で `sansa-testkit` に統合する。

1. 既存テストを **1テスト1ケース**構造に整理
2. `System.out.println` を廃止
3. `TestPrinter` による出力へ移行
4. テスト仕様書と 1:1 対応を保証

---

## 4. 注意事項

- logging のテストは **仕様検証用**であり、  
  カバレッジ 100% を目的とした実装依存テストとは区別する
- testkit 移行後も、テスト番号は **LOGGING-COMMON-*** を維持する

---
[目次](../../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-logging 統合手順
