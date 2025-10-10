package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.exception.AuthExceptions.*;
import com.sansa.auth.store.InmemStore;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final InmemStore store = InmemStore.get();

  public TokenRefreshResponse refresh(TokenRefreshRequest req) {
    if (req == null || req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
      throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    }
    var res = store.rotateRefreshToken(req.getRefreshToken());
    switch (res.status()) {
      case OK -> {
        return TokenRefreshResponse.builder()
            .tokens(LoginTokens.builder()
                .accessToken(res.newAccess())
                .refreshToken(res.newRefresh())
                .build())
            .tv(res.tv())
            .build();
      }
      case EXPIRED -> throw new UnauthorizedException("https://errors.sansa.dev/token/expired");
      case REUSED -> throw new UnauthorizedException("https://errors.sansa.dev/token/reused");
      default -> throw new UnauthorizedException("https://errors.sansa.dev/token/invalid");
    }
  }
}
