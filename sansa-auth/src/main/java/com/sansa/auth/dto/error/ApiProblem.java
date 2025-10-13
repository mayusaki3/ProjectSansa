package com.sansa.auth.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * APIエラーの汎用レスポンス（application/problem+json 準拠）。
 * 仕様: 各章の「エラーモデル（共通）」のサンプルに準拠（status/traceId/errors含む）。:contentReference[oaicite:18]{index=18}
 *
 * 備考:
 * - Spring Frameworkの ProblemDetail を使う場合は本DTOは不要。
 * - i18n: Content-Language を必ず返す（仕様記述）。:contentReference[oaicite:19]{index=19}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiProblem {

    /** エラータイプ（URL or 名前） */
    private String type;

    /** 短い説明（人間可読） */
    private String title;

    /** HTTPステータスコード（数値） */
    private Integer status;

    /** 詳細メッセージ（人間可読、ローカライズ可） */
    private String detail;

    /** 追加のエラー配列（フィールド別の理由等） */
    private List<ErrorItem> errors;

    /** トレース相関ID（サーバ側で付与） */
    private String traceId;

    /** 内部コード/リンク等（任意） */
    private String code;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorItem {
        /** エラーに関連するフィールド名（例: "code"） */
        private String field;
        /** 原因や分類（例: "expired", "mismatch"） */
        private String reason;
        /** 任意の補足 */
        private String message;
    }
}
