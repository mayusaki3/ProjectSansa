package com.sansa.testkit.annotations;

import java.lang.annotation.*;

/**
 * ダミー実装を表すアノテーション。
 * 
 * 用途:
 * - InMemory 実装や、プロトタイプ段階の仮実装に付与する。
 * - TestPrinter / DummyUtil により、対象がダミーかどうかを判定する。
 *
 * 対象:
 * - クラス
 * - メソッド（クラス全体は本番だが、特定メソッドだけダミーな場合）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DummyImplementation {

    /**
     * 任意のメモ。
     * 例: "初期バージョンの仮実装"、"メール送信をログ出力に置き換え" など。
     *
     * @return ダミー実装に関する説明文字列
     */
    String value() default "";
}
