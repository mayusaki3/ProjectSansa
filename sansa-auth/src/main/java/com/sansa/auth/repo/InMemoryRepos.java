package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.repo.RepoInterfaces.IPreRegRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

@Repository
@Profile("inmem")
public class InMemoryRepos {

    // ===== User =====
    @Repository
    @Profile("inmem")
    public static class UserRepo implements IUserRepo {
        private final Map<UUID, User> usersById = new ConcurrentHashMap<>();
        private final Map<String, UUID> idByEmail = new ConcurrentHashMap<>();
        private final Map<String, UUID> idByAccountId = new ConcurrentHashMap<>();

        @Override
        public User save(User user) {
            usersById.put(user.getId(), user);
            if (user.getEmail() != null) {
                idByEmail.put(user.getEmail(), user.getId());
            }
            if (user.getAccountId() != null) {
                idByAccountId.put(user.getAccountId(), user.getId());
            }
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            UUID id = idByEmail.get(email);
            return id == null ? Optional.empty() : Optional.ofNullable(usersById.get(id));
        }

        @Override
        public Optional<User> findByAccountId(String accountId) {
            UUID id = idByAccountId.get(accountId);
            return id == null ? Optional.empty() : Optional.ofNullable(usersById.get(id));
        }
    }

    // ===== Session =====
    @Repository
    @Profile("inmem")
    public static class SessionRepo implements ISessionRepo {
        private final Map<UUID, Session> sessionsById = new ConcurrentHashMap<>();
        private final Map<UUID, Set<UUID>> sessionIdsByUser = new ConcurrentHashMap<>();

        @Override
        public Session save(Session session) {
            sessionsById.put(session.getId(), session);
            sessionIdsByUser.computeIfAbsent(session.getUserId(), k -> new HashSet<>()).add(session.getId());
            return session;
        }

        @Override
        public Optional<Session> findById(UUID sessionId) {
            return Optional.ofNullable(sessionsById.get(sessionId));
        }

        @Override
        public void delete(UUID sessionId) {
            Session removed = sessionsById.remove(sessionId);
            if (removed != null) {
                Set<UUID> set = sessionIdsByUser.getOrDefault(removed.getUserId(), Collections.emptySet());
                set.remove(sessionId);
            }
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            Set<UUID> ids = sessionIdsByUser.remove(userId);
            if (ids != null) {
                for (UUID sid : ids) {
                    sessionsById.remove(sid);
                }
            }
        }
    }

    // ===== PreReg =====
    @Repository
    @Profile("inmem")
    public static class PreRegRepo implements IPreRegRepo {
        private final Map<String, Models.PreReg> store = new ConcurrentHashMap<>();

        @Override
        public Models.PreReg save(Models.PreReg preReg) {
            store.put(preReg.getPreRegId(), preReg);
            return preReg;
        }

        @Override
        public Optional<Models.PreReg> findById(String preRegId) {
            return Optional.ofNullable(store.get(preRegId));
        }

        @Override
        public void deleteById(String preRegId) {
            store.remove(preRegId);
        }
    }
}
