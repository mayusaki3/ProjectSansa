package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicesCassandra {

    private final RepoInterfaces.IUserRepo userRepo;
    private final RepoInterfaces.ISessionRepo sessionRepo;

    public ServicesCassandra(RepoInterfaces.IUserRepo userRepo,
                             RepoInterfaces.ISessionRepo sessionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
    }

    // 例：登録処理の抜粋（実際のエンドポイント呼び出しは AuthService 実装から）
    public Dtos.AuthResult registerInternal(UUID preRegId, String email, String passwordHash) {
        if (preRegId == null) {
            return Dtos.AuthResult.error("preRegId is required");
        }
        if (email == null || email.isBlank()) {
            return Dtos.AuthResult.error("email is required");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            return Dtos.AuthResult.error("password is required");
        }

        // 既存ユーザ重複チェックは findBy系で
        Optional<Models.User> byEmail = userRepo.findByEmail(email);
        if (byEmail.isPresent()) {
            return Dtos.AuthResult.error("email already registered");
        }

        // User を JavaBeans 形式で生成
        Models.User u = new Models.User();
        u.setId(UUID.randomUUID());
        u.setAccountId(preRegId);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setCreatedAt(Instant.now());

        userRepo.save(u);

        return Dtos.AuthResult.ok("registered");
    }

    // 以降の箇所も、User/Session の getter/setter を getXxx()/setXxx() に統一して利用すること
}
