package com.sansa.testkit.util;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 統一形式でテストケース結果を出力するユーティリティ。
 *
 * 想定出力例:
 *   ✅[M01:T01-01-01] シナリオ説明
 *   ❌[M01:T01-01-02] 別シナリオ説明 (DUMMY)
 *
 * 役割:
 * - モジュールID（例: "M01"）とテストスイートID（任意）単位でインスタンス化。
 * - テストケース結果のカウント（成功/失敗）とサマリ出力。
 * - DummyUtil と連携し、ダミー実装を対象としている場合は出力に "(DUMMY)" を付ける。
 */
public class TestPrinter {

    /** 成功行頭に付与する記号。 */
    private static final String MARK_SUCCESS = "✅";
    /** 失敗行頭に付与する記号。 */
    private static final String MARK_FAIL = "❌";
    /** ダミー対象の場合に末尾に付与する記号。 */
    private static final String MARK_DUMMY_SUFFIX = " (DUMMY)";

    private final String moduleId;
    private final String suiteId;
    private final PrintStream out;

    private final AtomicInteger successCount = new AtomicInteger();
    private final AtomicInteger failCount = new AtomicInteger();
    private final AtomicInteger totalCount = new AtomicInteger();

    /**
     * コンストラクタ。
     *
     * @param moduleId モジュールID（例: "M01"）
     * @param suiteId  スイートID（例: "T01-01"）null/空文字の場合は省略可
     * @param out      出力先（通常は System.out）
     */
    public TestPrinter(String moduleId, String suiteId, PrintStream out) {
        this.moduleId = moduleId != null ? moduleId : "";
        this.suiteId = suiteId != null ? suiteId : "";
        this.out = out != null ? out : System.out;
    }

    /**
     * モジュールIDとスイートIDを指定して TestPrinter を生成するヘルパ。
     *
     * @param moduleId モジュールID
     * @param suiteId  スイートID
     * @return TestPrinter インスタンス
     */
    public static TestPrinter of(String moduleId, String suiteId) {
        return new TestPrinter(moduleId, suiteId, System.out);
    }

    /**
     * 単一テストケースの結果を出力する。
     *
     * @param caseId      テストケースID（例: "M01:T01-01-01" や "T01-01-01"）
     * @param description シナリオ説明
     * @param success     成否
     * @param targets     判定対象（ダミーかどうかを判定するクラス/インスタンス）
     */
    public void printCase(String caseId, String description, boolean success, Object... targets) {
        boolean dummy = DummyUtil.hasAnyDummy(targets);
        printInternal(caseId, description, success, dummy);
    }

    /**
     * 単一テストケースの結果を出力する（クラス配列版）。
     *
     * @param caseId      テストケースID
     * @param description シナリオ説明
     * @param success     成否
     * @param targetTypes 判定対象（ダミーかどうかを判定するクラス）
     */
    public void printCaseWithTypes(String caseId, String description, boolean success, Class<?>... targetTypes) {
        boolean dummy = DummyUtil.hasAnyDummy(targetTypes);
        printInternal(caseId, description, success, dummy);
    }

    /**
     * 成否とダミーフラグを指定して出力する内部メソッド。
     *
     * @param caseId      テストケースID
     * @param description シナリオ説明
     * @param success     成否
     * @param dummy       ダミー対象かどうか
     */
    private void printInternal(String caseId, String description, boolean success, boolean dummy) {
        totalCount.incrementAndGet();
        if (success) {
            successCount.incrementAndGet();
        } else {
            failCount.incrementAndGet();
        }

        String prefix = success ? MARK_SUCCESS : MARK_FAIL;
        String idPart = buildIdPart(caseId);
        StringBuilder sb = new StringBuilder();

        sb.append(prefix)
          .append("[")
          .append(idPart)
          .append("] ")
          .append(description != null ? description : "");

        if (dummy) {
            sb.append(MARK_DUMMY_SUFFIX);
        }

        out.println(sb.toString());
    }

    /**
     * テストスイート終了時にサマリを出力する。
     *
     * 例:
     *   --- SUMMARY M01:T01-01: ✅=5 / ❌=0 / TOTAL=5 ---
     */
    public void printSummary() {
        String idPart = buildIdPart(null);
        out.printf(
            "--- SUMMARY %s: %s=%d / %s=%d / TOTAL=%d ---%n",
            idPart,
            MARK_SUCCESS, successCount.get(),
            MARK_FAIL, failCount.get(),
            totalCount.get()
        );
    }

    /**
     * モジュールID／スイートID／ケースID から ID 部分を組み立てる。
     *
     * @param caseId テストケースID（null の場合はモジュール+スイートのみ）
     * @return 組み立て済みID文字列
     */
    private String buildIdPart(String caseId) {
        StringBuilder sb = new StringBuilder();

        if (!moduleId.isEmpty()) {
            sb.append(moduleId);
        }
        if (!suiteId.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(suiteId);
        }
        if (caseId != null && !caseId.isEmpty()) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(caseId);
        }

        if (sb.length() == 0 && caseId != null) {
            // 最低限 caseId だけは出す
            sb.append(caseId);
        }

        return sb.toString();
    }

    /**
     * 成功件数を取得する。
     *
     * @return 成功件数
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /**
     * 失敗件数を取得する。
     *
     * @return 失敗件数
     */
    public int getFailCount() {
        return failCount.get();
    }

    /**
     * 合計テスト件数を取得する。
     *
     * @return 合計件数
     */
    public int getTotalCount() {
        return totalCount.get();
    }
}
