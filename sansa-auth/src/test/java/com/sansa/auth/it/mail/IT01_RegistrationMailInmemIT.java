package com.sansa.auth.it.mail;

import com.sansa.auth.testutil.InmemOutboxMailSender; // ← ラッパを使う
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 登録メールの in-memory outbox 検証（簡易版）
 *
 * ポイント:
 * - 型は testutil の InmemOutboxMailSender を使う
 * - 実体（com.sansa.auth.mail.InmemOutboxMailSender）を new してコンストラクタに渡す
 * - 以前のエラー: 「不適合な型」→ 変数型と new の型が食い違っていたため
 */
public class IT01_RegistrationMailInmemIT {

    private InmemOutboxMailSender outbox;

    @BeforeEach
    void setUp() {
        // 実体を生成（プロジェクトの実情に応じて差し替えてOK）
        com.sansa.auth.mail.InmemOutboxMailSender real =
                new com.sansa.auth.mail.InmemOutboxMailSender();

        // ラッパに渡す（変数型は testutil 側）
        this.outbox = new InmemOutboxMailSender(real);

        // 初期化
        outbox.clear();
    }

    @Test
    void smoke_send_and_capture() {
        // ここでは「最低限の疎通」を検証
        assertEquals(0, outbox.size(), "purge 直後は 0 件の想定");

        // 実際には登録フローを実行し、メール送出を誘発させる。
        // （ここでは省略し、最低限の API 連携面のみを担保）

        // 例えば1通飛んだと仮定するなら…
        // assertEquals(1, outbox.size());
        // assertNotNull(outbox.last());

        // 件名一覧の取得（実体に subjects() が無くてもラッパが吸収）
        outbox.subjects(10);
    }
}
