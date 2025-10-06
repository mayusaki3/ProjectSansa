package com.sansa.auth.repo;

import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class RepoInterfaces {

    private RepoInterfaces() {}

    public interface IUserRepo {
        boolean existsById(UUID id);
        boolean existsByEmail(String email);

        Optional<User> findById(UUID id);
        Optional<User> findByEmail(String email);

        User save(User user);
    }

    public interface ISessionRepo {
        Optional<Session> findById(UUID id);

        List<Session> findByUserId(UUID userId);
        Optional<Session> findByUserIdAndDeviceId(UUID userId, String deviceId);

        Session save(Session session);

        void delete(UUID sessionId);
        void deleteAllByUserId(UUID userId);
    }
}
