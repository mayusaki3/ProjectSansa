package com.sansa.auth.repo;

import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepos {
    public static class UserRepo {
        private final Map<String, User> byAccount = new ConcurrentHashMap<>();
        private final Map<String, User> byEmail = new ConcurrentHashMap<>();

        public Optional<User> findByAccountId(String accountId) {
            return Optional.ofNullable(byAccount.get(accountId.toLowerCase(Locale.ROOT)));
        }
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(byEmail.get(email.toLowerCase(Locale.ROOT)));
        }
        public User save(User u) {
            byAccount.put(u.accountId.toLowerCase(Locale.ROOT), u);
            byEmail.put(u.email.toLowerCase(Locale.ROOT), u);
            return u;
        }
    }

    public static class SessionRepo {
        private final Map<UUID, Session> byId = new ConcurrentHashMap<>();
        private final Map<UUID, List<Session>> byUser = new ConcurrentHashMap<>();

        public Session save(Session s) {
            byId.put(s.sessionId, s);
            byUser.computeIfAbsent(s.userId, k -> new ArrayList<>()).add(s);
            return s;
        }
        public List<Session> findByUserId(UUID userId) {
            return byUser.getOrDefault(userId, List.of());
        }
        public void delete(UUID sessionId) {
            Session s = byId.remove(sessionId);
            if (s != null) {
                byUser.getOrDefault(s.userId, List.of()).removeIf(x -> x.sessionId.equals(sessionId));
            }
        }
    }
}
