package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.repo.RepoInterfaces;
import com.sansa.auth.util.JwtProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * inmem プロファイル用のシンプルな AuthService 実装。
 * 事前登録やメール検証はダミー挙動（必ず成功）にしておき、登録だけ UserRepo に保存します。
 */
@Service
@Profile("inmem")
public class ServicesInmem implements AuthService {

    private final RepoInterfaces.IUserRepo userRepo;
    private final RepoInterfaces.ISessionRepo sessionRepo;
    private final JwtProvider jwtProvider;

    // inmem 用の簡易プリレジストリ（preRegId -> email）
    private final Map<String, String> preRegStore = new HashMap<>();

    public ServicesInmem(RepoInterfaces.IUserRepo userRepo,
                         RepoInterfaces.ISessionRepo sessionRepo,
                         JwtProvider jwtProvider) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Map<String, Object> preRegister(String email) {
        // 実際はメール送信などを行うが、inmem ではダミーで preRegId を返す
        String preRegId = UUID.randomUUID().toString();
        preRegStore.put(preRegId, email);

        return Map.of(
                "ok", true,
                "preRegId", preRegId,
                "email", email
        );
    }

    @Override
    public Map<String, Object> verifyEmail(String email, String code) {
        // 本来は code 検証。inmem では常に成功扱い。
        // 既存の preRegId のうち email が一致するものを返す（なければ新規に採番）
        String preRegId = preRegStore.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), email))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> {
                    String id = UUID.randomUUID().toString();
                    preRegStore.put(id, email);
                    return id;
                });

        return Map.of(
                "ok", true,
                "preRegId", preRegId,
                "email", email
        );
    }

    @Override
    public User register(String preRegId, String accountId, String language) {
        // preRegId が存在しない/メール不一致でも inmem では許容（実運用ではチェックして 4xx 返す）
        String email = preRegStore.getOrDefault(preRegId, accountId + "@example.com");

        // 既存ユーザならそのまま返す
        if (userRepo.existsByEmail(email)) {
            // findByLoginId は userId ベースっぽいので email 既存時は簡易に新規検索/保存をスキップ
            // ここでは一旦新規作成せず、適当に id を生成して返す必要があるなら保存結果で返す
        }

        User u = new Models.User();
        u.id = UUID.randomUUID();
        u.accountId = UUID.randomUUID(); // 簡易に採番（要求仕様に合わせて accountId を使うならここで変える）
        u.email = email;
        u.createdAt = Instant.now();

        // 保存
        u = userRepo.save(u);

        // セッションを張るならここで作成してもよいが、inmem では省略
        return u;
    }
}
