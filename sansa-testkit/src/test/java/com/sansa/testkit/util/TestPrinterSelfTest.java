package com.sansa.testkit.util;

import com.sansa.testkit.annotations.DummyImplementation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestPrinter / DummyUtil / DummyImplementation の
 * 基本的な動作を自己検証するためのテスト。
 *
 * sansa-testkit 自体の UT を担うため、
 * 他モジュールに依存しないように構成している。
 */
public class TestPrinterSelfTest {

    /**
     * ダミー実装用のサンプルクラス。
     */
    @DummyImplementation("SelfTest DummyClass")
    static class DummyClass {
        public String hello() {
            return "dummy";
        }
    }

    /**
     * 本番実装用のサンプルクラス（ダミー注釈なし）。
     */
    static class RealClass {
        public String hello() {
            return "real";
        }
    }

    /**
     * TestPrinter の基本動作テスト。
     * 成功・失敗・ダミー判定・サマリ出力が期待通りであることを確認する。
     */
    @Test
    void testPrinter_basic() {
        // 出力捕捉用
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true);

        TestPrinter printer = new TestPrinter("TESTMOD", "T00", ps);

        // --- ケース1: 成功（本番）
        printer.printCase("01", "success / real", true, new RealClass());

        // --- ケース2: 失敗（本番）
        printer.printCase("02", "fail / real", false, new RealClass());

        // --- ケース3: 成功（ダミー）
        printer.printCase("03", "success / dummy", true, new DummyClass());

        // --- ケース4: 失敗（ダミー）
        printer.printCase("04", "fail / dummy", false, new DummyClass());

        // --- サマリ出力
        printer.printSummary();

        String output = bos.toString();

        // 出力に必要な要素が含まれているか検証
        assertTrue(output.contains("success / real"));
        assertTrue(output.contains("fail / real"));
        assertTrue(output.contains("success / dummy"));
        assertTrue(output.contains("fail / dummy"));

        // DUMMY マークが付いている行が2つある
        assertTrue(output.contains("(DUMMY)"));

        // SUMMARY 行の検証
        assertTrue(output.contains("SUMMARY"));
        assertTrue(output.contains("TOTAL=4"));
    }

    /**
     * DummyUtil がクラス／インスタンス／メソッドで
     * 正しくダミー判定できることを確認する。
     */
    @Test
    void dummyUtil_basic() throws Exception {
        // クラス判定
        assertTrue(DummyUtil.isDummy(DummyClass.class));
        assertFalse(DummyUtil.isDummy(RealClass.class));

        // インスタンス判定
        assertTrue(DummyUtil.isDummy(new DummyClass()));
        assertFalse(DummyUtil.isDummy(new RealClass()));

        // メソッド判定
        assertTrue(DummyUtil.isDummy(
                DummyClass.class.getDeclaredMethod("hello")
        ));
        assertFalse(DummyUtil.isDummy(
                RealClass.class.getDeclaredMethod("hello")
        ));

        // hasAnyDummy 判定
        assertTrue(DummyUtil.hasAnyDummy(new DummyClass(), new RealClass()));
        assertFalse(DummyUtil.hasAnyDummy(new RealClass(), new RealClass()));
    }

    /**
     * TestPrinter の ID 組み立てロジックの形式検証。
     */
    @Test
    void testPrinter_idFormat() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true);

        TestPrinter printer = new TestPrinter("XMOD", "TX1", ps);

        printer.printCase("99", "ID format test", true, new RealClass());
        printer.printSummary();

        String output = bos.toString();

        // "[XMOD:TX1-99]" が出ていること
        assertTrue(output.contains("[XMOD:TX1-99]"));

        // SUMMARY が "[XMOD:TX1]" 形式で出ていること
        assertTrue(output.contains("SUMMARY XMOD:TX1"));
    }

    /**
     * TestPrinter の caseId が null の場合のフォールバック動作。
     */
    @Test
    void testPrinter_caseIdNull() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true);

        TestPrinter printer = new TestPrinter("MOD", "TSX", ps);

        printer.printCase(null, "no caseId", true, new RealClass());
        printer.printSummary();

        String output = bos.toString();

        // caseId なしでも ID 組み立てが破綻していないこと
        assertTrue(output.contains("[MOD:TSX]"));
    }
}
