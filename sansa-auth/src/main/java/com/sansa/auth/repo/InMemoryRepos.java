package com.sansa.auth.repo;

import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class InMemoryRepos {

    @Repository
    @Profile("inmemory")
    public static class UserRepo implements IUserRepo {

        private final Map<UUID, User> store = new ConcurrentHashMap<>();
        // email -> id 索引
        private final Map<String, UUID> emailIndex = new ConcurrentHashMap<>();

        @Override
        public boolean existsById(UUID id) {
            return store.containsKey(id);
        }

        @Override
        public boolean existsByEmail(String email) {
            return emailIndex.containsKey(email);
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            UUID id = emailIndex.get(email);
            return id == null ? Optional.empty() : Optional.ofNullable(store.get(id));
        }

        @Override
        public User save(User user) {
            Objects.requireNonNull(user, "user");
            UUID id = user.getId();
            Objects.requireNonNull(id, "user.id");

            store.put(id, user);
            if (user.getEmail() != null) {
                emailIndex.put(user.getEmail(), id);
            }
            return user;
        }
    }

    @Repository
    @Profile("inmemory")
    public static class SessionRepo implements ISessionRepo {

        private final Map<UUID, Session> store = new ConcurrentHashMap<>();

        @Override
        public Optional<Session> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Session> findByUserId(UUID userId) {
            return store.values().stream()
                    .filter(s -> userId.equals(s.getUserId()))
                    .collect(Collectors.toList());
        }

        @Override
        public Optional<Session> findByUserIdAndDeviceId(UUID userId, String deviceId) {
            return store.values().stream()
                    .filter(s -> userId.equals(s.getUserId()) && Objects.equals(deviceId, s.getDeviceId()))
                    .findFirst();
        }

        @Override
        public Session save(Session session) {
            Objects.requireNonNull(session, "session");
            store.put(session.getId(), session);
            return session;
        }

        @Override
        public void delete(UUID sessionId) {
            store.remove(sessionId);
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            store.values().removeIf(s -> userId.equals(s.getUserId()));
        }
    }
}
