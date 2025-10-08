package com.sansa.auth.service;

import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Profile("prod") // 本番で Cassandra を使うなら
public class ServicesCassandra implements AuthService {

    private final RepoInterfaces.IPreRegRepo preRegRepo;
    private final RepoInterfaces.IUserRepo userRepo;

    public ServicesCassandra(RepoInterfaces.IPreRegRepo preRegRepo,
                             RepoInterfaces.IUserRepo userRepo) {
        this.preRegRepo = preRegRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Map<String, Object> preRegister(String email, String language) {
        Models.PreReg pr = new Models.PreReg();
        pr.setId(UUID.randomUUID().toString());
        pr.setEmail(email);
        pr.setLanguage(language);
        pr.setCreatedAt(Instant.now());
        preRegRepo.save(pr);

        return ok("pre-registered", Map.of("preRegId", pr.getId()));
    }

    @Override
    public Map<String, Object> verifyEmail(String email, String code) {
        return ok("verified", Map.of("email", email));
    }

    @Override
    public Map<String, Object> register(String preRegId, String language) {
        var prOpt = preRegRepo.findById(preRegId);
        if (prOpt.isEmpty()) {
            return ng("preReg not found");
        }
        var pr = prOpt.get();

        Models.User u = new Models.User();
        u.setId(UUID.randomUUID().toString());
        u.setAccountId(UUID.randomUUID().toString());
        u.setEmail(pr.getEmail());
        u.setCreatedAt(Instant.now());
        userRepo.save(u);

        return ok("registered", Map.of("user", toUserMap(u)));
    }

    /* ===== Helper (同上) ===== */

    private Map<String, Object> ok(String message, Map<String, Object> details) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", message);
        res.put("details", details != null ? details : Map.of());
        return res;
    }

    private Map<String, Object> ng(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", message);
        res.put("details", Map.of());
        return res;
    }

    private Map<String, Object> toUserMap(Models.User u) {
        return Map.of(
            "id", u.getId(),
            "accountId", u.getAccountId(),
            "email", u.getEmail(),
            "createdAt", u.getCreatedAt()
        );
    }
}
