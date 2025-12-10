package com.sansa.auth.exception;

import java.util.Arrays;

/**
 * 認証・認可ドメインで発生する例外の共通基底クラス。
 *
 * <p>特徴:
 * <ul>
 *   <li>ユーザー向け文言ではなく「機械可読なエラーコード」を中心に扱う。</li>
 *   <li>多言語対応や詳細メッセージ組み立て用の可変長引数 {@code args} を保持する。</li>
 *   <li>{@link #getMessage()} はデバッグ用途として {@code code} を返すのみとし、
 *       UI や API レスポンス用の文言には直接使用しない。</li>
 * </ul>
 *
 * <p>想定される利用パターン:
 * <pre>{@code
 * throw new InvalidCredentialsException("invalid_credentials");
 * throw new SessionNotFoundException("session_not_found", sessionId);
 * }</pre>
 */
public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * エラーコード（機械可読な識別子）。
     * 例: {@code "invalid_credentials"}, {@code "session_not_found"} など。
     */
    private final String code;

    /**
     * メッセージ組み立て等に利用可能な追加引数。
     * UI / i18n 層でテンプレート展開する際に使用することを想定する。
     */
    private final Object[] args;

    /**
     * エラーコードのみを指定して例外を生成するコンストラクタ。
     *
     * @param code 機械可読なエラーコード（null/空文字は不可を推奨）
     */
    public DomainException(String code) {
        this(code, null, (Object[]) null);
    }

    /**
     * エラーコードと原因例外を指定して例外を生成するコンストラクタ。
     *
     * @param code  機械可読なエラーコード
     * @param cause 原因例外
     */
    public DomainException(String code, Throwable cause) {
        this(code, cause, (Object[]) null);
    }

    /**
     * エラーコードと追加引数を指定して例外を生成するコンストラクタ。
     *
     * @param code 機械可読なエラーコード
     * @param args メッセージ組み立て用の任意個の引数
     */
    public DomainException(String code, Object... args) {
        this(code, null, args);
    }

    /**
     * エラーコード・原因例外・追加引数をすべて指定して例外を生成するコンストラクタ。
     *
     * @param code  機械可読なエラーコード
     * @param cause 原因例外
     * @param args  メッセージ組み立て用の任意個の引数
     */
    public DomainException(String code, Throwable cause, Object... args) {
        // RuntimeException の message には code をそのまま渡す（デバッグ/ログ用）
        super(code, cause);
        this.code = code;
        // 外部から配列を書き換えられないように防御的コピーを行う
        this.args = (args == null || args.length == 0) ? new Object[0] : Arrays.copyOf(args, args.length);
    }

    /**
     * エラーコードを返す。
     *
     * @return 機械可読なエラーコード
     */
    public String getCode() {
        return code;
    }

    /**
     * 追加引数のコピーを返す。
     *
     * <p>呼び出し側で配列を書き換えても、内部状態は変化しない。
     *
     * @return 追加引数の配列（防御的コピー）
     */
    public Object[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }
}
