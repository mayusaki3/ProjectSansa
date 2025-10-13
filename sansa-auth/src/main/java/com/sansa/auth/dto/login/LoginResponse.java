package com.sansa.auth.dto.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sansa.auth.dto.sessions.SessionInfo;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * /auth/login のレスポンスDTO
 *
 * 役割:
 *   - 認証結果（authenticated, mfaRequired）と、セッション/トークン/AMR/ユーザ概要/MFA情報を返す。
 *
 * 注意:
 *   - mfaRequired=true の場合、tokens や session は欠落し得る（実装側で null 許容）。
 *   - user はセッションDTOの UserSummary を再利用。
 *   - mfa は本DTO内の最小クラス MfaInfo（チャレンジ情報など）で表現。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    /** 認証が完了しているか（MFA まで完了なら true）。 */
    private boolean authenticated;

    /** 追加の MFA が必要か。 */
    private boolean mfaRequired;

    /** セッション情報（ログイン完了時）。 */
    private SessionInfo session;

    /** アクセス/リフレッシュトークン（ログイン完了時）。 */
    private LoginTokens tokens;

    /** 認証方法 (AMR: Authentication Methods References)。例: ["pwd", "otp"]. */
    private List<String> amr;

    /** ユーザー概要（セッションDTOで定義された UserSummary を再利用）。 */
    private SessionInfo.UserSummary user;

    /** MFA フロー情報（チャレンジ ID 等）。 */
    private MfaInfo mfa;

    /**
     * MFA に関する最小情報。
     * - 実装に応じてフィールドを拡張可能（チャネル、期限など）。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MfaInfo {
        /** 発行されたチャレンジID（検証APIに渡す） */
        private String challengeId;
        /** チャネル等（"totp", "email" など）。 */
        private String channel;
    }
}
