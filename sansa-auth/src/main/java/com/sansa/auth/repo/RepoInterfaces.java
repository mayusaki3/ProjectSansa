package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import java.util.UUID;

public class RepoInterfaces {

    /** ユーザーリポジトリ */
    public interface IUserRepo {
        Models.User save(Models.User user);
        Models.User findById(UUID userId);
        Models.User findByEmail(String email);
        Models.User findByAccountId(UUID accountId);
        void deleteById(UUID userId);
    }

    /** セッションリポジトリ */
    public interface ISessionRepo {
        Models.Session save(Models.Session session);
        Models.Session findById(UUID sessionId);
        void deleteById(String sessionId);
        Models.Session findByToken(String token);
        Models.Session findByUserId(UUID userId);
    }

    /** 事前登録（PreReg）用リポジトリ */
    public interface IPreRegRepo {
        Models.PreReg save(Models.PreReg preReg);
        Models.PreReg findById(UUID preRegId);
        void deleteById(UUID preRegId);
    }
}
