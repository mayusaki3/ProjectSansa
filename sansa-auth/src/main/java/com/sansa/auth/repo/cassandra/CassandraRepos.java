package com.sansa.auth.repo.cassandra;

import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.repo.RepoInterfaces.IPreRegRepo;
import java.util.UUID;

/**
 * ひとまずコンパイルを通すためのダミー実装。
 * 実DB 実装時に中身を置き換えてください。
 */
public class CassandraRepos {

    public static class UserRepo implements IUserRepo {
        @Override
        public Models.User save(Models.User user) {
            // TODO: 実装（Cassandra driver に差し替え）
            return user;
        }

        @Override
        public Models.User findById(UUID userId) {
            // TODO
            return null;
        }

        @Override
        public Models.User findByEmail(String email) {
            // TODO
            return null;
        }

        @Override
        public Models.User findByAccountId(UUID accountId) {
            // TODO
            return null;
        }

        @Override
        public void deleteById(UUID userId) {
            // TODO
        }
    }

    public static class SessionRepo implements ISessionRepo {
        @Override
        public Models.Session save(Models.Session session) {
            // TODO
            return session;
        }

        @Override
        public Models.Session findById(UUID sessionId) {
            // TODO
            return null;
        }

        @Override
        public void deleteById(UUID sessionId) {
            // TODO
        }

        @Override
        public Models.Session findByToken(String token) {
            // TODO
            return null;
        }

        @Override
        public Models.Session findByUserId(UUID userId) {
            // TODO
            return null;
        }
    }

    public static class PreRegRepo implements IPreRegRepo {
        @Override
        public Models.PreReg save(Models.PreReg preReg) {
            // TODO
            return preReg;
        }

        @Override
        public Models.PreReg findById(UUID preRegId) {
            // TODO
            return null;
        }

        @Override
        public void deleteById(UUID preRegId) {
            // TODO
        }
    }
}
