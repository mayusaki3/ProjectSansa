// src/main/java/com/sansa/auth/repo/cassandra/CassandraConfig.java
package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Configuration
@Profile("cassandra")
public class CassandraConfig {

  @Value("${sansa.cassandra.contactPoint:127.0.0.1}")
  private String contactPoint;

  @Value("${sansa.cassandra.port:9042}")
  private int port;

  @Value("${sansa.cassandra.datacenter:datacenter1}")
  private String datacenter;

  @Value("${sansa.cassandra.keyspace:sansa_auth}")
  private String keyspace;

  @Bean
  public CqlSession cqlSession() {
    // 1) 一時セッションで keyspace 作成
    try (CqlSession tmp = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(contactPoint, port))
        .withLocalDatacenter(datacenter)
        .build()) {
      tmp.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace +
          " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
    }

    // 2) keyspace を選択したセッション
    CqlSession session = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(contactPoint, port))
        .withLocalDatacenter(datacenter)
        .withKeyspace(keyspace)
        .build();

    // 3) スキーマ適用（classpath: cassandra/schema.cql）
    applySchema(session);
    return session;
  }

  private void applySchema(CqlSession session) {
    try {
      var res = new ClassPathResource("cassandra/schema.cql");
      if (res.exists()) {
        try (Scanner sc = new Scanner(res.getInputStream(), StandardCharsets.UTF_8)) {
          sc.useDelimiter(";");
          while (sc.hasNext()) {
            String stmt = sc.next().trim();
            if (!stmt.isEmpty()) session.execute(stmt);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to apply schema.cql", e);
    }
  }
}
