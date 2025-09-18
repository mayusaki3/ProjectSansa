package psansa.api.repo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class UserRepo {
    @Inject CqlSession db;

    public Optional<Row> findByUsername(String username){
        ResultSet rs = db.execute(SimpleStatement.newInstance(
            "SELECT user_id, password_hash, display_name FROM users_by_username WHERE username=?", username));
        Row row = rs.one();
        return Optional.ofNullable(row);
    }

    public void insert(String userId, String username, String hash, String display){
        Instant ts = Instant.now();
        BatchStatement bs = BatchStatement.builder(DefaultBatchType.LOGGED)
            .addStatement(SimpleStatement.newInstance(
                "INSERT INTO users (user_id, username, password_hash, display_name, created_at) VALUES (?,?,?,?,?)",
                userId, username, hash, display, ts))
            .addStatement(SimpleStatement.newInstance(
                "INSERT INTO users_by_username (username, user_id, password_hash, display_name, created_at) VALUES (?,?,?,?,?)",
                username, userId, hash, display, ts))
            .build();
        db.execute(bs);
    }
}
