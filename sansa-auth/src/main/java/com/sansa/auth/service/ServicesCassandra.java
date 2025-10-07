package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ServicesCassandra extends AuthService {  // implements → extends に修正

    private final IUserRepo userRepo;

    public ServicesCassandra(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public AuthResult preRegister(String email, String language) {
        // 実装は後続。今は疎通優先のダミー
        Map<String, Object> details = new HashMap<>();
        details.put("preRegId", UUID.randomUUID().toString());
        details.put("email", email);
        details.put("language", language);
        return AuthResult.ok("pre-register accepted", details);
    }

    @Override
    public AuthResult verifyEmail(String preRegId, String code) {
        Map<String, Object> details = new HashMap<>();
        details.put("preRegId", preRegId);
        details.put("verified", true);
        return AuthResult.ok("email verified", details);
    }

    @Override
    public AuthResult register(String preRegId, String accountId, String language) {
        // User は引数なしコンストラクタのみ → setter で設定
        User u = new User();
        u.setId(UUID.randomUUID());
        try {
            u.setAccountId(UUID.fromString(accountId));
        } catch (Exception e) {
            return AuthResult.error("invalid accountId: " + accountId);
        }
        u.setEmail("noreply@example.com");
        u.setCreatedAt(Instant.now());

        userRepo.save(u);

        Map<String, Object> details = new HashMap<>();
        details.put("userId", u.getId() != null ? u.getId().toString() : null);
        details.put("accountId", u.getAccountId() != null ? u.getAccountId().toString() : null);
        details.put("language", language);
        return AuthResult.ok("registered", details);
    }
}
