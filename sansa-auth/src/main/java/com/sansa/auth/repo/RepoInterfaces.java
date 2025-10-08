package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.model.Models.PreReg;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface declarations.
 * - User / Session は UUID キー
 * - PreReg は preRegId(String)
 */
public final class RepoInterfaces {

    /** ユーザー用リポジトリ */
    public interface IUserRepo {
        User save(User user);
        Optional<User> findById(UUID id);
        Optional<User> findByAccountId(UUID accountId);
        Optional<User> findByEmail(String email);
        void deleteById(UUID id);
    }

    /** セッション用リポジトリ */
    public interface ISessionRepo {
        Session save(Session session);
        Optional<Session> findById(UUID sessionId);
        void deleteById(UUID sessionId);
        void deleteAllByUserId(UUID userId);
    }

    /** 事前登録（PreReg）用リポジトリ */
    public interface IPreRegRepo {
        PreReg save(PreReg preReg);
        Optional<PreReg> findById(UUID preRegId);
        void deleteById(UUID preRegId);
    }

    private RepoInterfaces() {}
}
