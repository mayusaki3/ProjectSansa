package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Profile("inmem")
public class ServicesInmem implements AuthService {

    private final RepoInterfaces.IUserRepo userRepo;

    public ServicesInmem(RepoInterfaces.IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Dtos.AuthResult preRegister(String email, String language) {
        // inmem はダミー挙動（常に成功）
        return Dtos.AuthResult.ok("pre-registered");
    }

    @Override
    public Dtos.AuthResult verifyEmail(String preRegId, String code) {
        // inmem はダミー挙動（常に成功）
        return Dtos.AuthResult.ok("verified");
    }

    @Override
    public Dtos.AuthResult register(String preRegId, String language) {
        // 本来は preRegId からメール等を取り出す。ここではダミーで作成
        Models.User u = new Models.User();
        u.setId(UUID.randomUUID());
        u.setAccountId(UUID.randomUUID());
        u.setEmail("user+" + preRegId + "@example.local");
        u.setCreatedAt(Instant.now());

        u = userRepo.save(u);
        return Dtos.AuthResult.ok("registered", java.util.Map.of("user", u));
    }
}
