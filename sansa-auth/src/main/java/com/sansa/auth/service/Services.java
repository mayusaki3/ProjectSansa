package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.dto.Dtos.LoginRequest;
import com.sansa.auth.dto.Dtos.RegisterRequest;
import com.sansa.auth.dto.Dtos.TokenPair;

import java.util.Optional;
import java.util.UUID;

public interface Services {
    AuthResult register(RegisterRequest req);
    AuthResult login(LoginRequest req);
    TokenPair  rotateTokens(String refreshJwt);
    void       logoutSession(UUID sessionId);
    void       logoutAll(UUID userId);
    Optional<UUID> verifyAccess(String accessJwt);
}
