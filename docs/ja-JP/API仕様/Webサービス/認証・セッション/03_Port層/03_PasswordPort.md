[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > PasswordPort

# PasswordPort I/F 仕様

## 位置づけ・役割

PasswordPort は、アプリケーション層から見た「パスワードハッシュ生成・検証」の抽象化 I/F です。

- ドメイン/アプリケーション層は「平文パスワード」を扱うが、保存・比較は PasswordPort 経由で行う。
- 具体的なハッシュアルゴリズム（例: Argon2id, bcrypt 等）やライブラリはインフラ層の責務とする。
- ハッシュ形式（バージョン/パラメータ）は PasswordPort 実装でカプセル化し、アプリケーション層からは透過的に扱えるようにする。

主な利用箇所:

- ユーザー登録（RegisterCommand）時のパスワード保存
- パスワードログイン（LoginCommand）時の検証
- パスワード変更/リセット時の再ハッシュ

## インターフェース定義

```java
public interface PasswordPort {

    /**
     * 生パスワードから保存用ハッシュを生成する。
     *
     * @param rawPassword ユーザー入力の生パスワード
     * @return 保存用ハッシュ文字列
     * @throws PasswordPortException ハッシュ生成に失敗した場合
     */
    String hash(String rawPassword);

    /**
     * 生パスワードと保存済みハッシュを比較し、一致するか判定する。
     *
     * @param rawPassword ユーザー入力の生パスワード
     * @param passwordHash 保存済みハッシュ
     * @return 一致する場合 true、不一致または無効なハッシュ形式の場合 false
     * @throws PasswordPortException 内部エラー（フォーマット不正など）が発生した場合
     */
    boolean matches(String rawPassword, String passwordHash);

    /**
     * 既存ハッシュが現在の推奨パラメータから見て再ハッシュ対象かどうかを判定する。
     *
     * @param passwordHash 保存済みハッシュ
     * @return 再ハッシュ推奨なら true、現行パラメータで十分なら false
     */
    boolean needsRehash(String passwordHash);
}
```

実装例（イメージ）:

- Argon2id + バージョン情報/パラメータをハッシュ文字列に埋め込み、`needsRehash` で閾値を判定。
- `matches` 内で古い形式のハッシュもサポートし、マッチした場合に `needsRehash=true` で再ハッシュを促す。

## メソッド仕様詳細

### hash(rawPassword)

- 入力:
  - `rawPassword`: 非 null/非空文字列が前提（バリデーションはアプリケーション層で実施済みを想定）。
- 出力:
  - 保存用のハッシュ文字列（例: `$argon2id$v=19$m=...$salt$hash` 形式）。
- 期待動作:
  - 実装側は十分な計算コストとソルト生成を行う。
  - 実装ごとにパラメータを設定ファイル等で調整可能とする。
- 例外:
  - 内部ライブラリエラーなど、予期しない障害は `PasswordPortException`（実装定義のランタイム例外）として送出し、上位でログ/監査対象とする。

### matches(rawPassword, passwordHash)

- 入力:
  - `rawPassword`: ユーザー入力の生パスワード。
  - `passwordHash`: DB 等に保存されたハッシュ文字列。
- 出力:
  - パスワードが一致すれば `true`、そうでなければ `false`。
- 期待動作:
  - タイミング攻撃対策のため、比較は一定時間で行う（ライブラリ標準 API を使用）。
  - `passwordHash` のフォーマットが不正な場合は、実装方針に応じて:
    - `false` を返す（一般的なログイン失敗扱い）
    - または `PasswordPortException` を送出し、別途運用上の修正を促す
  - sansa-auth 初版では「ログイン失敗扱い（false返却）」を基本としつつ、詳細はログに残す構成を推奨。

### needsRehash(passwordHash)

- 入力:
  - 既存のパスワードハッシュ文字列。
- 出力:
  - 再ハッシュが推奨される場合 `true`、現行パラメータと同等の場合 `false`。
- 期待動作:
  - ハッシュに埋め込まれたパラメータ（メモリ/反復回数/アルゴリズムバージョンなど）を解析し、アプリケーション設定の閾値と比較する。
  - 将来的にコストパラメータを調整していく際の移行を容易にする。
- 利用パターン:
  - ログイン成功時:
    - `matches` が `true` で、`needsRehash` も `true` の場合
      - バックグラウンドで新ハッシュを保存するか、次のパスワード変更タイミングで再ハッシュを要求する等の運用を検討。

## エラー処理・ロギング方針

- PasswordPort はセキュリティ上の重要コンポーネントであるため、詳細なエラーメッセージを外部に出さない。
- 例外内容はログ/監査にのみ出力し、クライアントには「認証失敗」として扱う。
- レート制限やアカウントロックは AuthService 側で `AuthResultType.LOCKED` / `RATE_LIMITED` として扱い、PasswordPort 自体はあくまで「ハッシュ処理」に責務を限定する。

## セキュリティ考慮事項

- ハッシュアルゴリズムは PBKDF2 などの旧方式ではなく、Argon2id 等のモダンなメモリハード関数を推奨。
- ソルトの再利用禁止・ランダム生成を徹底する（ライブラリ標準 API を利用）。
- パスワードの最小長/複雑性のチェックは、バリデーションルールとしてアプリケーション層で定義する。

## 関連ドキュメント

- [AuthService I/F 仕様](../02_Application層/01_AuthService.md)
- [Port DTO: Store Models](./DTO_01_Store_Models.md)（ユーザーモデルにおける `passwordHash` の扱いなど）
- [Port DTO: PortErrorResult](./DTO_06_PortErrorResult.md)（PasswordPortException のラップに利用する場合）

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > PasswordPort
