// src/main/java/com/sansa/auth/store/Store.java
package com.sansa.auth.store;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 認証/登録/セッション/MFA/WebAuthn を永続化層で支える最小ストアI/F（サービス層からのみ利用）。
 *
 * 参照仕様:
 * - 01_ユーザー登録.md（/auth/pre-register, /auth/verify-email, /auth/register）
 * - 02_ログイン.md（/auth/token/refresh, RT再利用検知, tv）
 * - 05_セッション管理.md（/auth/session, /sessions, /auth/logout(_all)）
 * - 03_WebAuthn.md（/webauthn/*）
 * - 04_MFA.md（/auth/mfa/*）
 */
public interface Store {

    // ========== 内部レコード型（最小構成 / 実装自由） ==========
    record User(String userId, String accountId, String email, String displayName, String language,
                boolean emailVerified, int tokenVersion) {}

    record Session(String sessionId, String userId, String device, Instant issuedAt,
                   Instant lastActive, Instant expiresAt, List<String> amr) {}

    record PreReg(String preRegId, String email, Instant issuedAt, Instant expiresAt) {}

    record WebAuthnCredential(String credentialId, String userId, String nickname,
                              String aaguid, List<String> transports, long signCount,
                              Instant createdAt) {}

    // ------------------------------------------------------------
    // 01_ユーザー登録.md (/auth/pre-register, /auth/verify-email, /auth/register)
    // ------------------------------------------------------------

    /** 検証メール用のコードを発行（非同期送信はサービス側）。POST /auth/pre-register 参照 */
    String issueEmailVerificationCode(String email, Duration ttl);

    /** email+code を検証し、OKなら preReg を発行（/auth/verify-email → preRegId, expiresIn） */
    PreReg verifyEmailAndIssuePreReg(String email, String code, Duration preRegTtl, Instant now);

    /** preReg を 1回限りで消費（/auth/register 前提）。消費に成功した preReg を返す。 */
    PreReg consumePreReg(String preRegId, Instant now);

    /** accountId の重複チェック（/auth/register） */
    boolean isAccountIdTaken(String accountId);

    /** ユーザー作成（/auth/register） */
    User createUser(String accountId, String email, String displayName, String language, boolean emailVerified);

    /** 識別子（username/email 等）からユーザーを引く（ログイン/照合に利用） */
    Optional<User> findUserByIdentifier(String type, String value);

    /** userId からユーザー取得 */
    Optional<User> findUserById(String userId);

    // ------------------------------------------------------------
    // 02_ログイン.md（RT再発行 / 再利用検知, tv）
    // ------------------------------------------------------------

    /** ユーザーの token_version を取得（/auth/token/refresh, /auth/logout_all） */
    int getTokenVersion(String userId);

    /** token_version をインクリメント（全端末失効）: /auth/logout_all, RT再利用検知時 */
    int incrementTokenVersion(String userId);

    /**
     * RTローテーション管理フック。
     * - 正常ローテーション: oldRt を失効 → newRt を登録 → true
     * - 再利用検知: oldRt が既に失効/使われ済み → false を返し、サービス側で tv++ へ
     */
    boolean rotateRefreshToken(String userId, String oldRtId, String newRtId, Instant now);

    // ------------------------------------------------------------
    // 05_セッション管理.md（/auth/session, /sessions, /auth/logout, /auth/logout_all）
    // ------------------------------------------------------------

    /** 現在セッションの保存/更新（発行・最終アクセス更新など） */
    void saveOrUpdateSession(Session session);

    /** GET /auth/session 用：sessionId → Session を取得 */
    Optional<Session> findSessionById(String sessionId);

    /** GET /sessions 用：ユーザーの全セッション一覧 */
    List<Session> listSessions(String userId);

    /** DELETE /sessions/{id}：対象セッション失効（204/404想定のため戻り値なし） */
    void deleteSession(String userId, String sessionId);

    /** /auth/logout（任意の RT または sessionId 指定時にも使用） */
    void deleteSessionByRefreshToken(String userId, String refreshTokenId);

    /** /auth/logout_all（tv++ と併せて全セッション失効） */
    void deleteAllSessions(String userId);

    // ------------------------------------------------------------
    // 03_WebAuthn.md（register/options, register/verify, assertion, credentials）
    // ------------------------------------------------------------

    /** 登録検証成功後、クレデンシャルの保存（/webauthn/register/verify） */
    void saveWebAuthnCredential(WebAuthnCredential credential);

    /** 登録済みクレデンシャル一覧（/webauthn/credentials） */
    List<WebAuthnCredential> listWebAuthnCredentials(String userId);

    /** クレデンシャル失効（/webauthn/credentials/{credentialId}） */
    void deleteWebAuthnCredential(String userId, String credentialId);

    // ------------------------------------------------------------
    // 04_MFA.md（/auth/mfa/totp|email|recovery）
    // ------------------------------------------------------------

    // ----- TOTP -----
    String issueTotpSecret(String userId);                 // enroll 用
    Optional<String> getTotpSecret(String userId);
    void markTotpEnabled(String userId);                   // activate 用
    boolean verifyTotpCode(String userId, String code);    // verify 用（実装は本番ライブラリに置換）

    // ----- Email OTP -----
    void issueEmailMfaCode(String userId, Duration ttl);
    boolean verifyEmailMfaCode(String userId, String code);

    // ----- Recovery -----
    List<String> issueRecoveryCodes(String userId, int count);
    boolean consumeRecoveryCode(String userId, String code);

    // ------------------------------------------------------------
    // レート制限（pre-register / verify-email / login / token/refresh 等で使用可）
    // ------------------------------------------------------------

    /**
     * トークンバケット方式のレート制限。
     * @param key 論理キー（例: ip:xxx, email:xxx）
     * @param burst バースト最大
     * @param refillPerMinute 1分あたりの補充数
     * @return 1トークン消費できたら true
     */
    boolean tryConsumeRateLimit(String key, int burst, int refillPerMinute);
}
