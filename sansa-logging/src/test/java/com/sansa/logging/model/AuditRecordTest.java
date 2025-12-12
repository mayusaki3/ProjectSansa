package com.sansa.logging.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T01: AuditRecord の基本挙動に関する単体テスト。
 */
class AuditRecordTest {

    // T01-01: eventType が null の場合は NPE
    @Test
    void T01_01_shouldThrowWhenEventTypeIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new AuditRecord(
                    null,
                    "user-1",
                    "SUCCESS",
                    Instant.now(),
                    null,
                    null
            );
        });
    }

    // T01-02: result が null の場合は NPE
    @Test
    void T01_02_shouldThrowWhenResultIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new AuditRecord(
                    "AUTH_LOGIN",
                    "user-1",
                    null,
                    Instant.now(),
                    null,
                    null
            );
        });
    }

    // T01-03: details が null の場合は emptyMap になる
    @Test
    void T01_03_shouldUseEmptyMapWhenDetailsIsNull() {
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

    // T01-04: details は不変マップとして保持される
    @Test
    void T01_04_detailsShouldBeUnmodifiable() {
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
        assertThrows(UnsupportedOperationException.class, () -> {
            record.getDetails().put("x", "y");
        });
    }
}
