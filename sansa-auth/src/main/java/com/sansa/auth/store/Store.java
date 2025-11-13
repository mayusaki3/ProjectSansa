// File: src/main/java/com/sansa/auth/store/Store.java
package com.sansa.auth.store;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Store
 * 役割:
 *   - 認証・セッション・WebAuthn(一部)に必要な最小限の永続化I/Fを定義する。
 *   - 実装は in-memory / DB 実装で差し替え可能にする。
 *
 * 注意点:
 *   - 呼び出し側（service 層）の既存利用に合わせ、メソッド名・引数・戻り値を合わせている。
 *   - ネスト型はインタフェース内の public static クラスとして定義する（実装間で共通の最小DTO）。
 */
public interface Store {

    // ===== ユーザー関連 =====

    /**
     * メールアドレスでユーザーを検索（大小無視の正規化は実装側で行うこと）
     * @param email メールアドレス
     * @return 見つかれば User、なければ Optional.empty()
     */
    Optional<User> findUserByEmail(String email);

    /**
     * アカウントIDでユーザーを検索
     * @param accountId アカウントID
     * @return 見つかれば User、なければ Optional.empty()
     */
    Optional<User> findUserByAccountId(String accountId);

    /**
     * ユーザー作成
     * @param accountId 付与済みアカウントID（UUID等）
     * @param email メール
     * @param displayName 表示名
     * @param language 言語コード
     * @param admin 管理者フラグ
     * @return 作成された User
     */
    User createUser(String accountId, String email, String displayName, String language, boolean admin);


    // ===== プレ登録（メールコード）関連 =====

    /**
     * 期限付きのプレ登録コードを発行し、期限（Epoch秒）を返す。
     * 呼び出し側が null チェックをしているため、戻り値は Long（null 許容）とする。
     *
     * @param email 対象メール
     * @param ttl   有効期間
     * @param now   基準時刻
     * @return 失効Epoch秒。失敗時は null を返し得る（呼び出し側の実装に合わせるため）
     */
    Long issuePreRegCode(String email, Duration ttl, Instant now);

    /**
     * preRegId でプレ登録情報を検索
     * @param preRegId 事前登録ID
     * @return 見つかれば PreRegistration
     */
    Optional<PreRegistration> findPreRegById(String preRegId);

    /**
     * コード消費（一致・期限内確認を含む）
     * @param preRegId 事前登録ID
     * @param code     メールコード
     * @param now      検証時刻
     * @return 成功時は PreRegistration、失敗時は Optional.empty()
     */
    Optional<PreRegistration> consumePreRegCode(String preRegId, String code, Instant now);


    // ===== セッション関連 =====

    /**
     * セッション作成
     * @param sessionId   セッションID（外部で生成済み文字列）
     * @param accountId   紐付けアカウントID
     * @param createdAt   作成時刻
     * @param expiresAt   失効時刻
     * @param ip          生成時IP
     * @param userAgent   生成時UA
     * @return 作成された Session
     */
    Session createSession(String sessionId, String accountId, Instant createdAt, Instant expiresAt, String ip, String userAgent);

    /**
     * セッションIDで検索
     * @param sessionId セッションID
     * @return 見つかれば Session
     */
    Optional<Session> findSessionById(String sessionId);

    /**
     * アカウントIDに紐づくセッション一覧（新しい順などの並びは実装依存）
     * @param accountId アカウントID
     * @return セッション一覧（なければ空リスト）
     */
    List<Session> listSessionsByAccountId(String accountId);

    /**
     * セッションIDで削除
     * @param sessionId セッションID
     * @return 削除できた場合 true
     */
    boolean deleteSessionById(String sessionId);


    // ===== WebAuthn 資格情報 =====

    /**
     * ユーザーに紐づくWebAuthn資格情報一覧
     * @param userId ユーザーID（=accountId を想定）
     * @return 資格情報一覧
     */
    List<WebAuthnCredential> listCredentialsByUserId(String userId);

    /**
     * 資格情報IDで検索
     * @param credentialId 資格情報ID
     * @return 見つかれば WebAuthnCredential
     */
    Optional<WebAuthnCredential> findCredentialById(String credentialId);

    /**
     * 資格情報の作成
     * @param cred 追加する資格情報
     * @return 永続化後の資格情報
     */
    WebAuthnCredential createCredential(WebAuthnCredential cred);

    /**
     * 資格情報をIDで削除
     * @param credentialId 資格情報ID
     * @return 削除成否
     */
    boolean deleteCredentialById(String credentialId);


    // ========================
    // ネストDTO（最小限フィールド）
    // ========================

    /**
     * User: 認証対象ユーザー
     */
    public static class User {
        private final String accountId;
        private final String email;
        private final String displayName;
        private final String language;
        private final boolean admin;
        private final Instant createdAt;

        public User(String accountId, String email, String displayName, String language, boolean admin, Instant createdAt) {
            this.accountId = Objects.requireNonNull(accountId);
            this.email = Objects.requireNonNull(email);
            this.displayName = Objects.requireNonNull(displayName);
            this.language = Objects.requireNonNull(language);
            this.admin = admin;
            this.createdAt = Objects.requireNonNull(createdAt);
        }

        /** アカウントID（サービス層の findUserByAccountId で使用） */
        public String getAccountId() { return accountId; }
        public String getEmail() { return email; }
        public String getDisplayName() { return displayName; }
        public String getLanguage() { return language; }
        public boolean isAdmin() { return admin; }
        public Instant getCreatedAt() { return createdAt; }
    }

    /**
     * PreRegistration: メールコード認証の事前登録情報
     */
    public static class PreRegistration {
        private final String id;
        private final String email;
        private final String language;
        private final String code;
        private final Instant createdAt;
        private final Instant expiresAt;

        public PreRegistration(String id, String email, String language, String code, Instant createdAt, Instant expiresAt) {
            this.id = Objects.requireNonNull(id);
            this.email = Objects.requireNonNull(email);
            this.language = Objects.requireNonNull(language);
            this.code = Objects.requireNonNull(code);
            this.createdAt = Objects.requireNonNull(createdAt);
            this.expiresAt = Objects.requireNonNull(expiresAt);
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getLanguage() { return language; }
        public String getCode() { return code; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getExpiresAt() { return expiresAt; }

        /** 便宜メソッド: 期限切れ判定 */
        public boolean isExpired(Instant now) {
            return now.isAfter(expiresAt) || now.equals(expiresAt);
        }
    }

    /**
     * Session: ログインセッション
     */
    public static class Session {
        private final String id;
        private final String accountId;
        private final Instant createdAt;
        private final Instant expiresAt;
        private final String ip;
        private final String userAgent;
        private final boolean active;

        public Session(String id, String accountId, Instant createdAt, Instant expiresAt, String ip, String userAgent, boolean active) {
            this.id = Objects.requireNonNull(id);
            this.accountId = Objects.requireNonNull(accountId);
            this.createdAt = Objects.requireNonNull(createdAt);
            this.expiresAt = Objects.requireNonNull(expiresAt);
            this.ip = ip;
            this.userAgent = userAgent;
            this.active = active;
        }

        public String getId() { return id; }
        public String getAccountId() { return accountId; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public String getIp() { return ip; }
        public String getUserAgent() { return userAgent; }
        public boolean isActive() { return active; }
    }

    /**
     * WebAuthnCredential: WebAuthn 資格情報（登録済みクレデンシャル）
     *
     * 呼び出し側の利用に合わせた命名アクセサを用意する。
     * 例）getCredentialId(), getNewSignCount()
     */
    public static class WebAuthnCredential {
        private final String id;
        private final String userId;
        private final String name;
        private final long signCount;
        private final Instant createdAt;

        public WebAuthnCredential(String id, String userId, String name, long signCount, Instant createdAt) {
            this.id = Objects.requireNonNull(id);
            this.userId = Objects.requireNonNull(userId);
            this.name = Objects.requireNonNull(name);
            this.signCount = signCount;
            this.createdAt = Objects.requireNonNull(createdAt);
        }

        /** 呼び出し側が getCredentialId() を使用しているため、別名アクセサを提供 */
        public String getCredentialId() { return id; }

        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getName() { return name; }

        /** 呼び出し側が getNewSignCount() を使用しているため、別名アクセサを提供 */
        public long getNewSignCount() { return signCount; }

        public long getSignCount() { return signCount; }
        public Instant getCreatedAt() { return createdAt; }
    }
}
