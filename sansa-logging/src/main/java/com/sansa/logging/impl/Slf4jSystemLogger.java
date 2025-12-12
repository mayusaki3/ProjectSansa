package com.sansa.logging.impl;

import com.sansa.logging.SystemLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J を用いてシステムログを出力する標準実装。
 *
 * <p>本クラスは単純に SLF4J Logger へ委譲する。</p>
 *
 * <p>将来的に MDC や構造化ログ（JSON ログ）を導入する場合も、
 * SystemLogger インターフェースを変更せずに差し替え可能とする。</p>
 */
public class Slf4jSystemLogger implements SystemLogger {

    /**
     * 実際のログ出力に利用する SLF4J Logger。
     */
    private final Logger logger;

    /**
     * デフォルトコンストラクタ。
     * クラス名をカテゴリとする Logger を利用する。
     */
    public Slf4jSystemLogger() {
        this(LoggerFactory.getLogger(Slf4jSystemLogger.class));
    }

    /**
     * Logger を差し替えるためのコンストラクタ。
     * 主にテストやカテゴリ分離のために利用する。
     *
     * @param logger 利用する SLF4J Logger（必須）
     */
    public Slf4jSystemLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    @Override
    public void warn(String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, args);
        }
    }

    @Override
    public void error(String message, Throwable ex, Object... args) {
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (ex != null) {
            // 暫定実装：例外付きログでは message + Throwable を優先し、args は無視する
            logger.error(message, ex);
        } else {
            logger.error(message, args);
        }
    }
}
