package com.sansa.auth.service;

import java.util.Map;
import java.util.UUID;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.model.Models.User;

public interface AuthService {
    Map<String, Object> preRegister(String email);
    Map<String, Object> verifyEmail(String email, String code);
    User register(String preRegId, String accountId, String language);
    Map<String, Object> webAuthnChallenge();
    Session loginWithAssertion(String accountId);
    String signAccess(UUID userId, long tv, long ttlSec);
    Map<String, Object> logoutAll(String accountId);
}
