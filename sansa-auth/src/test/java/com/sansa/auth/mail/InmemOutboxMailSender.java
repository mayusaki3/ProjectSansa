package com.sansa.auth.mail;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 【TEST-SCOPE】インメモリOutbox。以前 main にあったクラス名をテスト内で再現。
 * - プロダクションに混入させないため test 配下に配置。
 * - MailService を実装、送信された MailMessage を保持。
 * - 既存テストが呼ぶユーティリティ(purge/subjects/lastBody/waitUntilSubjectsAtLeast)も提供。
 */
public class InmemOutboxMailSender implements MailService {

    @Getter
    private final List<MailMessage> outbox = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void send(MailMessage msg) {
        outbox.add(msg);
    }

    /** 送信箱を空にする */
    public void purge() {
        outbox.clear();
    }

    /** 最新N件の件名を新しい順に返す（N件未満ならあるだけ） */
    public List<String> subjects(int limit) {
        synchronized (outbox) {
            int n = Math.min(limit, outbox.size());
            List<String> r = new ArrayList<>(n);
            for (int i = outbox.size() - 1; i >= 0 && r.size() < n; i--) {
                r.add(outbox.get(i).getSubject());
            }
            return r;
        }
    }

    /** 最後の本文を返す（無ければ空文字） */
    public String lastBody() {
        synchronized (outbox) {
            if (outbox.isEmpty()) return "";
            return outbox.get(outbox.size() - 1).getBody();
        }
    }

    /**
     * 件名の通数が minCount 以上になるまで待機。
     * @param minCount 必要件数
     * @param timeout  最大待機時間
     * @param polling  ポーリング間隔
     */
    public void waitUntilSubjectsAtLeast(int minCount, Duration timeout, Duration polling)
            throws InterruptedException {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            if (outbox.size() >= minCount) return;
            Thread.sleep(Math.max(1, polling.toMillis()));
        }
        // タイムアウト：何もしない（呼び出し側で検証失敗になる）
    }
}
