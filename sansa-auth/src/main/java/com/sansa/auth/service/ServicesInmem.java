package com.sansa.auth.service;

import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServicesInmem implements AuthService {

    private final RepoInterfaces.IPreRegRepo preRegRepo;
    private final RepoInterfaces.IUserRepo userRepo;

    public ServicesInmem(RepoInterfaces.IPreRegRepo preRegRepo,
                         RepoInterfaces.IUserRepo userRepo) {
        this.preRegRepo = preRegRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Map<String, Object> preRegister(String email, String language) {
        // 簡易に PreReg を保存
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
        // 疎通用: 何でも OK にする（本実装ではコード検証を入れる）
        return ok("verified", Map.of("email", email));
    }

    @Override
    public Map<String, Object> register(UUID preRegId, String language) {
        // 簡易: PreReg を拾って User を作る
        Optional<Models.PreReg> prOpt = preRegRepo.findById(preRegId);
        if (prOpt.isEmpty()) {
            return ng("preReg not found");
        }
        Models.PreReg pr = prOpt.get();

        Models.User u = new Models.User();
        u.setId(UUID.randomUUID());                  // ← User の型が UUID の場合
        u.setAccountId(UUID.randomUUID());           // ← 同上（String フィールドなら toString() を渡す）
        u.setEmail(pr.getEmail());
        u.setCreatedAt(Instant.now());
        userRepo.save(u);

        return ok("registered", Map.of("user", toUserMap(u)));
    }

    /* ===== Helper ===== */

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

    private Map<String,Object> toUserMap(Models.User u) {
        return Map.of(
            "id",        u.getId() instanceof UUID ? ((UUID)u.getId()).toString() : u.getId().toString(),
            "accountId", u.getAccountId() instanceof UUID ? ((UUID)u.getAccountId()).toString() : u.getAccountId().toString(),
            "email",     u.getEmail(),
            "createdAt", u.getCreatedAt()
        );
    }
}
