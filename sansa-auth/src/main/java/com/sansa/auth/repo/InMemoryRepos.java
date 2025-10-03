package com.sansa.auth.repo;

import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.repo.RepoInterfaces.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepos {

    @Repository
    @Profile("inmem")
    public static class UserRepo implements IUserRepo {
        private final Map<String, User> byAccount = new ConcurrentHashMap<>();
        private final Map<String, User> byEmail = new ConcurrentHashMap<>();

        @Override
        public Optional<User> findByAccountId(String accountId) {
            return Optional.ofNullable(byAccount.get(accountId.toLowerCase(Locale.ROOT)));
        }
        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(byEmail.get(email.toLowerCase(Locale.ROOT)));
        }
        @Override
        public User save(User u) {
            byAccount.put(u.accountId.toLowerCase(Locale.ROOT), u);
            byEmail.put(u.email.toLowerCase(Locale.ROOT), u);
            return u;
        }
    }

    @Repository
    @Profile("inmem")
    public static class SessionRepo implements ISessionRepo {
        private final Map<UUID, Session> byId = new ConcurrentHashMap<>();
        private final Map<UUID, List<Session>> byUser = new ConcurrentHashMap<>();

        @Override
        public Session save(Session s) {
            byId.put(s.sessionId, s);
            byUser.computeIfAbsent(s.userId, k -> new ArrayList<>()).add(s);
            return s;
        }
        @Override
        public List<Session> findByUserId(UUID userId) {
            return byUser.getOrDefault(userId, List.of());
        }
        @Override
        public void delete(UUID sessionId) {
            Session s = byId.remove(sessionId);
            if (s != null) {
                byUser.getOrDefault(s.userId, List.of()).removeIf(x -> x.sessionId.equals(sessionId));
            }
        }
    }
}
