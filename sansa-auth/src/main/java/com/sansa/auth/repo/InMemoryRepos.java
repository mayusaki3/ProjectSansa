package com.sansa.auth.repo;

import com.sansa.auth.model.Models.Session;
import com.sansa.auth.model.Models.User;
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
    public static class UserRepo implements IUserRepo {
        private final Map<UUID, User> store = new ConcurrentHashMap<>();

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Optional<User> findByLoginId(String loginId) {
            return store.values().stream()
                    .filter(u -> Objects.equals(u.getEmail(), loginId)) // email を loginId とみなす想定
                    .findFirst();
        }

        @Override
        public boolean existsById(UUID id) {
            return store.containsKey(id);
        }

        @Override
        public boolean existsByEmail(String email) {
            return store.values().stream().anyMatch(u -> Objects.equals(u.getEmail(), email));
        }

        @Override
        public User save(User user) {
            store.put(user.getId(), user);
            return user;
        }

        @Override
        public void deleteById(UUID id) {
            store.remove(id);
        }
    }

    @Repository
    public static class SessionRepo implements ISessionRepo {
        private final Map<UUID, Session> store = new ConcurrentHashMap<>();

        @Override
        public Optional<Session> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Session save(Session session) {
            store.put(session.getId(), session);
            return session;
        }

        @Override
        public void deleteById(UUID id) {
            store.remove(id);
        }

        @Override
        public List<Session> findByUserId(UUID userId) {
            return store.values().stream()
                    .filter(s -> Objects.equals(s.getUserId(), userId))
                    .collect(Collectors.toList());
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            var ids = store.values().stream()
                    .filter(s -> Objects.equals(s.getUserId(), userId))
                    .map(Session::getId)
                    .toList();
            ids.forEach(store::remove);
        }
    }
}
