package com.sansa.logging;

/**
 * システムログ出力のための抽象インターフェース。
 *
 * <p>本インターフェースは、Project Sansa 全体の共通ロギング仕様に基づき、
 * 実装方式（SLF4J / JSON ログ / 外部サービスなど）を隠蔽するための
 * エントリーポイントとして利用する。</p>
 *
 * <p>本インターフェースはドメイン非依存であり、sansa-auth 以外の
 * モジュールからも利用されることを前提とする。</p>
 */
public interface SystemLogger {

    /**
     * INFO レベルでログを出力する。
     *
     * @param message ログメッセージ（パターン文字列を想定）
     * @param args    メッセージのプレースホルダに対応する引数（任意）
     */
    void info(String message, Object... args);

    /**
     * WARN レベルでログを出力する。
     *
     * @param message ログメッセージ（パターン文字列を想定）
     * @param args    メッセージのプレースホルダに対応する引数（任意）
     */
    void warn(String message, Object... args);

    /**
     * ERROR レベルでログを出力する。
     *
     * @param message ログメッセージ（パターン文字列を想定）
     * @param ex      例外オブジェクト（null 可）
     * @param args    メッセージのプレースホルダに対応する引数（任意）
     */
    void error(String message, Throwable ex, Object... args);
}
