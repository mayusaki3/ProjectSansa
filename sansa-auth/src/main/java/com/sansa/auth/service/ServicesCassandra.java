package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"inmem","cassandra"})
public class ServicesCassandra implements AuthService {

    private final RepoInterfaces.IUserRepo userRepo;
    private final RepoInterfaces.ISessionRepo sessionRepo;

    public ServicesCassandra(RepoInterfaces.IUserRepo userRepo,
                             RepoInterfaces.ISessionRepo sessionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
    }

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

    @Override
    public Dtos.AuthResult verifyEmail(String email, String code) {
        Dtos.VerifyEmailRequest req = new Dtos.VerifyEmailRequest();
        req.setEmail(email);
        req.setCode(code);
        return verifyEmail(req);
    }

    @Override
    public Dtos.AuthResult preRegister(String email) {
        Dtos.PreRegisterRequest req = new Dtos.PreRegisterRequest();
        req.setEmail(email);
        return preRegister(req); // 既存の DTO 版メソッドに委譲
    }

    @Override
    public Dtos.AuthResult register(String preRegId, String accountId, String language) {
        Dtos.RegisterRequest req = new Dtos.RegisterRequest();
        req.setPreRegId(preRegId);
        req.setAccountId(accountId);
        req.setLanguage(language);
        return register(req);
    }
}
