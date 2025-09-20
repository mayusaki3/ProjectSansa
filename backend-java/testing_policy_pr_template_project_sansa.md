# docs/TESTING.md — ProjectSansa テスト方針

本ドキュメントは **「どのテストが何を担保しているか」** を常に可視化し、追加/変更があれば PR 上で把握できるようにするためのガイドラインです。

## 1. 概要
- **Unit Test**: 単機能の純粋なロジック検証（外部I/Oに依存しない）。
- **Integration Test (IT)**: Cassandra / HTTP エンドポイントを含む実運用に近い検証（Testcontainers 使用）。
- **Smoke**: dev 起動後の最小通し確認（手動・自動どちらでも可）。

## 2. 命名・配置規約
```
backend-java/
  src/test/java/.../*Test.java          # Unit Test（Surefire）
  src/test/java/.../*IT.java            # Integration Test（Failsafe）
  src/test/resources/                   # テスト専用設定（例: quarkus.http.test-port=8080）
```
- **クラス命名**: 単体 → `*Test`, 結合 → `*IT`
- **タグ**（任意）: `@Tag("api")`, `@Tag("repo")`, `@Tag("e2e")`

## 3. 実行コマンド
```powershell
# Unit のみ
mvn -B -ntp -DskipITs=true test

# IT 含めフル
mvn -B -ntp -DskipITs=false verify

# レポート（HTML）生成
mvn surefire-report:report-only failsafe-report:report-only
```
- JUnit XML: `target/surefire-reports/`, `target/failsafe-reports/`
- HTML レポ: `target/site/surefire-report.html`, `target/site/failsafe-report.html`
- カバレッジ: `target/site/jacoco/index.html`

## 4. カバレッジ（推奨しきい値）
- Line: **80%**、Branch: **60%** を目安（IT 主体の領域では柔軟に判断）。
- 重要バグ修正時は **回帰テスト**を必ず追加。

## 5. 既存テストの役割（2025-09 現在）
- `psansa.api.AuthAndPostApiIT` … **E2E 最小シナリオ**
  - `/auth/register` → `/auth/login` → `POST /posts`(JWT必須) → `GET /posts`(匿名可)
  - Testcontainers で Cassandra 起動・初期化確認

## 6. 追加テストの方針（例）
- **Repo 層**: `PostRepoTest`（timeuuid 並び、limit、before 条件、空結果）
- **API 層**: `PostResourceTest`（バリデーション、400/401/404、lang 自動付与の有無）
- **ページング**: カーソル生成/復元、日バケツ跨ぎ、端数ページ、`limit` 異常値クランプ
- **セキュリティ**: 期限切れ JWT、aud/iss 不一致、署名不正
- **エラーパス**: Cassandra タイムアウト時のハンドリング（500→メッセージ観察）

## 7. テンプレ（雛形）
```java
// src/test/java/psansa/api/PostRepoTest.java
package psansa.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostRepoTest {
  @Test
  void example() {
    assertTrue(true);
  }
}
```

---

# .github/PULL_REQUEST_TEMPLATE.md — PR テンプレート

## 概要
- 変更点（目的 / 背景）:
- 関連Issue/チケット:

## テスト
- [ ] Unit 追加/変更: `ClassNameTest`（タグ: ）
- [ ] IT 追加/変更: `ClassNameIT`（タグ: ）
- 手動スモーク:
  - [ ] `register` → `login` → `POST /posts`(JWT) → `GET /posts`

### レポート/カバレッジ
- surefire/failsafe HTML: `target/site/`
- JaCoCo: `target/site/jacoco/index.html`
- カバレッジ（差分）: Before → After: **__% → __%**

## 影響範囲
- 互換性: あり / なし（詳細）
- マイグレーション: あり / なし（詳細）

## 補足
- デプロイ/ロールバック手順:
- 既知の懸念/フォローアップ:

---

# POM 追加スニペット（レポート & カバレッジ）
```xml
<!-- reports -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-report-plugin</artifactId>
  <version>3.2.5</version>
</plugin>

<!-- failsafe（既存なら version と executions を確認） -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>3.2.5</version>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
    </execution>
  </executions>
</plugin>

<!-- JaCoCo coverage -->
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>verify</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
> すでに failsafe/surefire がある場合は **version 整合**に注意。`report-only` のゴールはサイト生成用に都度呼び出します。

---

# 補助スクリプト（任意） — tools/list-tests.ps1
```powershell
param([switch]$Raw)
$tests = Get-ChildItem -Recurse src/test/java -Filter *.java |
  ForEach-Object {
    $path = $_.FullName
    $name = (Select-String -Path $path -Pattern 'class\s+([A-Za-z0-9_]+)') | ForEach-Object { $_.Matches.Groups[1].Value } | Select-Object -First 1
    [PSCustomObject]@{ Class=$name; Path=$path }
  } | Where-Object { $_.Class }

if ($Raw) { $tests | Format-Table -Auto; return }

$unit = $tests | Where-Object { $_.Class -like '*Test' -and $_.Class -notlike '*IT' }
$it   = $tests | Where-Object { $_.Class -like '*IT' }

"`n[Unit Tests]"; $unit | Sort-Object Class | Format-Table -Auto
"`n[Integration Tests]"; $it | Sort-Object Class | Format-Table -Auto
```

---

## 運用ルール（要点）
1. **テストを追加/変更したら PR に必ず列挙**（本テンプレに従う）。
2. **HTML レポート/カバレッジを PR からダウンロード可能に**（CI で `target/site` を artifact 化）。
3. 重大修正や仕様変更時は **回帰テストを先に書く**（Red → Green → Refactor）。

