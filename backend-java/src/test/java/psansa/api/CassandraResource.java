package psansa.api;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.CassandraContainer;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import com.datastax.oss.driver.api.core.CqlSession;

public class CassandraResource implements BeforeAllCallback {
  static final CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:5");

  @Override
  public void beforeAll(ExtensionContext context) {
    if (!cassandra.isRunning()) {
      cassandra.start();
    }

    // アプリ側が読む接続設定（System properties）
    System.setProperty("psansa.cassandra.contact-points",
        cassandra.getHost() + ":" + cassandra.getFirstMappedPort());
    System.setProperty("psansa.cassandra.local-dc", "datacenter1");
    System.setProperty("psansa.cassandra.keyspace", "sansa");

    // スキーマ適用
    try (CqlSession s = CqlSession.builder()
            .addContactPoint(new InetSocketAddress(
                cassandra.getHost(), cassandra.getFirstMappedPort()))
            .withLocalDatacenter("datacenter1")
            .build()) {
      String cql = Files.readString(Path.of("src/test/resources/init.cql"));
      for (String stmt : cql.split(";")) {
        String trimmed = stmt.trim();
        if (!trimmed.isEmpty()) {
          s.execute(trimmed);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
