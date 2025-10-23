package com.sansa.auth.it;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class CassandraITBootstrap {

  private static final DockerImageName IMAGE = DockerImageName.parse("cassandra:5");

  static final CassandraContainer<?> cassandra = new CassandraContainer<>(IMAGE)
      .withEnv("CASSANDRA_DC", "datacenter1")
      .withStartupTimeout(Duration.ofMinutes(3));

  static {
    cassandra.start();
    applyCqlMigrations(); // ← 起動後に V*.cql を全部流す
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.cassandra.contact-points",
        () -> cassandra.getContactPoint().getAddress().getHostAddress() + ":" + cassandra.getFirstMappedPort());
    r.add("spring.cassandra.local-datacenter", () -> "datacenter1");
    r.add("spring.cassandra.keyspace-name", () -> "sansa_auth");
  }

  private static void applyCqlMigrations() {
    try (CqlSession session = CqlSession.builder()
        .addContactPoint(cassandra.getContactPoint())
        .withLocalDatacenter("datacenter1")
        .build()) {

      // classpath:/cql の V*.cql を昇順適用
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] scripts = resolver.getResources("classpath:/cql/V*.cql");
      List<Resource> sorted = Arrays.stream(scripts)
          .sorted(Comparator.comparing(r -> r.getFilename()))
          .collect(Collectors.toList());

      for (Resource res : sorted) {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {

          String cql = br.lines().collect(Collectors.joining("\n"));
          // -- コメント行と空白を除き、; 区切りで実行
          for (String stmt : cql.split(";")) {
            String s = stmt.replaceAll("(?m)^\\s*--.*$", "").trim();
            if (!s.isEmpty()) {
              session.execute(s + ";");
            }
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("CQL migration failed", e);
    }
  }
}
