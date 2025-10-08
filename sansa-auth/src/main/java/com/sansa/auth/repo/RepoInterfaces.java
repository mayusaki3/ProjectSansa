package com.sansa.auth.repo;

import com.sansa.auth.model.Models;

/**
 * リポジトリの契約（インターフェース）群。
 * 実装は in-memory / Cassandra などで用意する前提。
 */
public final class RepoInterfaces {

    private RepoInterfaces() { /* no-op */ }

    /** 事前登録（PreReg）用リポジトリ */
    public interface IPreRegRepo {
        Models.PreReg save(Models.PreReg preReg);
        Models.PreReg findById(String preRegId);
        void deleteById(String preRegId);
    }

    /** ユーザー用リポジトリ */
    public interface IUserRepo {
        Models.User save(Models.User user);
        Models.User findByEmail(String email);
        Models.User findByAccountId(String accountId);
    }

    /** セッション用リポジトリ */
    public interface ISessionRepo {
        Models.Session save(Models.Session session);
        Models.Session findById(String sessionId);
        void deleteById(String sessionId);
    }
}
