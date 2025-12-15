[目次](../../目次.md) > テスト仕様 > 共通基盤 > テスト基盤_testkit_テスト仕様

# テスト基盤（sansa-testkit）テスト仕様（自己検証UT）

## 1. 目的

本書は Project Sansa のテスト基盤（sansa-testkit）が提供する主要ユーティリティについて、
「外部契約として維持すべき振る舞い」をテストで担保するためのテスト範囲とテストケースを定義する。

本仕様は testkit の“実装詳細”を固定化することを目的としない。
ChatGPT 等による生成・改変が行われても、契約として維持すべき振る舞いが破綻していないことを確認可能にする。

---

## 2. 対象範囲

- モジュール：sansa-testkit
- 対象ユーティリティ（例）
  - com.sansa.testkit.util.TestPrinter
  - com.sansa.testkit.util.DummyUtil
  - com.sansa.testkit.annotations.DummyImplementation

---

## 3. 非対象（本仕様で扱わないもの）

- 表示フォーマットの装飾（見栄えのみの変更）
- 100%カバレッジ達成のためだけの実装依存テスト（内部 private など）
- 各サービス（sansa-auth 等）のドメイン固有テスト規約

---

## 4. テストケース一覧（自己検証UT）

本仕様のTCは、1テストメソッドが複数TCをまとめて検証することを許容する  
※ただし実行ログなどで、どのTCがパスしたかわかるように結果を出力すること

### 4.1 TestPrinter（基本出力）

- TESTKIT-TP-TC-001 必須：成功/失敗のケース出力が行われる
  - 期待：case の title が出力に含まれる（例：success / real, fail / real）
- TESTKIT-TP-TC-002 必須：DummyImplementation が付いた対象は DUMMY として識別される
  - 期待：DUMMY マーク（例：(DUMMY)）が出力に含まれる
- TESTKIT-TP-TC-003 必須：サマリが出力される
  - 期待：SUMMARY 行が存在する
  - 期待：TOTAL が正しく集計される（例：TOTAL=4）

（対応テスト：TestPrinterSelfTest.testPrinter_basic）

---

### 4.2 DummyUtil（ダミー判定）

- TESTKIT-DU-TC-001 必須：クラスが DummyImplementation を持つ場合 dummy と判定する
- TESTKIT-DU-TC-002 必須：インスタンスも dummy 判定できる
- TESTKIT-DU-TC-003 必須：メソッド単位でも dummy 判定できる
- TESTKIT-DU-TC-004 必須：hasAnyDummy は入力配列に dummy が含まれる場合 true を返す

（対応テスト：TestPrinterSelfTest.dummyUtil_basic）

---

### 4.3 TestPrinter（IDフォーマット）

- TESTKIT-TP-TC-101 必須：caseId ありの場合、ケースIDは "[{module}:{suite}-{caseId}]" 形式で出力される
  - 例："[XMOD:TX1-99]"
- TESTKIT-TP-TC-102 必須：SUMMARY は "{module}:{suite}" 形式で出力される
  - 例："SUMMARY XMOD:TX1"

（対応テスト：TestPrinterSelfTest.testPrinter_idFormat）

---

### 4.4 TestPrinter（caseId null のフォールバック）

- TESTKIT-TP-TC-201 必須：caseId が null でも ID 組み立てが破綻しない
  - 期待："[{module}:{suite}]" が出力される（例："[MOD:TSX]"）

（対応テスト：TestPrinterSelfTest.testPrinter_caseIdNull）

---

## 5. 実装との対応（参照）

- 自己検証UT：TestPrinterSelfTest.java
  - testPrinter_basic
  - dummyUtil_basic
  - testPrinter_idFormat
  - testPrinter_caseIdNull

---

## 6. 関連ドキュメント

- 共通仕様：テスト基盤（sansa-testkit の仕様）※既存
- テストポリシー（共通／各サービス）

---
[目次](../../目次.md) > テスト仕様 > 共通基盤 > テスト基盤_testkit_テスト仕様
