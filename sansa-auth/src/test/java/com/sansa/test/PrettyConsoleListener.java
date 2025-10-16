package com.sansa.test;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PrettyConsoleListener
 *
 * <p>JUnit Platform の TestExecutionListener 実装。
 * 各テストメソッドの終了時に「✅/❌ + 表示名」をコンソールへ出力し、
 * すべてのテスト完了後に「実行/成功/失敗」件数のサマリを出力する。</p>
 *
 * <p>表示名は @DisplayName を優先し、未指定の場合は JUnit の displayName
 * （多くはメソッド名）を用いる。UT 番号（例: UT-01-001）を確実に表示したい場合は
 * 各テストメソッドに @DisplayName を付与すること。</p>
 *
 * <p>適用範囲: Surefire（mvn test）/ Failsafe（mvn verify の IT）いずれも、
 * テストクラスパスに本リスナーが存在し、SPI 登録されていれば有効。</p>
 */
public class PrettyConsoleListener implements TestExecutionListener {

    /** 個別結果を保持する軽量レコード（名称と成功フラグのみ） */
    private static final class Item {
        final String name;       // 例: "UT-01-001"
        final boolean success;   // 成功なら true, 失敗なら false
        Item(String name, boolean success) {
        this.name = name;
        this.success = success;
        }
    }

    /** 全テストの逐次結果を保持（必要に応じて後処理したい場合に利用） */
    private final List<Item> results = new ArrayList<>();

    /** サマリ用カウンタ */
    private int total = 0;
    private int passed = 0;
    private int failed = 0;

    /**
     * テスト表示名を決定する。
     * 1) @DisplayName があればそれを使う
     * 2) なければ JUnit が持つ displayName（MethodSource の場合は概ねメソッド名）
     */
    private static String displayName(TestIdentifier id) {
        String dn = id.getDisplayName();           // @DisplayName または JUnit 既定の表示名
        TestSource src = id.getSource().orElse(null);

        // メソッド由来のテストのみ丁寧に拾う（クラス/コンテナには触れない）
        if (src instanceof MethodSource) {
        return (dn != null) ? dn : ((MethodSource) src).getMethodName();
        }
        return dn;
    }

    /**
     * 各テスト（メソッド）終了時に呼ばれる。
     * 成否に応じて ✅/❌ を出力し、件数をカウントする。
     */
    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (!id.isTest()) return; // クラスやコンテナ（isContainer）は対象外

        total++;
        boolean ok = (result.getStatus() == TestExecutionResult.Status.SUCCESSFUL);
        if (ok) {
        passed++;
        } else {
        failed++;
        }

        String name = displayName(id);
        results.add(new Item(name, ok));

        // 逐次出力: 「✅ UT-01-001」や「❌ UT-01-003」の形式
        System.out.println((ok ? "✅ " : "❌ ") + name);
    }

    /**
     * すべてのテスト計画が完了したあとに呼ばれる。
     * 最後にサマリ行を 1 行出力する。
     */
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        System.out.printf("実行 %d 件、成功 %d 件、失敗 %d 件%n", total, passed, failed);
    }
}
