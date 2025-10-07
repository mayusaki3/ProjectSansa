package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.model.Models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Profile("inmem")
@Service
public class ServicesInmem extends AuthService {  // implements → extends に修正

    // 簡易 In-memory ストア
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<String, PreReg> preRegs = new ConcurrentHashMap<>();

    // 簡易 PreReg 定義（Models.PreReg 参照をやめ、ここで完結）
    private static class PreReg {
        private final String preRegId;
        private final String email;
        private final String language;
        private boolean verified;

        private PreReg(String preRegId, String email, String language) {
            this.preRegId = preRegId;
            this.email = email;
            this.language = language;
            this.verified = false;
        }
        public String getPreRegId() { return preRegId; }
        public String getEmail() { return email; }
        public String getLanguage() { return language; }
        public boolean isVerified() { return verified; }
        public void setVerified(boolean verified) { this.verified = verified; }
    }

    @Override
    public AuthResult preRegister(String email, String language) {
        String preRegId = UUID.randomUUID().toString();
        preRegs.put(preRegId, new PreReg(preRegId, email, language));

        Map<String, Object> details = new HashMap<>();
        details.put("preRegId", preRegId);
        details.put("email", email);
        details.put("language", language);
        return AuthResult.ok("pre-register accepted", details);
    }

    @Override
    public AuthResult verifyEmail(String preRegId, String code) {
        PreReg pr = preRegs.get(preRegId);
        if (pr == null) {
            return AuthResult.error("preRegId not found");
        }
        pr.setVerified(true);

        Map<String, Object> details = new HashMap<>();
        details.put("preRegId", preRegId);
        details.put("verified", true);
        details.put("email", pr.getEmail());
        return AuthResult.ok("email verified", details);
    }

    @Override
    public AuthResult register(String preRegId, String accountId, String language) {
        PreReg pr = preRegs.get(preRegId);
        if (pr == null || !pr.isVerified()) {
            return AuthResult.error("email not verified");
        }

        UUID uid = UUID.randomUUID();
        UUID acc;
        try {
            acc = UUID.fromString(accountId);
        } catch (Exception e) {
            return AuthResult.error("invalid accountId: " + accountId);
        }

        // User は引数なしコンストラクタのみ → setter で設定
        User u = new User();
        u.setId(uid);
        u.setAccountId(acc);
        u.setEmail(pr.getEmail());
        u.setCreatedAt(Instant.now());
        users.put(uid, u);

        Map<String, Object> details = new HashMap<>();
        details.put("userId", u.getId().toString());
        details.put("accountId", u.getAccountId().toString());
        details.put("email", u.getEmail());
        details.put("createdAt", u.getCreatedAt().toString());
        details.put("language", language);

        return AuthResult.ok("registered", details);
    }
}
