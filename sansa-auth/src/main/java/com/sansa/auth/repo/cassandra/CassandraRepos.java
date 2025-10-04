package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.sansa.auth.model.Models;
import com.sansa.auth.model.Models.User;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("cassandra")
public class CassandraRepos {

    // ---- retry/wait utils ---------------------------------------------------
    static void retry(String label, Runnable r) {
        long deadline = System.currentTimeMillis() + 30_000; // 30s
        int attempt = 0;
        while (true) {
            try { r.run(); return; }
            catch (InvalidQueryException | AllNodesFailedException e) {
                attempt++;
                if (System.currentTimeMillis() > deadline) {
                    throw new RuntimeException("Retry timeout: " + label + " (attempts=" + attempt + ")", e);
                }
                try { Thread.sleep(500L); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }

    static void waitTableVisible(CqlSession s, String ks, String table, long timeoutMs) {
        long end = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < end) {
            try {
                if (s.execute(
                        "SELECT table_name FROM system_schema.tables WHERE keyspace_name=? AND table_name=?",
                        ks, table).one() != null) return;
            } catch (Exception ignore) {}
            try { Thread.sleep(300); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
        throw new RuntimeException("Timeout waiting table visible: " + ks + "." + table);
    }

    // ----------------------------- UserRepo ----------------------------------
    @Repository
    @Profile("cassandra")
    public static class UserRepo implements IUserRepo {
        private final CqlSession session;
        private PreparedStatement psFindByAccount;       // users_by_account_id
        private PreparedStatement psFindByEmail;         // users_by_email
        private PreparedStatement psInsertUser;          // users
        private PreparedStatement psInsertUserByEmail;   // users_by_email
        private PreparedStatement psInsertUserByAccount; // users_by_account_id

        public UserRepo(CqlSession session) { this.session = session; }

        @PostConstruct
        void init() {
            try {
                var ks = session.getKeyspace().orElseThrow().asInternal();

                // DDL
                retry("create users tables", () -> {
                    session.execute(
                        "CREATE TABLE IF NOT EXISTS users (" +
                        "  user_id uuid PRIMARY KEY," +
                        "  account_id text," +
                        "  email text," +
                        "  email_verified boolean," +
                        "  token_version bigint" +
                        ")"
                    );
                    session.execute(
                        "CREATE TABLE IF NOT EXISTS users_by_email (" +
                        "  email text PRIMARY KEY," +
                        "  user_id uuid," +
                        "  account_id text," +
                        "  email_verified boolean," +
                        "  token_version bigint" +
                        ")"
                    );
                    session.execute(
                        "CREATE TABLE IF NOT EXISTS users_by_account_id (" +
                        "  account_id text PRIMARY KEY," +
                        "  user_id uuid," +
                        "  email text," +
                        "  email_verified boolean," +
                        "  token_version bigint" +
                        ")"
                    );
                });

                // 可視化待ち
                waitTableVisible(session, ks, "users", 30_000);
                waitTableVisible(session, ks, "users_by_email", 30_000);
                waitTableVisible(session, ks, "users_by_account_id", 30_000);

                // prepare
                retry("prepare users statements", () -> {
                    psFindByAccount = session.prepare(
                        "SELECT user_id, account_id, email, email_verified, token_version " +
                        "FROM users_by_account_id WHERE account_id=?");
                    psFindByEmail = session.prepare(
                        "SELECT user_id, account_id, email, email_verified, token_version " +
                        "FROM users_by_email WHERE email=?");
                    psInsertUser = session.prepare(
                        "INSERT INTO users (user_id, account_id, email, email_verified, token_version) " +
                        "VALUES (?,?,?,?,?)");
                    psInsertUserByEmail = session.prepare(
                        "INSERT INTO users_by_email (email, user_id, account_id, email_verified, token_version) " +
                        "VALUES (?,?,?,?,?)");
                    psInsertUserByAccount = session.prepare(
                        "INSERT INTO users_by_account_id (account_id, user_id, email, email_verified, token_version) " +
                        "VALUES (?,?,?,?,?)");
                });
            } catch (Throwable e) {
                System.err.println("[UserRepo.init] FAILED: " + e.getClass().getName() + " - " + e.getMessage());
                throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
            }
        }

        @Override
        public Optional<User> findByAccountId(String accountId) {
            Row r = session.execute(psFindByAccount.bind(accountId)).one();
            return Optional.ofNullable(mapUser(r));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            Row r = session.execute(psFindByEmail.bind(email.toLowerCase(Locale.ROOT))).one();
            return Optional.ofNullable(mapUser(r));
        }

        @Override
        public User save(User u) {
            if (u.userId == null) u.userId = UUID.randomUUID();
            if (u.tokenVersion == 0L) u.tokenVersion = 1L;
            if (u.email != null) u.email = u.email.toLowerCase(Locale.ROOT);

            session.execute(psInsertUser.bind(
                    u.userId, u.accountId, u.email, u.emailVerified, u.tokenVersion));
            if (u.email != null) {
                session.execute(psInsertUserByEmail.bind(
                        u.email, u.userId, u.accountId, u.emailVerified, u.tokenVersion));
            }
            if (u.accountId != null) {
                session.execute(psInsertUserByAccount.bind(
                        u.accountId, u.userId, u.email, u.emailVerified, u.tokenVersion));
            }
            return u;
        }

        private static User mapUser(Row r) {
            if (r == null) return null;
            User u = new User();
            u.userId = r.getUuid("user_id");
            u.accountId = r.getString("account_id");
            u.email = r.getString("email");
            u.emailVerified = r.getBoolean("email_verified");
            u.tokenVersion = r.getLong("token_version");
            return u;
        }
    }

    // ----------------------------- SessionRepo -------------------------------
    @Repository
    @Profile("cassandra")
    public static class SessionRepo implements ISessionRepo {
        private final CqlSession session;
        private PreparedStatement psInsertSession;        // sessions
        private PreparedStatement psInsertById;           // sessions_by_id
        private PreparedStatement psFindByUser;           // sessions
        private PreparedStatement psFindById;             // sessions_by_id
        private PreparedStatement psDeleteByUserDevice;   // sessions
        private PreparedStatement psDeleteById;           // sessions_by_id

        public SessionRepo(CqlSession session) { this.session = session; }

        @PostConstruct
        void init() {
            try {
                var ks = session.getKeyspace().orElseThrow().asInternal();

                retry("create sessions tables", () -> {
                    session.execute(
                        "CREATE TABLE IF NOT EXISTS sessions (" +
                        "  user_id uuid," +
                        "  device_id text," +
                        "  token_version bigint," +
                        "  created_at timestamp," +
                        "  PRIMARY KEY (user_id, device_id)" +
                        ")"
                    );
                    session.execute(
                        "CREATE TABLE IF NOT EXISTS sessions_by_id (" +
                        "  session_id uuid PRIMARY KEY," +
                        "  user_id uuid," +
                        "  device_id text" +
                        ")"
                    );
                });

                waitTableVisible(session, ks, "sessions", 30_000);
                waitTableVisible(session, ks, "sessions_by_id", 30_000);

                retry("prepare sessions statements", () -> {
                    psInsertSession = session.prepare(
                        "INSERT INTO sessions (user_id, device_id, token_version, created_at) " +
                        "VALUES (?,?,?,toTimestamp(now()))");
                    psInsertById = session.prepare(
                        "INSERT INTO sessions_by_id (session_id, user_id, device_id) VALUES (?,?,?)");
                    psFindByUser = session.prepare(
                        "SELECT user_id, device_id, token_version, created_at FROM sessions WHERE user_id=?");
                    psFindById = session.prepare(
                        "SELECT user_id, device_id FROM sessions_by_id WHERE session_id=?");
                    psDeleteByUserDevice = session.prepare(
                        "DELETE FROM sessions WHERE user_id=? AND device_id=?");
                    psDeleteById = session.prepare(
                        "DELETE FROM sessions_by_id WHERE session_id=?");
                });
            } catch (Throwable e) {
                System.err.println("[SessionRepo.init] FAILED: " + e.getClass().getName() + " - " + e.getMessage());
                throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
            }
        }

        @Override
        public Models.Session save(Models.Session s) {
            UUID sid = UUID.randomUUID();
            session.execute(psInsertSession.bind(s.userId, s.deviceId, s.tokenVersion));
            session.execute(psInsertById.bind(sid, s.userId, s.deviceId));
            return s;
        }

        @Override
        public List<Models.Session> findByUserId(UUID userId) {
            List<Models.Session> list = new ArrayList<>();
            for (Row r : session.execute(psFindByUser.bind(userId))) {
                var s = new Models.Session();
                s.userId = r.getUuid("user_id");
                s.deviceId = r.getString("device_id");
                s.tokenVersion = r.getLong("token_version");
                s.createdAt = r.getInstant("created_at");
                list.add(s);
            }
            return list;
        }

        @Override
        public void delete(UUID sessionId) {
            Row r = session.execute(psFindById.bind(sessionId)).one();
            if (r == null) return;
            UUID userId = r.getUuid("user_id");
            String deviceId = r.getString("device_id");
            session.execute(psDeleteByUserDevice.bind(userId, deviceId));
            session.execute(psDeleteById.bind(sessionId));
        }
    }
}
