package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.dto.Dtos.LoginRequest;
import com.sansa.auth.dto.Dtos.RegisterRequest;
import com.sansa.auth.dto.Dtos.TokenPair;
import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.util.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service("servicesCassandra")
public class ServicesCassandra implements Services {

    private final IUserRepo userRepo;
    private final ISessionRepo sessionRepo;
    private final JwtProvider jwt;

    public ServicesCassandra(IUserRepo userRepo, ISessionRepo sessionRepo, JwtProvider jwt) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.jwt = jwt;
    }

    @Override
    public AuthResult register(RegisterRequest req) {
        if (req.getUserId() == null || req.getUserId().isBlank()) return AuthResult.error("userId.required");
        if (req.getEmail() == null || !req.getEmail().contains("@")) return AuthResult.error("email.invalid");
        if (req.getPassword() == null || req.getPassword().length() < 8) return AuthResult.error("password.weak");

        if (userRepo.existsById(req.getUserId())) return AuthResult.error("userId.duplicate");
        if (userRepo.existsByEmail(req.getEmail())) return AuthResult.error("email.duplicate");

        String pwHash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
        UUID uid = UUID.randomUUID();
        Models.User user = new Models.User(uid, req.getUserId(), req.getEmail(), pwHash, Instant.now());
        userRepo.save(user);
        return AuthResult.ok("registered");
    }

    @Override
    public AuthResult login(LoginRequest req) {
        Optional<Models.User> u = userRepo.findByLoginId(req.getUserId());
        if (u.isEmpty()) return AuthResult.error("login.failed");
        if (!BCrypt.checkpw(req.getPassword(), u.get().getPasswordHash())) return AuthResult.error("login.failed");

        UUID sessionId = UUID.randomUUID();
        Models.Session s = new Models.Session(sessionId, u.get().id(), req.getDeviceId(), Instant.now(), null);
        sessionRepo.save(s);

        Map<String, Object> claims = Map.of("uid", u.get().id().toString(), "did", req.getDeviceId());
        String at = jwt.createAccessToken(u.get().id().toString(), claims);
        String rt = jwt.createRefreshToken(sessionId.toString(), u.get().id().toString());
        return AuthResult.ok(new TokenPair(at, rt));
    }

    @Override
    public TokenPair rotateTokens(String refreshJwt) {
        var claims = jwt.parse(refreshJwt);
        if (!"refresh".equals(claims.get("typ"))) throw new IllegalArgumentException("invalid.token");
        UUID sessionId = UUID.fromString((String) claims.get("sid"));
        Optional<Models.Session> s = sessionRepo.findById(sessionId);
        if (s.isEmpty()) throw new IllegalStateException("session.notfound");

        String userId = claims.getSubject();
        Map<String, Object> newClaims = Map.of("uid", userId, "did", s.get().getDeviceId());
        String at = jwt.createAccessToken(userId, newClaims);
        String rt = jwt.createRefreshToken(sessionId.toString(), userId);
        return new TokenPair(at, rt);
    }

    @Override
    public void logoutSession(UUID sessionId) {
        sessionRepo.delete(sessionId);
    }

    @Override
    public void logoutAll(UUID userId) {
        sessionRepo.findByUserId(userId).forEach(s -> sessionRepo.delete(s.getId()));
    }

    @Override
    public Optional<UUID> verifyAccess(String accessJwt) {
        try {
            var c = jwt.parse(accessJwt);
            return Optional.of(UUID.fromString((String) c.get("uid")));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
