package com.sansa.testkit.util;

/**
 * テストケース番号を標準出力へ統一フォーマットで出力する。
 *
 * 目的:
 * - CIログでテスト仕様と突合できるようにする
 * - 直接 println を書かず、出力ルールの変更を1箇所に集約する
 *
 * 注意:
 * - 本クラスは「出力」だけを責務とし、JUnitの成功/失敗判定には関与しない
 */
public final class TestCaseReporter {

    private static final String PREFIX = "[TESTCASE] ";

    private TestCaseReporter() {
        // utility class
    }

    /**
     * テストケース番号を出力する。
     *
     * @param testCaseId テストケース番号（例: LOGGING-COMMON-TC-001）
     */
    public static void tc(String testCaseId) {
        if (testCaseId == null) {
            return;
        }
        String id = testCaseId.trim();
        if (id.isEmpty()) {
            return;
        }
        System.out.println(PREFIX + id);
    }
}
