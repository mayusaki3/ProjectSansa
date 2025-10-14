package com.sansa.auth.testutil;

import org.junit.jupiter.api.extension.*;

import java.time.Duration;
import java.time.Instant;

/**
 * テスト実行時間を標準出力へ簡易レポートする JUnit5 Extension。
 *
 * 用途: - ローカル実行時に重いテストの目視確認を容易にするためのユーティリティ。
 *
 * 注意: - CI でのログ汚染が気になる場合は無効化できるようにしておくこと。
 */
public class ConsoleReportExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String KEY = "console-report.start";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put(KEY, Instant.now());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Instant start = context.getStore(ExtensionContext.Namespace.GLOBAL).remove(KEY, Instant.class);
        if (start != null) {
            Duration d = Duration.between(start, Instant.now());
            System.out.printf("[TEST] %s took %d ms%n",
                    context.getDisplayName(), d.toMillis());
        }
    }
}
