package com.sansa.logging;

import com.sansa.logging.impl.SimpleAuditLogger;
import com.sansa.logging.model.AuditRecord;
import com.sansa.testkit.util.TestCaseReporter;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T02: SimpleAuditLogger の基本挙動に関する単体テスト。
 */
class SimpleAuditLoggerTest {

    /**
     * テスト用の簡易 SystemLogger 実装。
     */
    private static class TestSystemLogger implements SystemLogger {
        enum Level { NONE, INFO, WARN, ERROR }

        Level lastLevel = Level.NONE;
        String lastMessage;
        Object[] lastArgs;
        Throwable lastThrowable;

        @Override
        public void info(String message, Object... args) {
            lastLevel = Level.INFO;
            lastMessage = message;
            lastArgs = args;
        }

        @Override
        public void warn(String message, Object... args) {
            lastLevel = Level.WARN;
            lastMessage = message;
            lastArgs = args;
        }

        @Override
        public void error(String message, Throwable ex, Object... args) {
            lastLevel = Level.ERROR;
            lastMessage = message;
            lastArgs = args;
            lastThrowable = ex;
        }
    }

    /**
     * LOGGING-COMMON-TC-101
     *
     * null レコードの場合は WARN ログになる
     */
    @Test
    void T02_01_shouldWarnWhenRecordIsNull() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-101");

        TestSystemLogger systemLogger = new TestSystemLogger();
        SimpleAuditLogger auditLogger = new SimpleAuditLogger(systemLogger);

        auditLogger.write(null);

        assertEquals(TestSystemLogger.Level.WARN, systemLogger.lastLevel);
        assertEquals("AuditRecord is null. Skip audit logging.", systemLogger.lastMessage);
    }

    /**
     * LOGGING-COMMON-TC-102
     *
     * 有効なレコードの場合は INFO ログになる
     */
    @Test
    void T02_02_shouldInfoLogWhenRecordIsValid() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-102");

        TestSystemLogger systemLogger = new TestSystemLogger();
        SimpleAuditLogger auditLogger = new SimpleAuditLogger(systemLogger);

        AuditRecord record = new AuditRecord(
                "AUTH_LOGIN",
                "user-1",
                "SUCCESS",
                Instant.parse("2025-01-01T00:00:00Z"),
                null,
                null
        );

        auditLogger.write(record);

        assertEquals(TestSystemLogger.Level.INFO, systemLogger.lastLevel);
        assertEquals("AUDIT {}", systemLogger.lastMessage);
        assertNotNull(systemLogger.lastArgs);
        assertEquals(1, systemLogger.lastArgs.length);
        assertSame(record, systemLogger.lastArgs[0]);
    }
}
