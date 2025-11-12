package com.sansa.auth.store;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 認証ドメインの永続化インタフェース。
 * サービス層（AuthServiceImpl / MfaServiceImpl / SessionServiceImpl / WebAuthnServiceImpl）
 * から呼ばれるメソッド群を集約する。
 *
 * 重要: 既存実装が呼ぶシグネチャを全て定義してある（過不足でコンパイルが壊れないようにする）
 */
public interface Store {

    // ===== User / PreReg =====

    /** 重複 / ブラックリスト用途 */
    boolean isBlockedAccountId(String accountId);

    /** 事前登録参照 */
    Optional<PreReg> findPreReg(String preRegId);

    /** 事前登録の消費マーク */
    void markPreRegConsumed(String preRegId, Instant consumedAt);

    /** ユーザー生成（AuthServiceImpl から呼ばれる4引数版に統一） */
    User createUser(String accountId, String email, String passwordHash, boolean emailVerified);

    /** トークンバージョン参照／更新（全失効用） */
    int getTokenVersion(String accountId);
    void incrementTokenVersion(String accountId);

    // ===== Session =====

    /** セッション作成（AuthServiceImpl, TokenFacadeImpl から参照される4引数版） */
    Session createSession(String userId, String sessionId, String amr, Instant issuedAt);

    /** セッション列挙／削除 */
    List<Session> listSessions(String accountId);
    void deleteSession(String accountId, String sessionId);

    /** アカウント配下のセッション全削除（ログアウトオール） */
    void deleteAllSessions(String accountId);

    /** セッション失効（ID単位）。古い実装が revokeSession(sessionId, now) を呼ぶためオーバーロードも持つ */
    void revokeSession(String sessionId);
    default void revokeSession(String sessionId, Instant ignored) { revokeSession(sessionId); }

    // ===== MFA: TOTP / Email / Recovery =====

    /** TOTP 秘密鍵の新規払い出し */
    String issueTotpSecret(String accountId);

    /** 保存済み TOTP 秘密鍵の取得 */
    Optional<String> getTotpSecret(String accountId);

    /** TOTP 有効化フラグの設定 */
    void markTotpEnabled(String accountId);

    /** メールMFAコードの発行／検証 */
    String issueEmailMfaCode(String accountId, Duration ttl);
    boolean verifyEmailMfaCode(String accountId, String code);

    /** リカバリコードの発行／消費 */
    List<String> issueRecoveryCodes(String accountId, int count);
    boolean consumeRecoveryCode(String accountId, String code);

    /** レートリミット用トークンバケット的カウンタ */
    boolean tryConsumeRateLimit(String key, int capacity, int refillSeconds);

    // ===== WebAuthn =====

    Optional<WebAuthnCredential> findWebAuthnCredential(String credentialId);
    void saveWebAuthnCredential(WebAuthnCredential cred);
    void updateWebAuthnCredentialOnSign(String credentialId, long newSignCount, Instant now);
    List<WebAuthnCredential> listWebAuthnCredentials(String userId);
    void deleteWebAuthnCredential(String credentialId);

    // ====== 内部モデル ======

    class User {
        private String id;
        private String accountId;
        private String email;
        private String passwordHash;
        private boolean emailVerified;
        private Instant createdAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public class PreReg {
        private String id;
        private String email;
        private String accountId;
        private Instant expiresAt;
        private boolean consumed;
        private long throttleMs;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

        public boolean isConsumed() { return consumed; }
        public void setConsumed(boolean consumed) { this.consumed = consumed; }

        public long getThrottleMs() { return throttleMs; }
        public void setThrottleMs(long throttleMs) { this.throttleMs = throttleMs; }
    }

    public class Session {
        private String sessionId;
        private String userId;
        private String amr;
        private Instant issuedAt;
        private Instant lastActive;
        private Instant expiresAt;

        public String sessionId() { return sessionId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String userId() { return userId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String amr() { return amr; }
        public String getAmr() { return amr; }
        public void setAmr(String amr) { this.amr = amr; }

        public Instant issuedAt() { return issuedAt; }
        public Instant getIssuedAt() { return issuedAt; }
        public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

        public Instant lastActive() { return lastActive; }
        public Instant getLastActive() { return lastActive; }
        public void setLastActive(Instant lastActive) { this.lastActive = lastActive; }

        public Instant expiresAt() { return expiresAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    }

    public class WebAuthnCredential {
        private String id;         // credentialId
        private String userId;
        private String name;       // 表示名
        private String publicKey;
        private long signCount;
        private Instant createdAt;
        private Instant lastUsedAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

        public long getSignCount() { return signCount; }
        public void setSignCount(long signCount) { this.signCount = signCount; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

        public Instant getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    }
}
