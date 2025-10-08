package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface declarations.
 * - User/Session は UUID をキーに統一
 * - PreReg は仕様通り preRegId(String) のまま
 */
public final class RepoInterfaces {

    /** ユーザー用リポジトリ */
    public interface IUserRepo {
        User save(User user);
        Optional<User> findById(UUID id);
        Optional<User> findByEmail(String email);
        Optional<User> findByAccountId(String accountId);
    }

    /** セッション用リポジトリ */
    public interface ISessionRepo {
        Session save(Session session);
        Optional<Session> findById(UUID sessionId);
        void delete(UUID sessionId);
        void deleteAllByUserId(UUID userId);
    }

    /** 事前登録（PreReg）用リポジトリ */
    public interface IPreRegRepo {
        Models.PreReg save(Models.PreReg preReg);
        Optional<Models.PreReg> findById(String preRegId);
        void deleteById(String preRegId);
    }

    private RepoInterfaces() {}
}
