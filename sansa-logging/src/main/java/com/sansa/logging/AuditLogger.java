package com.sansa.logging;

import com.sansa.logging.model.AuditRecord;

/**
 * 監査ログを出力するための抽象インターフェース。
 *
 * <p>本インターフェースは、共通スキーマに従った監査ログを
 * 永続化ストアやログ基盤へ出力する責務を持つ。</p>
 *
 * <p>出力先（Cassandra / ファイル / 外部サービスなど）や
 * 具体的なフォーマット（JSON など）は実装クラス側で決定する。</p>
 */
public interface AuditLogger {

    /**
     * 監査ログレコードを出力する。
     *
     * @param record 出力対象の監査レコード（必須）
     */
    void write(AuditRecord record);
}
