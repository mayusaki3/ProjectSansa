package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.service.store.InmemStore;
import com.sansa.auth.service.util.Timestamps;
import com.sansa.auth.service.error.AuthExceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebAuthnService {

  private final InmemStore store = InmemStore.get();

  public WebAuthnChallengeResponse challenge() {
    var ctx = store.debugCurrentContext(); // 未ログインでもOKな設計にする場合はnull許容
    var ch = store.issueWebAuthnChallenge(ctx != null ? ctx.userId() : null);
    return WebAuthnChallengeResponse.builder()
        .challenge(ch.challenge())
        .rpId(ch.rpId())
        .timeout(60000)
        .userVerification("preferred")
        .build();
  }

  public LoginResponse assertion(WebAuthnAssertionRequest req) {
    var result = store.verifyAssertion(req.getId(), req.getClientDataJSON(),
        req.getAuthenticatorData(), req.getSignature(), req.getUserHandle());
    if (!result.success()) {
      throw new BadRequestException("https://errors.sansa.dev/webauthn/invalid-assertion");
    }
    // セッション発行
    var user = store.getUser(result.userId());
    var now = Instant.now();
    var session = store.createSession(user.userId(), List.of("webauthn"), now, now.plusSeconds(3600));
    var tokens = store.issueTokens(user.userId(), session.sessionId());
    return LoginResponse.builder()
        .authenticated(true)
        .mfaRequired(false)
        .session(Timestamps.toSessionInfo(session, user))
        .tokens(LoginTokens.builder().accessToken(tokens.accessToken()).refreshToken(tokens.refreshToken()).build())
        .amr(session.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(user.userId()).email(user.email()).displayName(user.displayName()).build())
        .build();
  }

  public WebAuthnRegisterOptionsResponse registerOptions() {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var opt = store.issueRegisterOptions(ctx.userId());
    return WebAuthnRegisterOptionsResponse.builder()
        .challenge(opt.challenge())
        .rpId(opt.rpId())
        .user(opt.userEncoded())
        .pubKeyCredParams(List.of(WebAuthnRegisterOptionsResponse.PubKeyCredParam
            .builder().type("public-key").alg(-7).build()))
        .attestation("none")
        .build();
  }

  public WebAuthnRegisterVerifyResponse registerVerify(WebAuthnRegisterVerifyRequest req) {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var saved = store.verifyAndSaveCredential(ctx.userId(), req.getClientDataJSON(), req.getAttestationObject());
    if (!saved.success()) throw new BadRequestException("https://errors.sansa.dev/webauthn/invalid-assertion");
    return WebAuthnRegisterVerifyResponse.builder()
        .credentialId(saved.credentialId())
        .publicKey(saved.publicKey())
        .aaguid(saved.aaguid())
        .transports(saved.transports())
        .signCount(saved.signCount())
        .build();
  }

  public WebAuthnCredentialListResponse listCredentials() {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    var list = store.listCredentials(ctx.userId());
    return WebAuthnCredentialListResponse.builder().credentials(list).build();
  }

  public void revokeCredential(String credentialId) {
    var ctx = store.debugCurrentContext();
    if (ctx == null) throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    boolean ok = store.revokeCredential(ctx.userId(), credentialId);
    if (!ok) throw new NotFoundException("https://errors.sansa.dev/sessions/session-not-found");
  }
}
