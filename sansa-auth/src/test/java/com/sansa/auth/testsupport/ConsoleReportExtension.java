package com.sansa.auth.testsupport;

import org.junit.jupiter.api.extension.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleReportExtension implements TestWatcher, BeforeAllCallback, AfterAllCallback {

  private static final AtomicInteger TOTAL  = new AtomicInteger();
  private static final AtomicInteger PASS   = new AtomicInteger();
  private static final AtomicInteger FAIL   = new AtomicInteger();
  private static volatile boolean hookInstalled = false;

  private static String idFrom(ExtensionContext ctx) {
    // @DisplayName("UT-01-001: ...") を想定。見つからなければメソッド名。
    String dn = ctx.getDisplayName();
    if (dn != null && dn.matches("^[A-Za-z]{2,}-\\d{2}-\\d{3}.*")) return dn.split(":")[0].trim();
    return ctx.getRequiredTestMethod().getName();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    // JVM 終了時に全体サマリを 1 回だけ表示
    if (!hookInstalled) {
      synchronized (ConsoleReportExtension.class) {
        if (!hookInstalled) {
          Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("==== 単体テスト 実行サマリ ====");
            System.out.printf("合計: %d, 成功: %d, 失敗: %d%n",
                TOTAL.get(), PASS.get(), FAIL.get());
          }, "console-report-shutdown"));
          hookInstalled = true;
        }
      }
    }
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    TOTAL.incrementAndGet();
    PASS.incrementAndGet();
    System.out.printf("%s : OK%n", idFrom(context));
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    TOTAL.incrementAndGet();
    FAIL.incrementAndGet();
    System.out.printf("%s : Error (%s)%n", idFrom(context), cause.getClass().getSimpleName());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    // クラスごとの小計（任意）
    System.out.printf("[Class Summary] %s -> 合計:%d 成功:%d 失敗:%d%n",
        context.getRequiredTestClass().getSimpleName(),
        TOTAL.get(), PASS.get(), FAIL.get());
  }
}
