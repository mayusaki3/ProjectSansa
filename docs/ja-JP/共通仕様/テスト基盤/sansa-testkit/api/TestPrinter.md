[目次](../../../../目次.md) > [テスト基盤](../../目次.md) > [sansa-testkit](../目次.md) > api > TestPrinter

# TestPrinter API 仕様書

## 1. 概要

TestPrinter は、ユニットテスト（UT）および統合テスト（IT）の両方で  
**統一されたテスト出力フォーマット** を提供するユーティリティである。

目的は以下のとおり：

- テスト出力の標準化  
- CI による自動集計（成功/失敗/スキップ）の安定化  
- DUMMY 実装の判別を容易にするメタ情報の付加  
- テスト仕様（テスト番号体系）との簡易連携

TestPrinter はテストフレームワーク（JUnit など）とは独立して動作し、  
標準出力へ結果を整形して出力するだけの軽量設計である。

---

## 2. テスト番号体系との関係

TestPrinter は **テスト番号の妥当性検証を行わない**。  
付与された testId をそのまま表示するだけである。

例：  
- UT → `M01:UT-01-001`  
- IT → `M01:IT-02-003`

責務分離のため、TestPrinter は testId の形式チェックを行わない。  
testId の命名規則はテスト作者が保証する。

---

## 3. API 一覧

TestPrinter の外部公開 API は以下のとおり：

### 3.1 startSuite(String suiteName)

#### 目的
テストスイート開始を宣言する。  
スイート単位で SUMMARY を分ける境界。

#### 引数
| 名前 | 型 | 説明 |
|------|------|------|
| suiteName | String | スイート名。null 可。（例: `M01:UT-01`） |
| suiteDescription | String | スイートの説明。null 可。（例: `unittest suite`） |

#### 出力例
```
=== M01:UT-01 unittest suite ===
```

#### 備考
- suiteName が null / 空文字の場合は `"UNKNOWN"` として扱う。

---

### 3.2 success(String testId, String message, boolean isDummy)

#### 目的
成功テストケースを出力する。

#### 引数
| 名前 | 型 | 説明 |
|------|------|------|
| testId | String | テスト番号。null の場合 `[UNKNOWN]`。（例: `M01:UT-01-001`） |
| message | String | 説明文。null の場合空文字。（例: `正常登録`） |
| isDummy | boolean | DUMMY 実装フラグ。true の場合 `[DUMMY]` を testId の後に付与。 |

#### 出力形式
```
✅[testId] [DUMMY] message
```

#### 出力例（通常）
```
✅[M01:UT-01-001] 正常登録
```

#### 出力例（DUMMY）
```
✅[M01:UT-01-003] [DUMMY] 未実装処理（固定値を返す）
```

---

### 3.3 failure(String testId, String message, Throwable error)

#### 目的
失敗テストケースを出力する。

#### 引数
| 名前 | 型 | 説明 |
|------|------|------|
| testId | String | テスト番号 |
| message | String | 説明文 |
| error | Throwable | 例外情報（標準出力には出さない） |

#### 出力形式
```
❌[testId] message
```

#### 出力例
```
❌[M01:UT-01-004] 不正フォーマットを受理している
```

---

### 3.4 summary()

#### 目的
スイート単位で成功数 / 失敗数 / スキップ数を集計し、最後に出力する。

#### 出力例
```
--- SUMMARY M01:UT-01: ✅=3 / ❌=1 / SKIP=1 / TOTAL=5 ---
```

---

## 4. 出力フォーマット定義

### 4.1 成功（通常）
```
✅[M01:UT-01-001] 説明文
```

### 4.2 成功（DUMMY）
```
✅[M01:UT-01-003] [DUMMY] 説明文
```

### 4.3 失敗
```
❌[M01:UT-01-004] 説明文
```

### 4.4 スイート開始
```
=== M01:UT-01 unittest suite ===
```

### 4.5 SUMMARY
```
--- SUMMARY M01:UT-01: ✅=3 / ❌=1 / SKIP=0 / TOTAL=4 ---
```

---

## 5. 例外処理・エラーポリシー

- TestPrinter は例外を外へ投げない。
- testId = null → `[UNKNOWN]`
- message = null → 空文字
- suiteName = null → `"UNKNOWN"`
- error はログ用であり、標準出力へは出さない。

---

## 6. 最小利用例（SelfTest より抜粋）

```
printer.startSuite("M01:UT-01", "SelfTest suite");

printer.success("M01:UT-01-001", "正常系：登録成功", false);
printer.success("M01:UT-01-003", "暗号化はダミー実装", true);

printer.failure("M01:UT-01-004", "例外を投げるべき入力を通過", null);

printer.summary();
```

出力例：

```
=== M01:UT-01 SelfTest suite ===
✅[M01:UT-01-001] 正常系：登録成功
✅[M01:UT-01-003] [DUMMY] 暗号化はダミー実装
❌[M01:UT-01-004] 例外を投げるべき入力を通過
--- SUMMARY M01:UT-01: ✅=2 / ❌=1 / SKIP=0 / TOTAL=3 ---
```

---

## 7. 将来拡張

- skip() 出力 API の追加  
- 出力ログの JSON 化  
- カテゴリ集計機能（UT/IT の自動判別など）

---
[目次](../../../../目次.md) > [テスト基盤](../../目次.md) > [sansa-testkit](../目次.md) > api > TestPrinter
