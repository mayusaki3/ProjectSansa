package psansa.api.repo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostRepo {
    @Inject CqlSession db;

    public UUID insert(String authorId, String text, String lang){
        UUID postId = UUID.randomUUID();
        BatchStatement bs = BatchStatement.builder(DefaultBatchType.LOGGED)
            .addStatement(SimpleStatement.newInstance(
                "INSERT INTO posts_by_time (bucket_day, created_at, post_id, author_id, text, media, lang) " +
                    "VALUES (toDate(now()), now(), ?, ?, ?, [], ?)", postId, authorId, text, lang))
            .addStatement(SimpleStatement.newInstance(
                "INSERT INTO posts_by_user (author_id, created_at, post_id, text) VALUES (?, now(), ?, ?)",
                authorId, postId, text))
            .build();
        db.execute(bs);
        return postId;
    }

    public List<Row> latestToday(int limit){
        ResultSet rs = db.execute(SimpleStatement.newInstance(
            "SELECT created_at, post_id, author_id, text, lang FROM posts_by_time WHERE bucket_day=toDate(now()) LIMIT " + limit));
        return rs.all();
    }
}
