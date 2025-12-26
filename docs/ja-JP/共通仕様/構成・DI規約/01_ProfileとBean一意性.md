[目次](../../目次.md) > 共通仕様 > [構成・DI規約 目次](./目次.md) > Profile と Bean 一意性 規約

---

# Profile と Bean 一意性 規約

## 1. 目的

Spring Boot において、Profile 切替時に複数の実装 Bean が同時に有効化されると、  
以下の問題が発生する。

- No qualifying bean / expected single matching bean but found 2
- ApplicationContext 初期化失敗
- テスト全体の **failure threshold 超過による連鎖スキップ**

本規約はこれを防ぐため、**Profile と Bean 定義の一意性ルール**を明確化する。

---

## 2. 基本原則（必須）

### 原則1：Interface に対する実装 Bean は、Profile で必ず排他的にする

- @Profile を付けない複数実装は禁止
- @Primary による逃げは禁止（例外は明示的に規約化する）

---

### 原則2：本番実装は必ず prod Profile に閉じる

- 本番用 Bean が test / it / inmem でロードされることを禁止
- prod が有効でない限り、本番外部 I/O は発生しない設計とする

---

### 原則3：テスト用実装は「用途別 Profile」で分離する

| 用途 | 推奨 Profile |
|---|---|
| 単体テスト | test |
| 結合 / IT | it |
| インメモリ動作 | inmem |

---

## 3. MailService を例とした適用指針（代表例）

### 3.1 想定構成

| 実装 | 用途 | Profile |
|---|---|---|
| SmtpMailService | 本番 SMTP | prod |
| InMemoryMailService | テスト用インメモリ | test, it, inmem |
| NoopMailService | 無送信（必要な場合） | 明示的 Profile |

---

### 3.2 正しい定義例（概念）

```
MailService
 ├─ SmtpMailService       (@Profile("prod"))
 └─ InMemoryMailService   (@Profile({"test","it","inmem"}))
```

- prod と非 prod が **同時に有効化されない**
- MailService 注入点では Bean が常に 1 つに確定する

---

## 4. 禁止パターン（重要）

### ❌ Profile 未指定の複数実装

- Spring の component scan により常時 2 Bean が存在
- Context 初期化時点で失敗

---

### ❌ @Primary による回避

- Profile 切替時の安全性が保証されない
- 将来の実装追加で破綻しやすい

---

### ❌ TestConfig と MainConfig の混在定義

- src/test/java と src/main/java の両方で Bean 定義
- Profile 条件が曖昧なまま読み込まれる

---

## 5. テスト基盤（sansa-testkit）との関係

- 本規約は **testkit 専用ではない**
- ただし testkit を利用するテストでは、本規約に従うことを必須とする
- testkit 側では **Profile を前提とした Bean 差し替え**のみを行う

---

## 6. 適用範囲（将来拡張）

本規約は MailService に限定されない。  
以下の Interface 群にも同様に適用する。

- Store / Repository Adapter
- Clock / TimeProvider
- RateLimiter
- 外部 API Client
- Audit / Logging Backend

---

## 7. sansa-auth における位置づけ

- sansa-auth は **本規約の最初の適用プロジェクト**
- MailService 問題は「代表的な初期違反事例」として扱う
- 今後の修正は **規約準拠を前提**とする

---
[目次](../../目次.md) > 共通仕様 > [構成・DI規約 目次](./目次.md) > Profile と Bean 一意性 規約
