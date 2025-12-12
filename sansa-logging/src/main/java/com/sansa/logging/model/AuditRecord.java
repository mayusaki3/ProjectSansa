package com.sansa.logging.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 監査ログ共通スキーマに基づくレコードモデル。
 *
 * <p>本クラスはドメイン非依存であり、eventType や userId などの
 * 共通項目のみを保持する。不変オブジェクトとして設計する。</p>
 */
public final class AuditRecord {

    /**
     * イベント種別。
     * 例: "AUTH_LOGIN", "AUTH_LOGOUT", "TOKEN_REFRESH" など。
     */
    private final String eventType;

    /**
     * 操作主体ユーザーの識別子。
     * 匿名の場合や識別不能な場合は null を許容する。
     */
    private final String userId;

    /**
     * 処理結果。
     * 共通仕様では "SUCCESS" / "FAILURE" を推奨する。
     */
    private final String result;

    /**
     * イベント発生時刻（UTC）。
     */
    private final Instant occurredAt;

    /**
     * エラーコード。
     * 成功時は null を許容する。
     */
    private final String errorCode;

    /**
     * 任意の追加情報。
     * ドメイン固有のフィールドは details に含める。
     */
    private final Map<String, Object> details;

    /**
     * コンストラクタ。
     *
     * @param eventType  イベント種別（必須）
     * @param userId     ユーザー ID（任意）
     * @param result     結果（必須）
     * @param occurredAt 発生時刻（必須）
     * @param errorCode  エラーコード（任意）
     * @param details    追加情報（null 可）
     */
    public AuditRecord(
            String eventType,
            String userId,
            String result,
            Instant occurredAt,
            String errorCode,
            Map<String, Object> details
    ) {
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.userId = userId;
        this.result = Objects.requireNonNull(result, "result must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        this.errorCode = errorCode;
        this.details = (details == null)
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(details);
    }

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public String getResult() {
        return result;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        // デバッグ用の簡易表現。監査ログ本体の出力形式は実装クラスに委ねる。
        return "AuditRecord{" +
                "eventType='" + eventType + '\'' +
                ", userId='" + userId + '\'' +
                ", result='" + result + '\'' +
                ", occurredAt=" + occurredAt +
                ", errorCode='" + errorCode + '\'' +
                ", details=" + details +
                '}';
    }
}
