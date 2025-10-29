// 変更点のみ（クラス全体はそのまま）。先頭付近の import とフィールドを修正
import com.sansa.auth.testutil.MailOutboxSupport;
import com.sansa.auth.mail.InmemOutboxMailSender;

// フィールド
private InmemOutboxMailSender outbox;
private MailOutboxSupport support;

// @BeforeEach 等で初期化（例）
@BeforeEach
void setUp() {
    // ここは既存の入手手段に合わせてください（DI や @Autowired など）
    // outbox = ...;
    support = new MailOutboxSupport(outbox);
}

// 以後、呼び出しを以下に置換
// purgeOutbox();          -> support.purgeOutbox();
// awaitMails(x, y);       -> support.awaitMails(x, y);
// lastMailBodyNotNull();  -> support.lastMailBodyNotNull();

// 例：
// support.purgeOutbox();
// support.awaitMails(1, 3000);
// String body = support.lastMailBodyNotNull();
