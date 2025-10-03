package com.sansa.auth.service;

import com.sansa.auth.model.Models.*;
import com.sansa.auth.repo.InMemoryRepos.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;

@Service
public class Services {
    private final UserRepo users = new UserRepo();
    private final SessionRepo sessions = new SessionRepo();

    private static final byte[] SECRET = Decoders.BASE64.decode("c2Fuc2EtZGV2LXNlY3JldC1kby1ub3QtdXNlLWluLXByb2Q=");
    private final Key key = Keys.hmacShaKeyFor(Arrays.copyOf(SECRET, 32));

    private final Map<String, String> emailCodes = new HashMap<>();
    private final Map<String, Instant> emailCodeExp = new HashMap<>();

    public Map<String, Object> preRegister(String email) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        emailCodes.put(email.toLowerCase(), code);
        emailCodeExp.put(email.toLowerCase(), Instant.now().plusSeconds(600));
        return Map.of("sent", true);
    }

    public Map<String, Object> verifyEmail(String email, String code) {
        String k = email.toLowerCase();
        if (!Objects.equals(emailCodes.get(k), code) || Instant.now().isAfter(emailCodeExp.getOrDefault(k, Instant.EPOCH))) {
            throw new RuntimeException("invalid_code");
        }
        String preRegId = UUID.randomUUID().toString();
        emailCodes.put("pre:"+preRegId, k);
        return Map.of("preRegId", preRegId, "expiresIn", 600);
    }

    public User register(String preRegId, String accountId, String language) {
        String email = emailCodes.get("pre:"+preRegId);
        if (email == null) throw new RuntimeException("expired");
        if (users.findByAccountId(accountId).isPresent()) throw new RuntimeException("account_exists");
        if (users.findByEmail(email).isPresent()) throw new RuntimeException("email_exists");
        User u = new User();
        u.accountId = accountId;
        u.email = email;
        u.emailVerified = true;
        if (language != null) u.language = language;
        users.save(u);
        emailCodes.remove("pre:"+preRegId);
        return u;
    }

    public Map<String, Object> webAuthnChallenge() {
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());
        return Map.of("challenge", challenge, "rpId", "auth.sansa.local", "userVerification", "preferred", "timeout", 60000);
    }

    public Session loginWithAssertion(String accountId) {
        User u = users.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("user_not_found"));
        Session s = new Session();
        s.userId = u.userId;
        s.deviceId = "dev-" + UUID.randomUUID();
        s.tokenVersion = u.tokenVersion;
        sessions.save(s);
        return s;
    }

    public String signAccess(UUID userId, long tv, long ttlSec) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("tv", tv)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> logoutAll(String accountId) {
        User u = users.findByAccountId(accountId).orElseThrow();
        u.tokenVersion += 1;
        users.save(u);
        return Map.of("ok", true, "tokenVersion", u.tokenVersion);
    }
}
