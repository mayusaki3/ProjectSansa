package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.mfa.*;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.exception.AuthExceptions.*;
import com.sansa.auth.store.InmemStore;
import com.sansa.auth.util.Timestamps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MfaService {

  private final InmemStore store = InmemStore.get();

  // --- TOTP ---
  public MfaTotpEnrollResponse totpEnroll() {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var r = store.totpEnroll(ctx.userId());
    return MfaTotpEnrollResponse.builder().secret(r.secret()).uri(r.uri()).build();
  }

  public void totpActivate(MfaTotpActivateRequest req) {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    if (!store.totpActivate(ctx.userId(), req.getCode())) {
      throw new BadRequestException("https://errors.sansa.dev/mfa/invalid-code");
    }
  }

  public LoginResponse totpVerify(MfaTotpVerifyRequest req) {
    var ch = store.consumeMfaChallenge(req.getChallengeId());
    if (ch == null || !store.totpVerify(ch.userId(), req.getCode())) {
      throw new BadRequestException("https://errors.sansa.dev/mfa/invalid-code");
    }
    // 認証確定：セッション/トークン
    var user = store.getUser(ch.userId());
    var now = Instant.now();
    var s = store.createSession(user.userId(), List.of("mfa"), now, now.plusSeconds(3600));
    var t = store.issueTokens(user.userId(), s.sessionId());
    return LoginResponse.builder()
        .authenticated(true).mfaRequired(false)
        .session(Timestamps.toSessionInfo(s, user))
        .tokens(LoginTokens.builder().accessToken(t.accessToken()).refreshToken(t.refreshToken()).build())
        .amr(s.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(user.userId()).email(user.email()).displayName(user.displayName()).build())
        .build();
  }

  // --- Email OTP ---
  public void emailSend(MfaEmailSendRequest req) {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    store.emailOtpSend(ctx.userId());
  }

  public LoginResponse emailVerify(MfaEmailVerifyRequest req) {
    var ch = store.consumeMfaChallenge(req.getChallengeId());
    if (ch == null || !store.emailOtpVerify(ch.userId(), req.getCode())) {
      throw new BadRequestException("https://errors.sansa.dev/mfa/invalid-code");
    }
    var user = store.getUser(ch.userId());
    var now = Instant.now();
    var s = store.createSession(user.userId(), List.of("mfa"), now, now.plusSeconds(3600));
    var t = store.issueTokens(user.userId(), s.sessionId());
    return LoginResponse.builder()
        .authenticated(true).mfaRequired(false)
        .session(Timestamps.toSessionInfo(s, user))
        .tokens(LoginTokens.builder().accessToken(t.accessToken()).refreshToken(t.refreshToken()).build())
        .amr(s.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(user.userId()).email(user.email()).displayName(user.displayName()).build())
        .build();
  }

  // --- Recovery ---
  public MfaRecoveryIssueResponse recoveryIssue() {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var codes = store.recoveryIssue(ctx.userId());
    return MfaRecoveryIssueResponse.builder().recoveryCodes(codes).build();
  }

  public LoginResponse recoveryVerify(MfaRecoveryVerifyRequest req) {
    var ch = store.consumeMfaChallenge(req.getChallengeId());
    if (ch == null || !store.recoveryVerify(ch.userId(), req.getCode())) {
      throw new BadRequestException("https://errors.sansa.dev/mfa/invalid-code");
    }
    var user = store.getUser(ch.userId());
    var now = Instant.now();
    var s = store.createSession(user.userId(), List.of("mfa"), now, now.plusSeconds(3600));
    var t = store.issueTokens(user.userId(), s.sessionId());
    return LoginResponse.builder()
        .authenticated(true).mfaRequired(false)
        .session(Timestamps.toSessionInfo(s, user))
        .tokens(LoginTokens.builder().accessToken(t.accessToken()).refreshToken(t.refreshToken()).build())
        .amr(s.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(user.userId()).email(user.email()).displayName(user.displayName()).build())
        .build();
  }
}
