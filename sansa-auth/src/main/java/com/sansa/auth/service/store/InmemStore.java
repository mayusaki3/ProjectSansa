package com.sansa.auth.service.store;

import com.sansa.auth.dto.webauthn.WebAuthnCredentialSummary;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InmemStore {

  // ===== Singleton =====
  private static final InmemStore I = new InmemStore();
  public static InmemStore get() { return I; }
  private InmemStore() {}

  // ===== Users =====
  public record User(String userId, String email, String displayName, String accountId, String passwordHash) {}

  private final Map<String, User> usersById = new ConcurrentHashMap<>();
  private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
  private final Map<String, User> usersByAccount = new ConcurrentHashMap<>();

  public String createUser(String emailN, String accountId, String rawPassword) {
    String uid = UUID.randomUUID().toString();
    User u = new User(uid, emailN, "User", accountId, rawPassword == null ? "" : "{noop}"+rawPassword);
    usersById.put(uid, u);
    usersByEmail.put(emailN, u);
    usersByAccount.put(accountId, u);
    tokenVersion.put(uid, 0);
    return uid;
  }
  public User getUser(String userId) { return usersById.get(userId); }
  public User findByIdentifier(String emailOrNull, String identifier) {
    if (emailOrNull != null) return usersByEmail.get(emailOrNull);
    return usersByAccount.get(identifier);
  }
  public boolean verifyPassword(String userId, String raw) {
    User u = usersById.get(userId);
    if (u == null) return false;
    return Objects.equals(u.passwordHash(), "{noop}"+raw);
  }

  // ===== PreReg & Email Code =====
  public record EmailCode(String email, String code, Instant expires) {}
  private final Map<String, EmailCode> emailCodes = new ConcurrentHashMap<>();
  public String issueEmailCode(String email) {
    String code = String.format("%06d", new Random().nextInt(1_000_000));
    emailCodes.put(email, new EmailCode(email, code, Instant.now().plusSeconds(600)));
    return code;
  }
  public boolean verifyEmailCode(String email, String code) {
    var ec = emailCodes.get(email);
    return ec != null && ec.code.equals(code) && ec.expires.isAfter(Instant.now());
  }

  public record PreReg(String id, String emailNormalized, Instant expiresAt, boolean consumed) {}
  private final Map<String, PreReg> preRegs = new ConcurrentHashMap<>();
  public String createPreReg(String email, Instant exp) {
    String id = UUID.randomUUID().toString();
    preRegs.put(id, new PreReg(id, email, exp, false));
    return id;
  }
  public long getPreRegTtl(String id, Instant now) {
    var pr = preRegs.get(id);
    if (pr == null) return 0;
    return Math.max(0, pr.expiresAt().getEpochSecond() - now.getEpochSecond());
  }
  public PreReg consumePreReg(String id) {
    var pr = preRegs.get(id);
    if (pr == null || pr.expiresAt.isBefore(Instant.now()) || pr.consumed) return null;
    preRegs.put(id, new PreReg(pr.id, pr.emailNormalized, pr.expiresAt, true));
    return pr;
  }

  // ===== Sessions & Tokens =====
  public record Session(String sessionId, String userId, List<String> amr, Instant issuedAt, Instant lastActive, Instant expiresAt) {}
  private final Map<String, Session> sessions = new ConcurrentHashMap<>();
  public Session createSession(String userId, List<String> amr, Instant iat, Instant exp) {
    String sid = UUID.randomUUID().toString();
    var s = new Session(sid, userId, amr, iat, iat, exp);
    sessions.put(sid, s);
    currentContext = new Context(userId, sid); // 開発用
    return s;
  }
  public Session getSession(String sessionId) { return sessions.get(sessionId); }
  public void revokeSession(String sessionId) { sessions.remove(sessionId); }
  public void revokeAllSessions(String userId) { sessions.values().removeIf(s -> s.userId.equals(userId)); }

  // token_version
  private final Map<String, Integer> tokenVersion = new ConcurrentHashMap<>();
  public int tv(String userId) { return tokenVersion.getOrDefault(userId, 0); }
  public void bumpTokenVersion(String userId) { tokenVersion.put(userId, tv(userId)+1); }

  // Access/Refresh (簡易)
  public record IssuedTokens(String accessToken, String refreshToken) {}
  private final Map<String, String> refreshUser = new ConcurrentHashMap<>();
  private final Set<String> revokedRefresh = Collections.synchronizedSet(new HashSet<>());

  public IssuedTokens issueTokens(String userId, String sessionId) {
    String at = "AT."+userId+"."+sessionId+"."+tv(userId);
    String rt = UUID.randomUUID().toString();
    refreshUser.put(rt, userId);
    return new IssuedTokens(at, rt);
  }

  public enum RotateStatus { OK, INVALID, EXPIRED, REUSED }
  public record RotateResult(RotateStatus status, String newAccess, String newRefresh, int tv) {}

  public RotateResult rotateRefreshToken(String rt) {
    var uid = refreshUser.get(rt);
    if (uid == null) return new RotateResult(RotateStatus.INVALID, null, null, 0);
    if (revokedRefresh.contains(rt)) {
      // 再利用検知 → tv++ してブロック
      bumpTokenVersion(uid);
      return new RotateResult(RotateStatus.REUSED, null, null, tv(uid));
    }
    // 正常回転：旧RT失効
    revokedRefresh.add(rt);
    String newRt = UUID.randomUUID().toString();
    refreshUser.put(newRt, uid);
    String newAt = "AT."+uid+"."+UUID.randomUUID()+"."+tv(uid);
    return new RotateResult(RotateStatus.OK, newAt, newRt, tv(uid));
  }

  public void revokeRefreshToken(String rt) {
    revokedRefresh.add(rt);
  }

  // ===== MFA（簡略）=====
  public record Challenge(String id, String userId, Instant expiresAt) {}
  private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();
  public String issueMfaChallenge(String userId) {
    String id = UUID.randomUUID().toString();
    challenges.put(id, new Challenge(id, userId, Instant.now().plusSeconds(300)));
    return id;
  }
  public Challenge consumeMfaChallenge(String id) {
    var c = challenges.remove(id);
    if (c == null || c.expiresAt.isBefore(Instant.now())) return null;
    return c;
  }
  public boolean isMfaRequired(String userId) { return mfaTotp.containsKey(userId); }

  // TOTP（ダミー：コード "000000" ならOK）
  public record TotpEnroll(String secret, String uri) {}
  private final Map<String, Boolean> mfaTotp = new ConcurrentHashMap<>();
  public TotpEnroll totpEnroll(String userId) {
    return new TotpEnroll("BASE32SECRET", "otpauth://totp/Sansa:"+getUser(userId).email()+"?secret=BASE32SECRET&issuer=Sansa");
  }
  public boolean totpActivate(String userId, String code) {
    if ("000000".equals(code)) { mfaTotp.put(userId, true); return true; }
    return false;
  }
  public boolean totpVerify(String userId, String code) {
    return "000000".equals(code) && Boolean.TRUE.equals(mfaTotp.get(userId));
  }

  // Email OTP（ダミー：常に "999999" を送る想定）
  private final Map<String, String> emailOtp = new ConcurrentHashMap<>();
  public void emailOtpSend(String userId) { emailOtp.put(userId, "999999"); }
  public boolean emailOtpVerify(String userId, String code) { return "999999".equals(code); }

  // Recovery（ダミー）
  private final Map<String, Set<String>> recovery = new ConcurrentHashMap<>();
  public java.util.List<String> recoveryIssue(String userId) {
    var list = List.of("ABCD-1234", "EFGH-5678", "IJKL-9012", "MNOP-3456", "QRST-7890",
        "UVWX-1234", "YZ12-3456", "AB78-90CD", "EF12-34GH", "JK56-78LM");
    recovery.put(userId, new HashSet<>(list));
    return list;
  }
  public boolean recoveryVerify(String userId, String code) {
    var set = recovery.getOrDefault(userId, Set.of());
    return set.remove(code);
  }

  // ===== WebAuthn（超簡略スタブ）=====
  public record WAChallenge(String userId, String challenge, String rpId) {}
  private volatile WAChallenge waChallenge;
  public WAChallenge issueWebAuthnChallenge(String userId) {
    waChallenge = new WAChallenge(userId, UUID.randomUUID().toString(), "example.com");
    return waChallenge;
  }
  public record WAOptions(String userEncoded, String challenge, String rpId) {}
  public WAOptions issueRegisterOptions(String userId) {
    return new WAOptions(Base64.getUrlEncoder().encodeToString(userId.getBytes()), UUID.randomUUID().toString(), "example.com");
  }
  public record WAVerifyResult(boolean success, String credentialId, String publicKey, String aaguid, java.util.List<String> transports, long signCount) {}
  public WAVerifyResult verifyAndSaveCredential(String userId, String clientDataJSON, String attestationObject) {
    String credId = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    creds.computeIfAbsent(userId, k -> new ArrayList<>()).add(
        WebAuthnCredentialSummary.builder()
            .credentialId(credId).aaguid("stub-aaguid").transports(List.of("usb")).signCount(0L).revoked(false).build());
    return new WAVerifyResult(true, credId, "cose-stub", "stub-aaguid", List.of("usb"), 0);
  }
  public record AssertionResult(boolean success, String userId) {}
  public AssertionResult verifyAssertion(String id, String cdj, String authData, String sig, String userHandle) {
    // 簡略：常に成功。実装時は署名検証を行う
    if (waChallenge == null) return new AssertionResult(false, null);
    return new AssertionResult(true, waChallenge.userId());
  }
  private final Map<String, List<WebAuthnCredentialSummary>> creds = new ConcurrentHashMap<>();
  public java.util.List<WebAuthnCredentialSummary> listCredentials(String userId) {
    return new ArrayList<>(creds.getOrDefault(userId, List.of()));
  }
  public boolean revokeCredential(String userId, String credentialId) {
    var list = creds.getOrDefault(userId, List.of());
    return list.removeIf(c -> c.getCredentialId().equals(credentialId));
  }

  // ===== 開発用 Context（本番はSecurityContextへ置換）=====
  public record Context(String userId, String sessionId) {}
  private volatile Context currentContext;
  public Context debugCurrentContext() { return currentContext; }
}
