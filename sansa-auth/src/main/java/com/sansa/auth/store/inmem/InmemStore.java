// src/main/java/com/sansa/auth/store/inmem/InmemStore.java
package com.sansa.auth.store.inmem;

import com.sansa.auth.store.Store;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Profile("inmem")
public class InmemStore implements Store {

    // ====== 内部状態（開発用） ==================================================
    private final Map<String, User> users = new ConcurrentHashMap<>();                       // userId -> User
    private final Map<String, String> accountIndex = new ConcurrentHashMap<>();             // accountId -> userId
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>();               // email -> userId

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();                // sessionId -> Session
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();        // userId -> {sessionId}

    private final Map<String, Integer> tokenVersion = new ConcurrentHashMap<>();            // userId -> tv
    private final Map<String, String> refreshBinding = new ConcurrentHashMap<>();           // refreshTokenId -> userId

    private final Map<String, CodeRecord> emailVerify = new ConcurrentHashMap<>();          // email -> code,exp
    private final Map<String, PreReg> preRegs = new ConcurrentHashMap<>();                  // preRegId -> PreReg

    private final Map<String, String> totpSecret = new ConcurrentHashMap<>();               // userId -> secret
    private final Set<String> totpEnabled = ConcurrentHashMap.newKeySet();                  // userId in enabled
    private final Map<String, CodeRecord> emailMfa = new ConcurrentHashMap<>();             // userId -> code,exp
    private final Map<String, Set<String>> recovery = new ConcurrentHashMap<>();            // userId -> codes

    private final Map<String, Map<String, WebAuthnCredential>> webAuthn = new ConcurrentHashMap<>(); // userId -> (credId -> cred)

    // rate-limit: key -> bucket
    private final Map<String, RateBucket> rate = new ConcurrentHashMap<>();

    // ====== 補助型 =============================================================
    @Value private static class CodeRecord { String code; Instant expiresAt; }
    @Value private static class RateBucket { long windowStart; int tokens; }

    // ====== 01_ユーザー登録.md =================================================

    @Override
    public String issueEmailVerificationCode(String email, Duration ttl) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        Instant exp = Instant.now().plus(ttl);
        emailVerify.put(email, new CodeRecord(code, exp));
        log.info("[INMEM] verify-email code={} email={} (for dev only)", code, email);
        return code; // dev便宜上返す（本番は返さない）
    }

    @Override
    public PreReg verifyEmailAndIssuePreReg(String email, String code, Duration preRegTtl, Instant now) {
        CodeRecord r = emailVerify.get(email);
        if (r == null || now.isAfter(r.expiresAt) || !Objects.equals(r.code, code)) {
            throw new NoSuchElementException("invalid/expired email code");
        }
        String preRegId = UUID.randomUUID().toString();
        PreReg pr = new PreReg(preRegId, email, now, now.plus(preRegTtl));
        preRegs.put(preRegId, pr);
        return pr;
    }

    @Override
    public PreReg consumePreReg(String preRegId, Instant now) {
        PreReg pr = preRegs.remove(preRegId);
        if (pr == null || now.isAfter(pr.expiresAt())) {
            throw new NoSuchElementException("preReg not found/expired");
        }
        return pr;
    }

    @Override
    public boolean isAccountIdTaken(String accountId) {
        return accountIndex.containsKey(accountId);
    }

    @Override
    public User createUser(String accountId, String email, String displayName, String language, boolean emailVerified) {
        if (isAccountIdTaken(accountId)) throw new IllegalStateException("accountId taken");
        if (emailIndex.containsKey(email)) throw new IllegalStateException("email taken");
        String userId = UUID.randomUUID().toString();
        User u = new User(userId, accountId, email, displayName, language, emailVerified, 0);
        users.put(userId, u);
        accountIndex.put(accountId, userId);
        emailIndex.put(email, userId);
        tokenVersion.put(userId, 0);
        return u;
    }

    @Override
    public Optional<User> findUserByIdentifier(String type, String value) {
        return switch (type) {
            case "userId"    -> Optional.ofNullable(users.get(value));
            case "accountId" -> Optional.ofNullable(users.get(accountIndex.get(value)));
            case "email"     -> Optional.ofNullable(users.get(emailIndex.get(value)));
            default          -> Optional.empty();
        };
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    // ====== 02_ログイン.md（RT/tv） ===========================================

    @Override
    public int getTokenVersion(String userId) {
        return tokenVersion.getOrDefault(userId, 0);
    }

    @Override
    public int incrementTokenVersion(String userId) {
        return tokenVersion.merge(userId, 1, Integer::sum);
    }

    @Override
    public boolean rotateRefreshToken(String userId, String oldRtId, String newRtId, Instant now) {
        // 旧RTが未登録（または既に使われている）＝再利用検知の可能性
        if (oldRtId != null && !Objects.equals(refreshBinding.get(oldRtId), userId)) {
            return false; // サービス層で tv++ へ
        }
        if (oldRtId != null) refreshBinding.remove(oldRtId);
        refreshBinding.put(newRtId, userId);
        return true;
    }

    // ====== 05_セッション管理.md ==============================================

    @Override
    public void saveOrUpdateSession(Session s) {
        sessions.put(s.sessionId(), s);
        userSessions.computeIfAbsent(s.userId(), k -> ConcurrentHashMap.newKeySet()).add(s.sessionId());
    }

    @Override
    public Optional<Session> findSessionById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public List<Session> listSessions(String userId) {
        Set<String> ids = userSessions.getOrDefault(userId, Set.of());
        List<Session> out = new ArrayList<>(ids.size());
        for (String sid : ids) {
            Session s = sessions.get(sid);
            if (s != null) out.add(s);
        }
        return out;
    }

    @Override
    public void deleteSession(String userId, String sessionId) {
        Session s = sessions.remove(sessionId);
        if (s != null) userSessions.getOrDefault(userId, Collections.emptySet()).remove(sessionId);
    }

    @Override
    public void deleteSessionByRefreshToken(String userId, String refreshTokenId) {
        // dev簡易：RT→userId のみ管理。実セッションID紐付けはサービス層で解決して渡す想定。
        refreshBinding.remove(refreshTokenId);
    }

    @Override
    public void deleteAllSessions(String userId) {
        Set<String> ids = userSessions.remove(userId);
        if (ids != null) ids.forEach(sessions::remove);
    }

    // ====== 03_WebAuthn.md =====================================================

    @Override
    public void saveWebAuthnCredential(WebAuthnCredential c) {
        webAuthn.computeIfAbsent(c.userId(), k -> new ConcurrentHashMap<>())
                .put(c.credentialId(), c);
    }

    @Override
    public List<WebAuthnCredential> listWebAuthnCredentials(String userId) {
        Map<String, WebAuthnCredential> m = webAuthn.get(userId);
        return m == null ? List.of() : new ArrayList<>(m.values());
    }

    @Override
    public void deleteWebAuthnCredential(String userId, String credentialId) {
        Map<String, WebAuthnCredential> m = webAuthn.get(userId);
        if (m != null) m.remove(credentialId);
    }

    // ====== 04_MFA.md ==========================================================

    // ---- TOTP ----
    @Override
    public String issueTotpSecret(String userId) {
        String secret = randomBase32(32);
        totpSecret.put(userId, secret);
        return secret;
    }

    @Override
    public Optional<String> getTotpSecret(String userId) {
        return Optional.ofNullable(totpSecret.get(userId));
    }

    @Override
    public void markTotpEnabled(String userId) {
        totpEnabled.add(userId);
    }

    @Override
    public boolean verifyTotpCode(String userId, String code) {
        // dev簡易: secret の末尾6桁 = code なら OK（本番は TOTP ライブラリへ置換）
        String secret = totpSecret.get(userId);
        if (secret == null || code == null) return false;
        String expect = secret.substring(Math.max(0, secret.length() - 6));
        return code.equals(expect);
    }

    // ---- Email OTP ----
    @Override
    public void issueEmailMfaCode(String userId, Duration ttl) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        emailMfa.put(userId, new CodeRecord(code, Instant.now().plus(ttl)));
        log.info("[INMEM] email-mfa code={} userId={} (for dev only)", code, userId);
    }

    @Override
    public boolean verifyEmailMfaCode(String userId, String code) {
        CodeRecord r = emailMfa.get(userId);
        if (r == null || Instant.now().isAfter(r.expiresAt)) return false;
        boolean ok = Objects.equals(r.code, code);
        if (ok) emailMfa.remove(userId); // 1回限り
        return ok;
    }

    // ---- Recovery ----
    @Override
    public List<String> issueRecoveryCodes(String userId, int count) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < count; i++) set.add(randomBase32(12));
        recovery.put(userId, set);
        return new ArrayList<>(set);
    }

    @Override
    public boolean consumeRecoveryCode(String userId, String code) {
        Set<String> set = recovery.get(userId);
        if (set == null) return false;
        boolean ok = set.remove(code);
        if (set.isEmpty()) recovery.remove(userId);
        return ok;
    }

    // ====== Rate limit =========================================================

    @Override
    public boolean tryConsumeRateLimit(String key, int burst, int refillPerMinute) {
        long now = Instant.now().getEpochSecond();
        RateBucket b = rate.computeIfAbsent(key, k -> new RateBucket(now, burst));
        synchronized (b) {
            long elapsedSec = Math.max(0, now - b.windowStart);
            long add = (refillPerMinute * elapsedSec) / 60;
            if (add > 0) {
                int newTokens = (int)Math.min(burst, b.tokens + add);
                rate.put(key, new RateBucket(now, newTokens));
            }
            RateBucket cur = rate.get(key);
            if (cur.tokens <= 0) return false;
            rate.put(key, new RateBucket(cur.windowStart, cur.tokens - 1));
            return true;
        }
    }

    // ====== ユーティリティ =====================================================
    private static String randomBase32(int len) {
        // dev簡易: [A-Z2-7] から生成（厳密性不要）
        final char[] ALPH = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
        StringBuilder sb = new StringBuilder(len);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < len; i++) sb.append(ALPH[r.nextInt(ALPH.length)]);
        return sb.toString();
    }
}
