package com.sansa.auth.testutil;

import com.sansa.auth.mail.MailMessage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * テスト補助ラッパー:
 * - 実体の In-memory Outbox（例: com.sansa.auth.mail.InmemOutboxMailSender）を受け取り、
 *   clear()/size()/last()/subjects(int)/snapshot() を“反射”で呼ぶことで実装差を吸収する。
 *
 * 使い方（重要）:
 *   com.sansa.auth.mail.InmemOutboxMailSender real = new com.sansa.auth.mail.InmemOutboxMailSender();
 *   InmemOutboxMailSender outbox = new InmemOutboxMailSender(real);
 *   // 以後は outbox.clear(), outbox.size(), outbox.last(), ... を呼ぶ。
 */
public class InmemOutboxMailSender {

    /** 実体（com.sansa.auth.mail.InmemOutboxMailSender 等） */
    private final Object delegate;

    public InmemOutboxMailSender(Object realOutbox) {
        if (realOutbox == null) {
            throw new IllegalArgumentException("realOutbox must not be null");
        }
        this.delegate = realOutbox;
    }

    /* ---------------- 反射ヘルパ ---------------- */

    private Object callNoArg(String methodName) {
        try {
            Method m = delegate.getClass().getMethod(methodName);
            m.setAccessible(true);
            return m.invoke(delegate);
        } catch (ReflectiveOperationException ex) {
            return null;
        }
    }

    private Object call1(String methodName, Class<?> p0, Object a0) {
        try {
            Method m = delegate.getClass().getMethod(methodName, p0);
            m.setAccessible(true);
            return m.invoke(delegate, a0);
        } catch (ReflectiveOperationException ex) {
            return null;
        }
    }

    /* --------------- bridge API --------------- */

    /** 旧 clear() / purgeOutbox() を吸収 */
    public void clear() {
        Object r = callNoArg("clear");
        if (r != null) return;
        callNoArg("purgeOutbox");
    }

    /** 件数 */
    public int size() {
        Object r = callNoArg("size");
        if (r instanceof Integer i) return i;

        Object sub = call1("subjects", int.class, Integer.MAX_VALUE);
        if (sub instanceof Collection<?> c) return c.size();

        Object snap = callNoArg("snapshot");
        if (snap instanceof Collection<?> c2) return c2.size();

        return 0;
    }

    /** 最後のメッセージ */
    public MailMessage last() {
        Object r = callNoArg("last");
        if (r instanceof MailMessage m) return m;

        Object snap = callNoArg("snapshot");
        if (snap instanceof List<?> list && !list.isEmpty()) {
            Object tail = list.get(list.size() - 1);
            if (tail instanceof MailMessage m) return m;
        }
        return null;
    }

    /** 件名一覧（反射で subject を解決） */
    @SuppressWarnings("unchecked")
    public List<String> subjects(int limit) {
        Object sub = call1("subjects", int.class, limit);
        if (sub instanceof List<?> l && (l.isEmpty() || l.get(0) instanceof String)) {
            return (List<String>) l;
        }

        Object snap = callNoArg("snapshot");
        if (snap instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof MailMessage m) {
                    out.add(extractSubject(m));
                }
            }
            return out.size() > limit ? out.subList(0, limit) : out;
        }
        return List.of();
    }

    /** スナップショット（無ければ空） */
    @SuppressWarnings("unchecked")
    public List<MailMessage> snapshot() {
        Object snap = callNoArg("snapshot");
        if (snap instanceof List<?> l && (l.isEmpty() || l.get(0) instanceof MailMessage)) {
            return (List<MailMessage>) l;
        }
        return List.of();
    }

    /* --------------- MailMessage.subject の吸収 --------------- */
    private String extractSubject(MailMessage msg) {
        // 1) subject()
        try {
            Method m = msg.getClass().getMethod("subject");
            m.setAccessible(true);
            Object v = m.invoke(msg);
            if (v instanceof String s) return s;
        } catch (ReflectiveOperationException ignore) {}

        // 2) getSubject()
        try {
            Method m = msg.getClass().getMethod("getSubject");
            m.setAccessible(true);
            Object v = m.invoke(msg);
            if (v instanceof String s) return s;
        } catch (ReflectiveOperationException ignore) {}

        // 3) public/protected フィールド subject
        try {
            Field f = msg.getClass().getDeclaredField("subject");
            f.setAccessible(true);
            Object v = f.get(msg);
            if (v instanceof String s) return s;
        } catch (ReflectiveOperationException ignore) {}

        return "(no-subject)";
        }
}
