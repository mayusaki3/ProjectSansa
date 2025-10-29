// package: test用ラッパー（実装差を吸収）
package com.sansa.auth.testutil;

import com.sansa.auth.mail.MailMessage;

/**
 * Test wrapper for the real outbox.
 * - 実装差( clear/purgeOutbox, last/size/snapshot 等) を反射で吸収
 * - テストコードが以前のユーティリティAPIを呼んでも落ちないようにする
 */
public class InmemOutboxMailSender {
    private final Object delegate; // com.sansa.auth.mail.InmemOutboxMailSender 実体

    public InmemOutboxMailSender(Object realOutbox) {
        this.delegate = realOutbox;
    }

    // --- helpers ------------------------------------------------------------
    private Object call(String method, Class<?>[] sig, Object... args) {
        try {
            var m = delegate.getClass().getMethod(method, sig);
            m.setAccessible(true);
            return m.invoke(delegate, args);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    private Object call(String method) { return call(method, new Class<?>[]{ }); }

    // --- bridged APIs expected by tests -------------------------------------
    /** 以前の clear() 相当。実装により clear / purgeOutbox のどちらかを呼ぶ */
    public void clear() {
        if (call("clear") == null) call("purgeOutbox");
    }

    /** 件数取得（size() がなければ subjects(Integer.MAX_VALUE).size() を推定） */
    public int size() {
        var v = call("size");
        if (v instanceof Integer i) return i;
        var subjects = call("subjects", new Class<?>[]{ int.class }, Integer.MAX_VALUE);
        if (subjects instanceof java.util.Collection<?> c) return c.size();
        return -1;
    }

    /** 末尾メッセージ取得（last() がなければ snapshot() / subjects() から推定） */
    public MailMessage last() {
        var v = call("last");
        if (v instanceof MailMessage m) return m;

        // snapshot() があれば使う
        var snap = call("snapshot");
        if (snap instanceof java.util.List<?> lst && !lst.isEmpty()) {
            var tail = lst.get(lst.size() - 1);
            if (tail instanceof MailMessage m) return m;
        }

        // subjects(n) しか無い場合は取得できないので null
        return null;
    }

    /** 件名一覧（実装に subjects(int) がある想定。なければ snapshot() から合成） */
    @SuppressWarnings("unchecked")
    public java.util.List<String> subjects(int limit) {
        var v = call("subjects", new Class<?>[]{ int.class }, limit);
        if (v instanceof java.util.List<?> l && (l.isEmpty() || l.get(0) instanceof String)) {
            return (java.util.List<String>) v;
        }
        var snap = call("snapshot");
        if (snap instanceof java.util.List<?> lst) {
            var out = new java.util.ArrayList<String>();
            for (var o : lst) {
                if (o instanceof MailMessage m) out.add(m.subject());
            }
            return out.size() > limit ? out.subList(0, limit) : out;
        }
        return java.util.List.of();
    }

    /** スナップショット（なければ空） */
    @SuppressWarnings("unchecked")
    public java.util.List<MailMessage> snapshot() {
        var v = call("snapshot");
        if (v instanceof java.util.List<?> l && (l.isEmpty() || l.get(0) instanceof MailMessage)) {
            return (java.util.List<MailMessage>) v;
        }
        return java.util.List.of();
    }
}
