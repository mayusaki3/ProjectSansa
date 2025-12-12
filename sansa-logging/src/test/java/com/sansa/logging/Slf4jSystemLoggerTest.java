package com.sansa.logging;

import com.sansa.logging.impl.Slf4jSystemLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

/**
 * T03: Slf4jSystemLogger の委譲挙動に関する単体テスト。
 */
class Slf4jSystemLoggerTest {

    // T03-01: info は SLF4J Logger の info に委譲される
    @Test
    void T03_01_shouldDelegateInfoToSlf4jLogger() {
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);

        Slf4jSystemLogger logger = new Slf4jSystemLogger(mockLogger);

        logger.info("hello {}", "world");

        // Object... オーバーロードを選ばせるため、(Object) any() として明示
        verify(mockLogger, times(1)).info(anyString(), (Object) any());
    }

    // T03-02: warn は SLF4J Logger の warn に委譲される
    @Test
    void T03_02_shouldDelegateWarnToSlf4jLogger() {
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isWarnEnabled()).thenReturn(true);

        Slf4jSystemLogger logger = new Slf4jSystemLogger(mockLogger);

        logger.warn("warn {}", 1);

        // こちらも同様に (Object) any() で曖昧性を避ける
        verify(mockLogger, times(1)).warn(anyString(), (Object) any());
    }

    // T03-03: error は Throwable 付きで SLF4J Logger の error に委譲される
    @Test
    void T03_03_shouldDelegateErrorWithThrowable() {
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isErrorEnabled()).thenReturn(true);

        Slf4jSystemLogger logger = new Slf4jSystemLogger(mockLogger);

        RuntimeException ex = new RuntimeException("boom");
        logger.error("error occurred", ex);

        // error についてはメッセージと Throwable の対応を厳密に確認
        verify(mockLogger, times(1)).error(eq("error occurred"), same(ex));
    }
}
