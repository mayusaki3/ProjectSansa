package com.sansa.testkit.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTKIT-TCR-TC-001
 * TESTKIT-TCR-TC-002
 * TESTKIT-TCR-TC-003
 *
 * TestCaseReporter の自己検証UT。
 */
class TestCaseReporterTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    /**
     * TESTKIT-TCR-TC-001
     *
     * tc(id) は "[TESTCASE] id" を標準出力へ出力する。
     */
    @Test
    void shouldPrintTestcaseLine() {
        System.out.println("[TESTCASE] TESTKIT-TCR-TC-001");

        var bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos, true));

        TestCaseReporter.tc("LOGGING-COMMON-TC-001");

        String out = bos.toString();
        assertTrue(out.contains("[TESTCASE] LOGGING-COMMON-TC-001"));
    }

    /**
     * TESTKIT-TCR-TC-002
     *
     * tc(null) は例外を投げず、出力もしない。
     */
    @Test
    void shouldDoNothingWhenNull() {
        System.out.println("[TESTCASE] TESTKIT-TCR-TC-002");

        var bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos, true));

        assertDoesNotThrow(() -> TestCaseReporter.tc(null));
        assertEquals("", bos.toString());
    }

    /**
     * TESTKIT-TCR-TC-003
     *
     * tc("  ") は例外を投げず、出力もしない。
     */
    @Test
    void shouldDoNothingWhenBlank() {
        System.out.println("[TESTCASE] TESTKIT-TCR-TC-003");

        var bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos, true));

        assertDoesNotThrow(() -> TestCaseReporter.tc("   "));
        assertEquals("", bos.toString());
    }
}
