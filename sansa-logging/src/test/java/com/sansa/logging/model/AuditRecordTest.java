package com.sansa.logging.model;

import com.sansa.testkit.util.TestCaseReporter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T01: AuditRecord の基本挙動に関する単体テスト。
 */
class AuditRecordTest {

    /**
     * LOGGING-COMMON-TC-001
     * eventType が null の場合は NPE
     */
    @Test
    void T01_01_shouldThrowWhenEventTypeIsNull() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-001");

        assertThrows(NullPointerException.class, () -> new AuditRecord(
                null,
                "user-1",
                "SUCCESS",
                Instant.now(),
                null,
                null
        ));
    }

    /**
     * LOGGING-COMMON-TC-002
     *
     * result が null の場合は NPE
     */
    @Test
    void T01_02_shouldThrowWhenResultIsNull() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-002");

        assertThrows(NullPointerException.class, () -> new AuditRecord(
                "AUTH_LOGIN",
                "user-1",
                null,
                Instant.now(),
                null,
                null
        ));
    }

    /**
     * LOGGING-COMMON-TC-003
     *
     * details が null の場合は emptyMap になる
     */
    @Test
    void T01_03_shouldUseEmptyMapWhenDetailsIsNull() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-003");

        AuditRecord record = new AuditRecord(
                "AUTH_LOGIN",
                "user-1",
                "SUCCESS",
                Instant.now(),
                null,
                null
        );

        assertNotNull(record.getDetails());
        assertTrue(record.getDetails().isEmpty());
    }

    /**
     * LOGGING-COMMON-TC-004
     *
     * details は不変マップとして保持される
     */
    @Test
    void T01_04_detailsShouldBeUnmodifiable() {
        TestCaseReporter.tc("LOGGING-COMMON-TC-004");

        Map<String, Object> details = new HashMap<>();
        details.put("k", "v");

        AuditRecord record = new AuditRecord(
                "AUTH_LOGIN",
                "user-1",
                "SUCCESS",
                Instant.now(),
                null,
                details
        );

        assertEquals(1, record.getDetails().size());
        assertThrows(UnsupportedOperationException.class, () -> record.getDetails().put("x", "y"));
    }
}
