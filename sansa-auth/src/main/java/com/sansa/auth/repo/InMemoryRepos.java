package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.repo.RepoInterfaces.IPreRegRepo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class InMemoryRepos {

    /* =====================
     * UserRepo (in-memory)
     * ===================== */
    public static class UserRepo implements IUserRepo {
        private final Map<String, Models.User> byId = new ConcurrentHashMap<>();
        private final Map<String, Models.User> byEmail = new ConcurrentHashMap<>();
        private final Map<String, Models.User> byAccountId = new ConcurrentHashMap<>();

        @Override
        public Models.User save(Models.User user) {
            if (user == null) return null;
            // Models.User は getter がある前提
            UUID id = user.getId();
            String email = user.getEmail();
            UUID accountId = user.getAccountId();

            if (id != null) byId.put(id, user);
            if (email != null) byEmail.put(email, user);
            if (accountId != null) byAccountId.put(accountId, user);
            return user;
        }

        @Override
        public Models.User findById(UUID userId) {
            return byId.get(userId);
        }

        @Override
        public Models.User findByEmail(String email) {
            return byEmail.get(email);
        }

        @Override
        public Models.User findByAccountId(UUID accountId) {
            return byAccountId.get(accountId);
        }

        @Override
        public void deleteById(UUID userId) {
            Models.User removed = byId.remove(userId);
            if (removed != null) {
                if (removed.getEmail() != null) {
                    byEmail.remove(removed.getEmail());
                }
                if (removed.getAccountId() != null) {
                    byAccountId.remove(removed.getAccountId());
                }
            }
        }
    }

    /* ========================
     * SessionRepo (in-memory)
     * ======================== */
    public static class SessionRepo implements ISessionRepo {
        private final Map<String, Models.Session> byId = new ConcurrentHashMap<>();
        private final Map<String, Models.Session> byToken = new ConcurrentHashMap<>();
        private final Map<String, Models.Session> byUserId = new ConcurrentHashMap<>();

        @Override
        public Models.Session save(Models.Session session) {
            if (session == null) return null;
            UUID id = session.getId();
            String token = session.getToken();
            UUID userId = session.getUserId();

            if (id != null) byId.put(id, session);
            if (token != null) byToken.put(token, session);
            if (userId != null) byUserId.put(userId, session);
            return session;
        }

        @Override
        public Models.Session findById(String sessionId) {
            return byId.get(sessionId);
        }

        @Override
        public void deleteById(String sessionId) {
            Models.Session removed = byId.remove(sessionId);
            if (removed != null) {
                if (removed.getToken() != null) {
                    byToken.remove(removed.getToken());
                }
                if (removed.getUserId() != null) {
                    byUserId.remove(removed.getUserId());
                }
            }
        }

        @Override
        public Models.Session findByToken(String token) {
            return byToken.get(token);
        }

        @Override
        public Models.Session findByUserId(UUID userId) {
            return byUserId.get(userId);
        }
    }

    /* =======================
     * PreRegRepo (in-memory)
     * ======================= */
    public static class PreRegRepo implements IPreRegRepo {
        private final Map<String, Models.PreReg> byId = new ConcurrentHashMap<>();

        @Override
        public Models.PreReg save(Models.PreReg preReg) {
            if (preReg == null) return null;
            UUID id = preReg.getPreRegId();
            if (id != null) byId.put(id, preReg);
            return preReg;
        }

        @Override
        public Models.PreReg findById(UUID preRegId) {
            return byId.get(preRegId);
        }

        @Override
        public void deleteById(UUID preRegId) {
            byId.remove(preRegId);
        }
    }
}
