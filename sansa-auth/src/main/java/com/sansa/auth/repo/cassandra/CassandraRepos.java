package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;
import com.sansa.auth.repo.RepoInterfaces.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@Profile("cassandra")
public class CassandraRepos {

    @Repository
    @Profile("cassandra")
    public static class UserRepo implements IUserRepo {
        private final CqlSession session;

        private final PreparedStatement selByAccount;
        private final PreparedStatement selByEmail;
        private final PreparedStatement upsert;

        public UserRepo(CqlSession session) {
            this.session = session;
            this.selByAccount = session.prepare(
                "SELECT user_id, account_id, email, email_verified, mfa_enabled, language, token_version, created_at, last_login_at " +
                "FROM users WHERE account_id = ?");
            this.selByEmail = session.prepare(
                "SELECT user_id, account_id, email, email_verified, mfa_enabled, language, token_version, created_at, last_login_at " +
                "FROM users_by_email WHERE email = ?");
            this.upsert = session.prepare(
                "INSERT INTO users (user_id, account_id, email, email_verified, mfa_enabled, language, token_version, created_at, last_login_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? )");
        }

        @Override
        public Optional<User> findByAccountId(String accountId) {
            Row r = session.execute(selByAccount.bind(accountId)).one();
            return Optional.ofNullable(mapUser(r));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            Row r = session.execute(selByEmail.bind(email.toLowerCase(Locale.ROOT))).one();
            return Optional.ofNullable(mapUser(r));
        }

        @Override
        public User save(User u) {
            session.execute(upsert.bind(
                u.userId, u.accountId, u.email, u.emailVerified, u.mfaEnabled, u.language, u.tokenVersion,
                Date.from(u.createdAt), u.lastLoginAt == null ? null : Date.from(u.lastLoginAt)
            ));
            // also maintain users_by_email
            session.execute(SimpleStatement.newInstance(
                "INSERT INTO users_by_email (email, user_id, account_id) VALUES (?,?,?)",
                u.email.toLowerCase(Locale.ROOT), u.userId, u.accountId
            ));
            return u;
        }

        private User mapUser(Row r) {
            if (r == null) return null;
            User u = new User();
            u.userId = r.getUuid("user_id");
            u.accountId = r.getString("account_id");
            u.email = r.getString("email");
            u.emailVerified = r.getBoolean("email_verified");
            u.mfaEnabled = r.getBoolean("mfa_enabled");
            u.language = r.getString("language");
            u.tokenVersion = r.getLong("token_version");
            Date ca = r.get("created_at", Date.class);
            if (ca != null) u.createdAt = ca.toInstant();
            Date ll = r.get("last_login_at", Date.class);
            if (ll != null) u.lastLoginAt = ll.toInstant();
            return u;
        }
    }

    @Repository
    @Profile("cassandra")
    public static class SessionRepo implements ISessionRepo {
        private final CqlSession session;

        private final PreparedStatement upsert;
        private final PreparedStatement byUser;
        private final PreparedStatement delete;

        public SessionRepo(CqlSession session) {
            this.session = session;
            this.upsert = session.prepare(
                "INSERT INTO sessions (session_id, user_id, device_id, token_version, created_at, last_seen_at, mfa_last_ok_at) " +
                "VALUES (?,?,?,?,?,?,?)");
            this.byUser = session.prepare(
                "SELECT session_id, user_id, device_id, token_version, created_at, last_seen_at, mfa_last_ok_at " +
                "FROM sessions_by_user WHERE user_id = ?");
            this.delete = session.prepare("DELETE FROM sessions WHERE session_id = ?");
        }

        @Override
        public Session save(Session s) {
            session.execute(upsert.bind(
                s.sessionId, s.userId, s.deviceId, s.tokenVersion,
                Date.from(s.createdAt), Date.from(s.lastSeenAt),
                s.mfaLastOkAt == null ? null : Date.from(s.mfaLastOkAt)
            ));
            session.execute(SimpleStatement.newInstance(
                "INSERT INTO sessions_by_user (user_id, session_id, device_id, token_version, created_at, last_seen_at) VALUES (?,?,?,?,?,?)",
                s.userId, s.sessionId, s.deviceId, s.tokenVersion, Date.from(s.createdAt), Date.from(s.lastSeenAt)
            ));
            return s;
        }

        @Override
        public List<Session> findByUserId(UUID userId) {
            List<Session> out = new ArrayList<>();
            ResultSet rs = session.execute(byUser.bind(userId));
            for (Row r : rs) {
                Session s = new Session();
                s.sessionId = r.getUuid("session_id");
                s.userId = r.getUuid("user_id");
                s.deviceId = r.getString("device_id");
                s.tokenVersion = r.getLong("token_version");
                Date ca = r.get("created_at", Date.class);
                if (ca != null) s.createdAt = ca.toInstant();
                Date ls = r.get("last_seen_at", Date.class);
                if (ls != null) s.lastSeenAt = ls.toInstant();
                out.add(s);
            }
            return out;
        }

        @Override
        public void delete(UUID sessionId) {
            session.execute(delete.bind(sessionId));
        }
    }
}
