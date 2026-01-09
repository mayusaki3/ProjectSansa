package com.sansa.auth.spec.mail;

import com.sansa.auth.mail.MailMessage;
import com.sansa.auth.mail.InMemoryMailService;
import com.sansa.auth.testutil.InmemOutboxMailSender;
import com.sansa.auth.testutil.MailOutboxSupport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 目的:
 * - 以前のテストは InMemoryMailService のテスト補助API（clearOutbox()/snapshot()）を
 *   直接呼んでいたが、実装側のAPI差異でコンパイルエラーになっていた。
 * - 本テストでは testutil の共通ヘルパ（MailOutboxSupport + InmemOutboxMailSender）を介し、
 *   実装差を吸収する。
 *
 * 注意:
 * - 実体の outbox 取得方法はプロジェクト固有（DIやstaticアクセサなど）。
 *   ここでは「テストが実行可能な最小限」を示すための例として、
 *   反射/シングルトン等で realOutbox を取り出すフックを用意しておく。
 */
public class InMemoryMailServiceTest {

    /**
     * 実体の outbox を取得する“例”。プロジェクトの実情に合わせて差し替えてください。
     * 例:
     *   - Spring の ApplicationContext から InmemOutboxMailSender を取得
     *   - MailService の getter から outbox を取得
     *   - テスト用の静的フィールドに保持しておく など
     */
    private Object obtainRealOutboxForTest() {
        /*
        * 本テストは Spring コンテキストに依存せず、最小構成で
        * 「outbox の purge/snapshot が可能な実体」を提供する。
        *
        * InmemOutboxMailSender は、渡された実体に対して
        * clearOutbox()/snapshotOutbox() 等を（必要なら反射で）呼ぶ設計の想定。
        */
        return new InMemoryMailService();
    }

    @Test
    void smoke_snapshot_and_clear_via_support() {
        Object real = obtainRealOutboxForTest();
        assertNotNull(real, "real outbox must be provided for test");

        InmemOutboxMailSender wrapper = new InmemOutboxMailSender(real);
        MailOutboxSupport support = new MailOutboxSupport(wrapper);

        // クリア（存在すれば clear/purgeOutbox が呼ばれる）
        support.purgeOutbox();

        // 直後スナップショットは空想定（環境に依存するなら適宜調整）
        List<MailMessage> snap = support.snapshot();
        assertNotNull(snap);
        assertEquals(0, snap.size(), "outbox should be empty just after purge (adjust if your impl differs)");
    }
}
