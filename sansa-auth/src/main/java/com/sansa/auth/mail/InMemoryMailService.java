package com.sansa.auth.mail;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * inmem プロファイル向けの疑似送信。
 * - 実送信を行わず、プロセス内のOutBoxへ格納します
 * - UT/ITで内容検証に使用（テスト終了時に clearOutbox() 推奨）
 */
@Slf4j
@Service
@Profile("inmem")
public class InMemoryMailService implements MailService {

    /** 最新が末尾になる簡易OutBox（スレッドセーフ用途が強い場合は別実装に） */
    @Getter
    private static final List<MailMessage> OUTBOX = Collections.synchronizedList(new LinkedList<>());

    @Override
    public void send(MailMessage msg) {
        OUTBOX.add(msg);
        log.debug("InMemory OUTBOX appended: to={}, subject='{}' (size={})", msg.to(), msg.subject(), OUTBOX.size());
    }

    /** テストで使うユーティリティ：OutBoxのスナップショット */
    public static List<MailMessage> snapshot() {
        synchronized (OUTBOX) { return List.copyOf(OUTBOX); }
    }

    /** テストで使うユーティリティ：OutBoxのクリア */
    public static void clearOutbox() {
        synchronized (OUTBOX) { OUTBOX.clear(); }
    }
}
