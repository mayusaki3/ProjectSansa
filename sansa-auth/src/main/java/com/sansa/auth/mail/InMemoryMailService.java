package com.sansa.auth.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * インメモリ MailService（テスト/IT/ローカル inmem 用）。
 *
 * Profile と Bean 一意性規約:
 * - 本クラスは "test","it","inmem" のみで有効
 * - "prod" では有効化されない（SmtpMailService が担当）
 *
 * 目的:
 * - MailService を DI する側で Bean が必ず 1 つに決まるようにする
 * - テストで送信メールを検証できるよう outbox を保持する
 */
@Service
@Profile({ "test", "it", "inmem" })
public class InMemoryMailService implements MailService {

    /** 送信メール（到達確認用） */
    private final CopyOnWriteArrayList<MailMessage> outbox = new CopyOnWriteArrayList<>();

    @Override
    public void send(MailMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        // 送信はせず、outbox に蓄積する
        outbox.add(message);
    }

    /**
     * 送信済みメールのスナップショットを返す。
     * テストでの検証用途。
     */
    public List<MailMessage> snapshotOutbox() {
        return List.copyOf(outbox);
    }

    /**
     * outbox をクリアする（テスト独立性の担保）。
     */
    public void clearOutbox() {
        outbox.clear();
    }

    /**
     * outbox のサイズ（簡易検証用）。
     */
    public int outboxSize() {
        return outbox.size();
    }

    /**
     * 直近送信時刻の概念が必要になった場合に備えた拡張余地（現時点では未使用）。
     * ※不要なら削除してよい。
     */
    public Instant lastSentAtOrNull() {
        // 現時点の仕様では不要（将来拡張用のダミー）
        return null;
    }
}
