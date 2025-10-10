package com.sansa.auth.service;

import com.sansa.auth.dto.auth.*;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.exception.AuthExceptions.*;
import com.sansa.auth.exception.InvalidCodeException;
import com.sansa.auth.store.InmemStore;
import com.sansa.auth.util.Idx;
import com.sansa.auth.util.Timestamps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final InmemStore store = InmemStore.get();

  public PreRegisterResponse preRegister(PreRegisterRequest req) {
    String emailN = Idx.normEmail(req.getEmail());
    // レート制限やドメインブロックは簡略化（必要なら後続差し替え）
    String code = store.issueEmailCode(emailN);
    // 実際はメール送信。ここでは発行のみ。
    return PreRegisterResponse.builder()
        .success(true)
        .throttleMs(0L)
        .build();
  }

  public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) {
    String emailN = Idx.normEmail(req.getEmail());
    if (!store.verifyEmailCode(emailN, req.getCode())) {
      throw new InvalidCodeException("https://errors.sansa.dev/auth/invalid-code");
    }
    String preRegId = store.createPreReg(emailN, Instant.now().plusSeconds(10 * 60));
    long ttl = store.getPreRegTtl(preRegId, Instant.now());
    return VerifyEmailResponse.builder()
        .preRegId(preRegId)
        .expiresIn((int) ttl)
        .build();
  }

  public RegisterResponse register(RegisterRequest req) {
    String preRegId = req.getPreRegId();
    var preReg = store.consumePreReg(preRegId);
    if (preReg == null) {
      throw new GoneException("https://errors.sansa.dev/auth/expired");
    }
    String userId = store.createUser(preReg.emailNormalized(), req.getAccountId(), req.getPassword());
    return RegisterResponse.builder()
        .success(true)
        .userId(userId)
        .emailVerified(true)
        .build();
  }

  public LoginResponse login(LoginRequest req) {
    var user = store.findByIdentifier(Idx.normEmailOrNull(req.getIdentifier()), req.getIdentifier());
    if (user == null || !store.verifyPassword(user.userId(), req.getPassword())) {
      throw new UnauthorizedException("https://errors.sansa.dev/login/invalid-credentials");
    }

    // MFA 必須判定
    boolean mfaEnabled = store.isMfaRequired(user.userId());
    if (mfaEnabled) {
      return LoginResponse.builder()
          .authenticated(false)
          .mfaRequired(true)
          .mfa(LoginResponse.MfaInfo.builder()
              .factors(List.of("totp", "email_otp", "recovery"))
              .challengeId(store.issueMfaChallenge(user.userId()))
              .build())
          .amr(List.of("pwd"))
          .user(SessionInfo.UserSummary.builder()
              .userId(user.userId())
              .email(user.email())
              .displayName(user.displayName())
              .build())
          .build();
    }

    // セッション作成 & トークン発行
    var issued = Instant.now();
    var session = store.createSession(user.userId(), List.of("pwd"), issued, issued.plusSeconds(3600));
    var tokens = store.issueTokens(user.userId(), session.sessionId());
    return LoginResponse.builder()
        .authenticated(true)
        .mfaRequired(false)
        .session(Timestamps.toSessionInfo(session, user))
        .tokens(LoginTokens.builder()
            .accessToken(tokens.accessToken())
            .refreshToken(tokens.refreshToken())
            .build())
        .amr(session.amr())
        .user(SessionInfo.UserSummary.builder()
            .userId(user.userId())
            .email(user.email())
            .displayName(user.displayName())
            .build())
        .build();
  }
}
