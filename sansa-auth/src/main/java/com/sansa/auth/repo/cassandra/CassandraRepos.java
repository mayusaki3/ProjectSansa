package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.repo.RepoInterfaces.IPreRegRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("cassandra")
public class CassandraRepos {

    // DI想定。実装はあとで詰める。
    private final CqlSession session = null;

    // ===== User =====
    @Repository
    @Profile("cassandra")
    public static class UserRepo implements IUserRepo {

        @Override
        public User save(User user) {
            // TODO: INSERT/UPDATE users
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            // TODO: SELECT ... WHERE id=?
            return Optional.empty();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            // TODO: SELECT ... WHERE email=?
            return Optional.empty();
        }

        @Override
        public Optional<User> findByAccountId(String accountId) {
            // TODO: SELECT ... WHERE account_id=?
            return Optional.empty();
        }
    }

    // ===== Session =====
    @Repository
    @Profile("cassandra")
    public static class SessionRepo implements ISessionRepo {

        @Override
        public Session save(Session s) {
            // TODO: INSERT/UPDATE sessions
            return s;
        }

        @Override
        public Optional<Session> findById(UUID sessionId) {
            // TODO: SELECT ... WHERE id=?
            return Optional.empty();
        }

        @Override
        public void delete(UUID sessionId) {
            // TODO: DELETE FROM sessions WHERE id=?; 他のインデックスも削除
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            // TODO: ユーザー関連のセッションを全削除
        }
    }

    // ===== PreReg =====（必要なら追加で）
    @Repository
    @Profile("cassandra")
    public static class PreRegRepo implements IPreRegRepo {
        @Override
        public Models.PreReg save(Models.PreReg preReg) {
            // TODO
            return preReg;
        }

        @Override
        public Optional<Models.PreReg> findById(String preRegId) {
            // TODO
            return Optional.empty();
        }

        @Override
        public void deleteById(String preRegId) {
            // TODO
        }
    }
}
