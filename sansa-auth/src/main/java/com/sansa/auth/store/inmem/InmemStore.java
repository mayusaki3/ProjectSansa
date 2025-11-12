package com.sansa.auth.store.inmem;

import com.sansa.auth.store.Store;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 開発・テスト用のインメモリ実装。
 * 非永続。最低限の一貫性とスレッドセーフ（ConcurrentHashMap）を確保。
 */
public class InmemStore implements Store {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, PreReg> preregById = new ConcurrentHashMap<>();
    private final Map<String, Session> sessionsById = new ConcurrentHashMap<>();
    private final Map<String, List<Session>> sessionsByAccount = new ConcurrentHashMap<>();
    private final Map<String, Integer> tokenVersionByAccount = new ConcurrentHashMap<>();
    private final Set<String> blockedAccountIds = ConcurrentHashMap.newKeySet();

    private final Map<String, String> totpSecretByAccount = new ConcurrentHashMap<>();
    private final Set<String> totpEnabled = ConcurrentHashMap.newKeySet();

    private final Map<String, String> emailMfaCodeByAccount = new ConcurrentHashMap<>();
    private final Map<String, Instant> emailMfaExpByAccount = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> recoveryCodesByAccount = new ConcurrentHashMap<>();

    private final Map<String, Integer> rlCounter = new ConcurrentHashMap<>();
    private final Map<String, Instant> rlRefillAt = new ConcurrentHashMap<>();

    private final Map<String, WebAuthnCredential> webauthnByCredId = new ConcurrentHashMap<>();
    private final Map<String, List<WebAuthnCredential>> webauthnByUser = new ConcurrentHashMap<>();

    @Override
    public boolean isBlockedAccountId(String accountId) {
        return blockedAccountIds.contains(accountId);
    }

    @Override
    public Optional<PreReg> findPreReg(String preRegId) {
        return Optional.ofNullable(preregById.get(preRegId));
    }

    @Override
    public void markPreRegConsumed(String preRegId, Instant consumedAt) {
        PreReg pr = preregById.get(preRegId);
        if (pr != null) {
            pr.setConsumed(true);
            pr.setExpiresAt(consumedAt);
        }
    }

    @Override
    public User createUser(String accountId, String email, String passwordHash, boolean emailVerified) {
        User u = new User();
        u.setId(UUID.randomUUID().toString());
        u.setAccountId(accountId);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setEmailVerified(emailVerified);
        u.setCreatedAt(Instant.now());
        usersById.put(u.getId(), u);
        tokenVersionByAccount.putIfAbsent(accountId, 0);
        return u;
    }

    @Override
    public int getTokenVersion(String accountId) {
        return tokenVersionByAccount.getOrDefault(accountId, 0);
    }

    @Override
    public void incrementTokenVersion(String accountId) {
        tokenVersionByAccount.merge(accountId, 1, Integer::sum);
    }

    @Override
    public Session createSession(String userId, String sessionId, String amr, Instant issuedAt) {
        Session s = new Session();
        s.setSessionId(sessionId);
        s.setUserId(userId);
        s.setAmr(amr);
        s.setIssuedAt(issuedAt);
        s.setLastActive(issuedAt);
        s.setExpiresAt(issuedAt.plusSeconds(60 * 60 * 24 * 7)); // 1週間のダミー
        sessionsById.put(sessionId, s);
        // accountId == userId とみなす or 実際のアカウント解決が必要なら適宜置換
        sessionsByAccount.computeIfAbsent(userId, k -> new ArrayList<>()).add(s);
        return s;
    }

    @Override
    public List<Session> listSessions(String accountId) {
        return new ArrayList<>(sessionsByAccount.getOrDefault(accountId, List.of()));
    }

    @Override
    public void deleteSession(String accountId, String sessionId) {
        sessionsById.remove(sessionId);
        List<Session> list = sessionsByAccount.get(accountId);
        if (list != null) list.removeIf(s -> Objects.equals(s.sessionId(), sessionId));
    }

    @Override
    public void deleteAllSessions(String accountId) {
        List<Session> list = sessionsByAccount.remove(accountId);
        if (list != null) {
            for (Session s : list) sessionsById.remove(s.sessionId());
        }
    }

    @Override
    public void revokeSession(String sessionId) {
        sessionsById.remove(sessionId);
        // accountマップからも除去
        sessionsByAccount.values().forEach(l -> l.removeIf(s -> Objects.equals(s.sessionId(), sessionId)));
    }

    @Override
    public String issueTotpSecret(String accountId) {
        String secret = UUID.randomUUID().toString().replace("-", "");
        totpSecretByAccount.put(accountId, secret);
        return secret;
    }

    @Override
    public Optional<String> getTotpSecret(String accountId) {
        return Optional.ofNullable(totpSecretByAccount.get(accountId));
    }

    @Override
    public void markTotpEnabled(String accountId) {
        totpEnabled.add(accountId);
    }

    @Override
    public String issueEmailMfaCode(String accountId, Duration ttl) {
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        emailMfaCodeByAccount.put(accountId, code);
        emailMfaExpByAccount.put(accountId, Instant.now().plus(ttl));
        return code;
    }

    @Override
    public boolean verifyEmailMfaCode(String accountId, String code) {
        String stored = emailMfaCodeByAccount.get(accountId);
        Instant exp = emailMfaExpByAccount.get(accountId);
        return stored != null && stored.equals(code) && exp != null && exp.isAfter(Instant.now());
    }

    @Override
    public List<String> issueRecoveryCodes(String accountId, int count) {
        Set<String> set = recoveryCodesByAccount.computeIfAbsent(accountId, k -> new HashSet<>());
        List<String> issued = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = UUID.randomUUID().toString().substring(0, 8);
            set.add(code);
            issued.add(code);
        }
        return issued;
    }

    @Override
    public boolean consumeRecoveryCode(String accountId, String code) {
        Set<String> set = recoveryCodesByAccount.get(accountId);
        return set != null && set.remove(code);
    }

    @Override
    public boolean tryConsumeRateLimit(String key, int capacity, int refillSeconds) {
        Instant now = Instant.now();
        rlRefillAt.putIfAbsent(key, now.plusSeconds(refillSeconds));
        rlCounter.putIfAbsent(key, capacity);

        if (now.isAfter(rlRefillAt.get(key))) {
            rlCounter.put(key, capacity);
            rlRefillAt.put(key, now.plusSeconds(refillSeconds));
        }
        int remain = rlCounter.get(key);
        if (remain <= 0) return false;
        rlCounter.put(key, remain - 1);
        return true;
    }

    @Override
    public Optional<WebAuthnCredential> findWebAuthnCredential(String credentialId) {
        return Optional.ofNullable(webauthnByCredId.get(credentialId));
    }

    @Override
    public void saveWebAuthnCredential(WebAuthnCredential cred) {
        webauthnByCredId.put(cred.getId(), cred);
        webauthnByUser.computeIfAbsent(cred.getUserId(), k -> new ArrayList<>()).add(cred);
    }

    @Override
    public void updateWebAuthnCredentialOnSign(String credentialId, long newSignCount, Instant now) {
        WebAuthnCredential c = webauthnByCredId.get(credentialId);
        if (c != null) {
            c.setSignCount(newSignCount);
            c.setLastUsedAt(now);
        }
    }

    @Override
    public List<WebAuthnCredential> listWebAuthnCredentials(String userId) {
        return new ArrayList<>(webauthnByUser.getOrDefault(userId, List.of()));
    }

    @Override
    public void deleteWebAuthnCredential(String credentialId) {
        WebAuthnCredential c = webauthnByCredId.remove(credentialId);
        if (c != null) {
            List<WebAuthnCredential> list = webauthnByUser.get(c.getUserId());
            if (list != null) list.removeIf(x -> Objects.equals(x.getId(), credentialId));
        }
    }
}
