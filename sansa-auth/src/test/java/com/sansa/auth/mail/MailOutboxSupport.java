// src/test/java/com/sansa/auth/testutil/MailOutboxSupport.java
package com.sansa.auth.testutil;

import com.sansa.auth.mail.InmemOutboxMailSender;
import com.sansa.auth.mail.MailMessage;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * テストから InmemOutboxMailSender の送信結果（Outbox）を安全に参照するためのユーティリティ。
 *
 * 役割:
 *  - purge(): 送信キューをクリア
 *  - subjects(n): 直近 n 通の件名を新しい順で取得
 *  - lastBody(): 直近 1 通の本文を取得
 *  - waitUntilSubjectsAtLeast(min, timeout, poll): 件数が揃うまで待機（ポーリング）
 *
 * 依存:
 *  - InmemOutboxMailSender（テスト専用 Bean）
 *  - MailMessage（to/cc/bcc/subject/body を持つ POJO）
 *
 * 注意:
 *  - Outbox 内部実装差異に耐えるため、反射で以下候補のいずれかを探して参照します:
 *      getOutbox(), outbox(), snapshot(), messages()
 */
public final class MailOutboxSupport {

    private final InmemOutboxMailSender outbox;

    private MailOutboxSupport(InmemOutboxMailSender outbox) {
        this.outbox = outbox;
    }

    /** ApplicationContext から Bean を取得してラップする。 */
    public static MailOutboxSupport outboxOf(ApplicationContext ctx) {
        return new MailOutboxSupport(ctx.getBean(InmemOutboxMailSender.class));
    }

    /** Outbox を空にする。 */
    public void purge() {
        // InmemOutboxMailSender に purge() があれば使う。無ければリストをクリア。
        try {
            Method m = safeMethod(outbox.getClass(), "purge");
            if (m != null) {
                m.setAccessible(true);
                m.invoke(outbox);
                return;
            }
        } catch (Exception ignore) { /* fallthrough to manual clear */ }

        List<MailMessage> list = snapshot();
        list.clear(); // 取得がコピーの場合は効かないが、その場合は purge 実装がある想定
    }

    /** 直近 n 件の件名（新しい順）。 */
    public List<String> subjects(int n) {
        List<MailMessage> all = snapshot();
        List<String> result = new ArrayList<>(Math.min(n, all.size()));
        // 新しい順想定（末尾が最新）で逆順走査
        for (int i = all.size() - 1; i >= 0 && result.size() < n; i--) {
            MailMessage msg = all.get(i);
            result.add(nullSafe(msg.subject()));
        }
        return result;
    }

    /** 直近 1 通の本文。無ければ空文字。 */
    public String lastBody() {
        List<MailMessage> all = snapshot();
        if (all.isEmpty()) return "";
        MailMessage last = all.get(all.size() - 1);
        return nullSafe(last.body());
    }

    /**
     * 指定件数以上 Outbox に溜まるまで待機。
     * @param min 最低件数
     * @param timeout 最大待機時間
     * @param pollInterval ポーリング間隔
     * @return 充足したら true、timeout で false
     */
    public boolean waitUntilSubjectsAtLeast(int min, Duration timeout, Duration pollInterval) {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            if (snapshot().size() >= min) return true;
            try {
                Thread.sleep(pollInterval.toMillis());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return snapshot().size() >= min;
    }

    // ---------- internal helpers ----------

    private List<MailMessage> snapshot() {
        // 反射で outbox 内部の List<MailMessage> を安全に取得
        List<String> candidates = List.of("getOutbox", "outbox", "snapshot", "messages");
        for (String name : candidates) {
            try {
                Method m = safeMethod(outbox.getClass(), name);
                if (m == null) continue;
                m.setAccessible(true);
                Object val = m.invoke(outbox);
                if (val instanceof List<?> raw) {
                    // 型安全にコピーして返す
                    List<MailMessage> casted = new ArrayList<>();
                    for (Object o : raw) {
                        if (o instanceof MailMessage mm) casted.add(mm);
                    }
                    return casted;
                }
            } catch (Exception ignore) { /* try next */ }
        }
        // 取得不能なら空リスト
        return new ArrayList<>();
    }

    private static Method safeMethod(Class<?> type, String name, Class<?>... params) {
        try {
            return type.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            try {
                return type.getMethod(name, params);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
