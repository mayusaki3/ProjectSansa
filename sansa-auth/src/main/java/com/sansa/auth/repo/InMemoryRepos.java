package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.model.Models.PreReg;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.repo.RepoInterfaces.IPreRegRepo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

/**
 * In-memory implementations for quick dev/test.
 */
public final class InMemoryRepos {

    /** UserRepo */
    public static class UserRepo implements IUserRepo {
        private final ConcurrentMap<UUID, User> users = new ConcurrentHashMap<>();
        // インデックス
        private final ConcurrentMap<String, UUID> byAccountId = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, UUID> byEmail = new ConcurrentHashMap<>();

        @Override
        public User save(User user) {
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());
            }
            users.put(user.getId(), user);
            if (user.getAccountId() != null) {
                byAccountId.put(user.getAccountId(), user.getId());
            }
            if (user.getEmail() != null) {
                byEmail.put(user.getEmail(), user.getId());
            }
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public Optional<User> findByAccountId(UUID accountId) {
            UUID id = byAccountId.get(accountId);
            return id == null ? Optional.empty() : Optional.ofNullable(users.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            UUID id = byEmail.get(email);
            return id == null ? Optional.empty() : Optional.ofNullable(users.get(id));
        }

        @Override
        public void deleteById(UUID id) {
            User removed = users.remove(id);
            if (removed != null) {
                if (removed.getAccountId() != null) byAccountId.remove(removed.getAccountId());
                if (removed.getEmail() != null) byEmail.remove(removed.getEmail());
            }
        }
    }

    /** SessionRepo */
    public static class SessionRepo implements ISessionRepo {
        private final ConcurrentMap<UUID, Session> sessions = new ConcurrentHashMap<>();
        // userId → sessionId の逆引き（複数を想定するなら Set に）
        private final ConcurrentMap<UUID, Set<UUID>> byUserId = new ConcurrentHashMap<>();

        @Override
        public Session save(Session session) {
            if (session.getSessionId() == null) {
                session.setSessionId(UUID.randomUUID());
            }
            sessions.put(session.getSessionId(), session);
            byUserId.computeIfAbsent(session.getUserId(), k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(session.getSessionId());
            return session;
        }

        @Override
        public Optional<Session> findById(UUID sessionId) {
            return Optional.ofNullable(sessions.get(sessionId));
        }

        @Override
        public void deleteById(UUID sessionId) {
            Session removed = sessions.remove(sessionId);
            if (removed != null) {
                Set<UUID> set = byUserId.get(removed.getUserId());
                if (set != null) {
                    set.remove(sessionId);
                    if (set.isEmpty()) byUserId.remove(removed.getUserId());
                }
            }
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            Set<UUID> set = byUserId.remove(userId);
            if (set != null) {
                for (UUID sid : set) {
                    sessions.remove(sid);
                }
            }
        }
    }

    /** PreRegRepo */
    public static class PreRegRepo implements IPreRegRepo {
        private final ConcurrentMap<String, PreReg> store = new ConcurrentHashMap<>();

        @Override
        public PreReg save(PreReg preReg) {
            if (preReg.getPreRegId() == null || preReg.getPreRegId().isEmpty()) {
                preReg.setPreRegId(UUID.randomUUID().toString());
            }
            store.put(preReg.getPreRegId(), preReg);
            return preReg;
        }

        @Override
        public Optional<PreReg> findById(UUID preRegId) {
            return Optional.ofNullable(store.get(preRegId));
        }
        @Override
        public void deleteById(UUID preRegId) {
            store.remove(preRegId);
        }
    }

    private InMemoryRepos() {}
}
