package com.sansa.logging.impl;

import com.sansa.logging.AuditLogger;
import com.sansa.logging.SystemLogger;
import com.sansa.logging.model.AuditRecord;

import java.util.Objects;

/**
 * 監査ログの暫定実装。
 *
 * <p>現段階では、AuditRecord#toString() の結果を SystemLogger に渡して
 * INFO レベルで出力するだけの簡易実装とする。</p>
 *
 * <p>将来的には JSON シリアライズや Cassandra への永続化など、
 * 共通仕様に基づいた本格的な実装へ差し替える。</p>
 */
public class SimpleAuditLogger implements AuditLogger {

    private final SystemLogger systemLogger;

    /**
     * コンストラクタ。
     *
     * @param systemLogger 出力先として利用する SystemLogger（必須）
     */
    public SimpleAuditLogger(SystemLogger systemLogger) {
        this.systemLogger = Objects.requireNonNull(systemLogger, "systemLogger must not be null");
    }

    @Override
    public void write(AuditRecord record) {
        if (record == null) {
            systemLogger.warn("AuditRecord is null. Skip audit logging.");
            return;
        }
        // 暫定的に toString() の結果を INFO ログとして出力する。
        systemLogger.info("AUDIT {}", record);
    }
}
