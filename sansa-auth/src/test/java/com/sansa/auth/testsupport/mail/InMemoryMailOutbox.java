package com.sansa.auth.testutil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * inmem用の“擬似送信箱(Outbox)”。
 * - アプリが inmem プロファイルでメール送信時にここへ積む想定
 * - まだ配線されていない場合でも、テストはコンパイル可
 *
 * 提供API（ITで使用）:
 *  - purge(): 全削除
 *  - size(): 件数
 *  - subjects(n): 直近n件の件名配列
 *  - lastBody(): 直近の本文
 *  - waitUntilSubjectsAtLeast(min, timeout, poll): 件数がmin以上になるまで待機
 *  - append(msg): （暫定）配線前でもテストが動かせるよう直接積めるフック
 */
@Component
public class InMemoryMailOutbox {

    private final List<MailMessage> store = Collections.synchronizedList(new ArrayList<>());

    public void purge() {
        store.clear();
    }

    public int size() {
        return store.size();
    }

    public List<String> subjects(int n) {
        synchronized (store) {
            int from = Math.max(0, store.size() - n);
            List<String> out = new ArrayList<>();
            for (int i = from; i < store.size(); i++) {
                out.add(store.get(i).getSubject());
            }
            return out;
        }
    }

    public String lastBody() {
        synchronized (store) {
            if (store.isEmpty()) return null;
            return store.get(store.size() - 1).getBody();
        }
    }

    public MailMessage last() {
        synchronized (store) {
            if (store.isEmpty()) return null;
            return store.get(store.size() - 1);
        }
    }

    public void append(MailMessage msg) {
        store.add(msg);
    }

    /**
     * 件数がmin以上になるまでポーリングで待機。
     * 外部SMTPではなくアプリの非同期送信でも待てるように用意。
     */
    public void waitUntilSubjectsAtLeast(int min, Duration timeout, Duration poll) throws InterruptedException {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            if (size() >= min) return;
            Thread.sleep(poll.toMillis());
        }
        throw new AssertionError("Outbox subjects did not reach " + min + " within " + timeout);
    }

    /** 本文に6桁コードが含まれるか簡易検証（ユーティリティ） */
    public static boolean containsSixDigits(String text) {
        if (text == null) return false;
        return Pattern.compile("\\b\\d{6}\\b").matcher(text).find();
    }
}
