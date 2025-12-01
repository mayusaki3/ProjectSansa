[目次](../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-auth 統合手順

# sansa-auth 統合手順

## 1. 依存関係の追加

`sansa-auth/pom.xml` に、`sansa-testkit` を **test スコープ**で追加します。

```xml
<dependency>
    <groupId>com.sansa</groupId>
    <artifactId>sansa-testkit</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

※ 親 POM（`project-sansa-parent`）で version を管理する場合は、上記 `version` を省略し、  
親側の `dependencyManagement` 設定に従って解決されるようにしても構いません。

## 2. 既存テスト 1 本を testkit 方式に移行

`sansa-auth` のテストクラスのうち 1 本を対象に、以下を行います。

1. テスト番号（例: `M01:UT-01-001`）を決定する
2. テストメソッド内で `TestPrinter` を呼び出し、開始・結果を出力する

イメージコード:

```java
@Test
void M01_UT_01_001_preRegister_success() {
    var printer = new TestPrinter("M01:UT-01-001", "preRegister 正常系: 事前登録メール送信");

    printer.start();
    // arrange & act
    // AuthService.preRegister(...) を呼び出し
    // assert
    printer.success();
}
```

実際の `TestPrinter` の API（メソッド名やコンストラクタ引数）は、実装に合わせて調整してください。  
ここでは「テスト番号と説明を渡して、start/success/fail を呼ぶ」という利用イメージのみを示しています。

## 3. テストマトリクスとの連携

`sansa-auth` のテストマトリクス（例: `M01_認証セッション_テストマトリクス.md`）には、  
各テストケースに対応するテスト番号を記載します。

- 例: `M01:UT-01-001 preRegister 正常系: 事前登録メール送信`

テストコード側のメソッド名・`TestPrinter` に渡すテキスト・テストマトリクスの行が揃うことで、以下が容易になります。

- 仕様（API/テストマトリクス） → テストコード → 実行ログ のトレース
- CI ログからの失敗テストの特定

## 4. ダミー実装のマーキング (@DummyImplementation)

sansa-auth のように、初版リリース前で段階的に実装を進めるモジュールでは、  
一部のサービスクラスを「ダミー実装」として暫定提供することがあります。

その際、以下のように `@DummyImplementation` を付与しておくことで、  
テスト出力上でダミー実装を明確に識別できます。

- テスト対象クラス側（例: AuthService の暫定実装）

```java
import com.sansa.testkit.dummy.DummyImplementation;

@DummyImplementation
public class AuthServiceDummyImpl implements AuthService {
    // TODO: 本実装に置き換えるまでの暫定コード
}
```

- 効果:
  - `AuthServiceDummyImpl` を対象としたテスト結果行の末尾に `[DUMMY]` が付く
  - テスト結果一覧を見たときに「どのケースがダミー実装に対するものか」を即座に判断できる
  - 本実装移行フェーズで、ダミー実装が残っていないか確認するための手がかりになる

sansa-auth 以外のモジュールでも、同様に `@DummyImplementation` を付与することで  
**「テスト対象の実装が暫定かどうか」を共通フォーマットで表現**できます。

---
[目次](../../目次.md) > テスト基盤 > [テスト基盤 目次](../目次.md) > [sansa-testkit 目次](目次.md) > sansa-auth 統合手順
