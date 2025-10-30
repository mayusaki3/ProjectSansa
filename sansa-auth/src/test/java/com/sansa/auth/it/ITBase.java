package com.sansa.auth.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * IT（結合テスト）共通のベースクラス。
 *
 * 背景:
 *  - JwtConfig には @Positive な TTL フィールド（accessTtlSeconds, refreshTtlSeconds）があり、
 *    デフォルト解釈が 0 になると ApplicationContext 起動が失敗する（BindValidationException）。
 *  - 過去ログにも「0 より大きな値にしてください」が出ていた。→ IT 全滅の主因。
 *
 * 対応:
 *  - テスト時だけ TTL を確実に正の値に上書きする（@TestPropertySource）。
 *  - application-it.yml 内の include 問題を避けるため、ActiveProfiles を
 *      {"it","inmem"} の複合指定にして inmem 設定を確実に取り込む。
 *
 * 注意:
 *  - issuer/secret 等、必須でデフォルトがない場合は本クラスで上書き追加するか、
 *    application-inmem.yml 側に入れておく（既に inmem に定義があれば不要）。
 */
@SpringBootTest(
    classes = com.sansa.auth.AuthApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({ "it", "inmem" })
@TestPropertySource(properties = {
    // --- JWT の TTL を @Positive を満たす値に上書き ---
    "auth.jwt.accessTtlSeconds=900",       // 15分
    "auth.jwt.refreshTtlSeconds=604800"    // 7日
    // 必要なら以下も上書き可:
    // "auth.jwt.issuer=sansa-auth-it",
    // "auth.jwt.algorithm=HS256",
    // "auth.jwt.secret=please-change-this-in-real-env"
})
public abstract class ITBase {
    // 共通ユーティリティ/前処理を置くならここに
}
