package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("cassandra")
public class CassandraRepos {

    @Repository
    @Profile("cassandra")
    public static class UserRepo implements IUserRepo {

        private final CqlSession session;

        public UserRepo(CqlSession session) {
            this.session = session;
        }

        @Override
        public boolean existsById(UUID id) {
            // TODO: SELECT 1 FROM users WHERE id = ?
            throw new UnsupportedOperationException("existsById not implemented yet.");
        }

        @Override
        public boolean existsByEmail(String email) {
            // TODO: SELECT 1 FROM users_by_email WHERE email = ?
            throw new UnsupportedOperationException("existsByEmail not implemented yet.");
        }

        @Override
        public Optional<User> findById(UUID id) {
            // TODO: SELECT * FROM users WHERE id = ?
            throw new UnsupportedOperationException("findById not implemented yet.");
        }

        @Override
        public Optional<User> findByEmail(String email) {
            // TODO: SELECT * FROM users_by_email WHERE email = ?
            throw new UnsupportedOperationException("findByEmail not implemented yet.");
        }

        @Override
        public User save(User user) {
            // TODO: INSERT/UPDATE users & users_by_email
            throw new UnsupportedOperationException("save not implemented yet.");
        }
    }

    @Repository
    @Profile("cassandra")
    public static class SessionRepo implements ISessionRepo {

        private final CqlSession session;

        public SessionRepo(CqlSession session) {
            this.session = session;
        }

        @Override
        public Optional<Session> findById(UUID id) {
            // TODO: SELECT * FROM sessions WHERE id = ?
            throw new UnsupportedOperationException("findById not implemented yet.");
        }

        @Override
        public List<Session> findByUserId(UUID userId) {
            // TODO: SELECT * FROM sessions_by_user WHERE user_id = ?
            throw new UnsupportedOperationException("findByUserId not implemented yet.");
        }

        @Override
        public Optional<Session> findByUserIdAndDeviceId(UUID userId, String deviceId) {
            // TODO: SELECT * FROM sessions_by_user_device WHERE user_id = ? AND device_id = ?
            throw new UnsupportedOperationException("findByUserIdAndDeviceId not implemented yet.");
        }

        @Override
        public Session save(Session sessionModel) {
            // TODO: INSERT/UPDATE sessions / sessions_by_user / sessions_by_user_device
            throw new UnsupportedOperationException("save not implemented yet.");
        }

        @Override
        public void delete(UUID sessionId) {
            // TODO: DELETE FROM sessions WHERE id = ?; DELETE FROM sessions_by_user ...; DELETE FROM sessions_by_user_device ...
            throw new UnsupportedOperationException("delete not implemented yet.");
        }

        @Override
        public void deleteAllByUserId(UUID userId) {
            // TODO: DELETE all rows for user_id from sessions_by_user and related tables
            throw new UnsupportedOperationException("deleteAllByUserId not implemented yet.");
        }
    }
}
