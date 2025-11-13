// File: src/main/java/com/sansa/auth/store/inmem/InmemStore.java
package com.sansa.auth.store.inmem;

import com.sansa.auth.store.Store;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InmemStore
 * 役割:
 *   - Store の最小 in-memory 実装（単体/ITテスト用、プロトタイプ用）
 *
 * 実装方針:
 *   - スレッドセーフのため ConcurrentHashMap を用いる
 *   - メールアドレスのキーは小文字化して保存
 *   - list は都度コピーして不変リストを返す（安全性を優先）
 *
 * 注意点:
 *   - 永続化ではないためプロセス終了で消える
 *   - 期限切れ自動削除等は行わない（必要に応じて呼び出し側でガベージ）
 */
public class InmemStore implements Store {

    // ====== ストレージ（簡易） ======

    // users
    private final Map<String, User> usersByAccountId = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmailLower = new ConcurrentHashMap<>();

    // pre-registrations
    private final Map<String, PreRegistration> preRegsById = new ConcurrentHashMap<>();

    // sessions
    private final Map<String, Session> sessionsById = new ConcurrentHashMap<>();
    private final Map<String, List<Session>> sessionsByAccountId = new ConcurrentHashMap<>();

    // webauthn credentials
    private final Map<String, WebAuthnCredential> credsById = new ConcurrentHashMap<>();
    private final Map<String, List<WebAuthnCredential>> credsByUserId = new ConcurrentHashMap<>();


    // ====== ユーザー関連 ======

    @Override
    public Optional<User> findUserByEmail(String email) {
        if (email == null) return Optional.empty();
        String key = normalizeEmail(email);
        return Optional.ofNullable(usersByEmailLower.get(key));
    }

    @Override
    public Optional<User> findUserByAccountId(String accountId) {
        if (accountId == null) return Optional.empty();
        return Optional.ofNullable(usersByAccountId.get(accountId));
    }

    @Override
    public User createUser(String accountId, String email, String displayName, String language, boolean admin) {
        Instant now = Instant.now();
        User u = new User(
            Objects.requireNonNull(accountId, "accountId"),
            Objects.requireNonNull(email, "email"),
            Objects.requireNonNull(displayName, "displayName"),
            Objects.requireNonNull(language, "language"),
            admin,
            now
        );
        usersByAccountId.put(u.getAccountId(), u);
        usersByEmailLower.put(normalizeEmail(u.getEmail()), u);
        return u;
    }


    // ====== プレ登録（メールコード） ======

    @Override
    public Long issuePreRegCode(String email, Duration ttl, Instant now) {
        if (email == null || ttl == null || now == null) return null;

        // preRegId は email + createdAt の組合せなど簡易生成（本番は UUID 推奨）
        String preRegId = "pr_" + normalizeEmail(email) + "_" + now.toEpochMilli();
        String lang = "ja-JP"; // デフォルト（呼び出し側が上書きする場合あり）
        String code = generateCode(email, now); // 簡易コード（本番はランダム/暗号的生成）

        Instant expiresAt = now.plus(ttl);
        PreRegistration pr = new PreRegistration(preRegId, email, lang, code, now, expiresAt);
        preRegsById.put(preRegId, pr);

        // 呼び出し側が Long の null チェックを行っているため、Long で返す
        return expiresAt.getEpochSecond();
    }

    @Override
    public Optional<PreRegistration> findPreRegById(String preRegId) {
        if (preRegId == null) return Optional.empty();
        return Optional.ofNullable(preRegsById.get(preRegId));
    }

    @Override
    public Optional<PreRegistration> consumePreRegCode(String preRegId, String code, Instant now) {
        if (preRegId == null || code == null || now == null) return Optional.empty();
        PreRegistration pr = preRegsById.get(preRegId);
        if (pr == null) return Optional.empty();
        if (pr.isExpired(now)) return Optional.empty();
        if (!Objects.equals(pr.getCode(), code)) return Optional.empty();

        // 成功時: 必要なら 1回限りにするため削除
        preRegsById.remove(preRegId);
        return Optional.of(pr);
    }


    // ====== セッション関連 ======

    @Override
    public Session createSession(String sessionId, String accountId, Instant createdAt, Instant expiresAt, String ip, String userAgent) {
        Session s = new Session(
            Objects.requireNonNull(sessionId, "sessionId"),
            Objects.requireNonNull(accountId, "accountId"),
            Objects.requireNonNull(createdAt, "createdAt"),
            Objects.requireNonNull(expiresAt, "expiresAt"),
            ip,
            userAgent,
            /* active */ true
        );
        sessionsById.put(sessionId, s);

        sessionsByAccountId.compute(accountId, (k, list) -> {
            List<Session> next = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
            next.add(s);
            return next;
        });
        return s;
    }

    @Override
    public Optional<Session> findSessionById(String sessionId) {
        if (sessionId == null) return Optional.empty();
        return Optional.ofNullable(sessionsById.get(sessionId));
    }

    @Override
    public List<Session> listSessionsByAccountId(String accountId) {
        if (accountId == null) return Collections.emptyList();
        List<Session> list = sessionsByAccountId.get(accountId);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    @Override
    public boolean deleteSessionById(String sessionId) {
        if (sessionId == null) return false;
        Session removed = sessionsById.remove(sessionId);
        if (removed == null) return false;

        sessionsByAccountId.computeIfPresent(removed.getAccountId(), (k, list) -> {
            if (list == null || list.isEmpty()) return list;
            List<Session> next = new ArrayList<>(list);
            next.removeIf(s -> Objects.equals(s.getId(), sessionId));
            return next;
        });
        return true;
    }


    // ====== WebAuthn 資格情報 ======

    @Override
    public List<WebAuthnCredential> listCredentialsByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        List<WebAuthnCredential> list = credsByUserId.get(userId);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    @Override
    public Optional<WebAuthnCredential> findCredentialById(String credentialId) {
        if (credentialId == null) return Optional.empty();
        return Optional.ofNullable(credsById.get(credentialId));
    }

    @Override
    public WebAuthnCredential createCredential(WebAuthnCredential cred) {
        Objects.requireNonNull(cred, "credential");
        credsById.put(cred.getCredentialId(), cred);
        credsByUserId.compute(cred.getUserId(), (k, list) -> {
            List<WebAuthnCredential> next = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
            next.add(cred);
            return next;
        });
        return cred;
    }

    @Override
    public boolean deleteCredentialById(String credentialId) {
        WebAuthnCredential removed = credsById.remove(credentialId);
        if (removed == null) return false;
        credsByUserId.computeIfPresent(removed.getUserId(), (k, list) -> {
            if (list == null || list.isEmpty()) return list;
            List<WebAuthnCredential> next = new ArrayList<>(list);
            next.removeIf(c -> Objects.equals(c.getCredentialId(), credentialId));
            return next;
        });
        return true;
    }


    // ====== ユーティリティ ======

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String generateCode(String email, Instant now) {
        // 簡易: 時刻 + ハッシュ断片（本番は暗号的乱数を利用）
        int h = Math.abs((email + now.toString()).hashCode());
        return String.format("%06d", h % 1_000_000);
    }
}
