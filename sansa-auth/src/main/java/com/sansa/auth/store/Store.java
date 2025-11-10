package com.sansa.auth.store;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 永続層アクセス用の抽象インターフェース。
 * インメモリ実装でもRDB実装でも、このインターフェースに従う。
 *
 * ポイント:
 * - Block機能は現時点で不要なため含めない。
 * - DTO/Service層から参照される内部型もPOJOとして定義。
 */
public interface Store {

    // ----------------------------------------------------------------------
    // Pre-registration (メール認証用ワンタイムコード)
    // ----------------------------------------------------------------------

    /**
     * メールアドレスに対してプレ登録コードを発行する。
     * 実装側は同一メールへの連続リクエストに対してスロットル等を行ってもよい。
     *
     * @param email 対象メールアドレス
     * @param ttl   有効期間
     * @param now   発行基準時刻
     * @return 発行結果（コードや有効期限情報を含む）
     */
    PreReg issuePreRegCode(String email, Duration ttl, Instant now);

    /**
     * 入力されたコードを検証し、正しければ消費済みにする。
     *
     * @param email 対象メールアドレス
     * @param code  入力コード
     * @param now   検証時刻
     * @return 該当PreReg（存在しない/不一致/期限切れ時は実装側ポリシーに従いnullや例外など）
     */
    PreReg consumePreRegCode(String email, String code, Instant now);

    /**
     * preRegId から PreReg 情報を取得する。
     *
     * @param preRegId プレ登録ID
     * @return 該当PreReg（なければ null または Optional.empty() 相当の扱いは呼び出し側で定義）
     */
    PreReg findPreRegById(String preRegId);

    // ----------------------------------------------------------------------
    // User
    // ----------------------------------------------------------------------

    /**
     * メールアドレスでユーザーを検索。
     */
    User findUserByEmail(String email);

    /**
     * アカウントIDでユーザーを検索。
     */
    User findUserByAccountId(String accountId);

    /**
     * 新規ユーザーを作成。
     *
     * @param accountId    ログインID
     * @param email        メール
     * @param passwordHash パスワードハッシュ
     * @param enabled      有効フラグ
     * @return 作成されたユーザー
     */
    User createUser(String accountId, String email, String passwordHash, boolean enabled);

    // ----------------------------------------------------------------------
    // Session
    // ----------------------------------------------------------------------

    /**
     * 新規セッションを作成。
     *
     * @param userId    ユーザーID
     * @param ip        アクセス元IP
     * @param userAgent UA
     * @param now       作成時刻
     */
    Session createSession(String userId, String ip, String userAgent, Instant now);

    /**
     * ユーザーの全セッション一覧を取得。
     */
    List<Session> listSessions(String userId);

    /**
     * セッションIDで単一セッションを取得。
     */
    Optional<Session> findSessionById(String sessionId);

    /**
     * 指定セッションを無効化。
     */
    void invalidateSession(String sessionId);

    /**
     * 指定ユーザーの全セッションを無効化。
     */
    void invalidateAllSessions(String userId);

    // ======================================================================
    // 内部型: PreReg / User / Session
    // POJO(JavaBean) アクセサのみ提供。record/フィールド直参照は禁止。
    // ======================================================================

    /**
     * プレ登録（メール認証コード）情報。
     */
    class PreReg {
        private String id;
        private String email;
        private String code;
        private Instant expiresAt;
        private boolean consumed;
        /** 連続リクエスト時のクライアント向け待機時間ヒント(ms)。任意。 */
        private Long throttleMsHint;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getCode() {
            return code;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }

        public boolean isConsumed() {
            return consumed;
        }

        public Long getThrottleMsHint() {
            return throttleMsHint;
        }

        // セッタは実装都合で必要に応じて追加
        public void setId(String id) {
            this.id = id;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setExpiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
        }

        public void setConsumed(boolean consumed) {
            this.consumed = consumed;
        }

        public void setThrottleMsHint(Long throttleMsHint) {
            this.throttleMsHint = throttleMsHint;
        }
    }

    /**
     * ユーザー情報。
     */
    class User {
        private String id;
        private String accountId;
        private String email;
        private String passwordHash;
        private boolean enabled;
        private Instant createdAt;

        public String getId() {
            return id;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getEmail() {
            return email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * セッション情報。
     */
    class Session {
        private String id;
        private String userId;
        private String ip;
        private String userAgent;
        private Instant createdAt;
        private Instant lastActiveAt;
        private boolean current;

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public String getIp() {
            return ip;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getLastActiveAt() {
            return lastActiveAt;
        }

        public boolean isCurrent() {
            return current;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        public void setLastActiveAt(Instant lastActiveAt) {
            this.lastActiveAt = lastActiveAt;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }
    }
}
