package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

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
        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoint, port))
                .withLocalDatacenter(datacenter)
                .build();
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace +
                " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        session.execute("USE " + keyspace);
        runSchema(session);
        return session;
    }

    private void runSchema(CqlSession session) {
        try {
            Path p = Path.of("src/main/resources/cassandra/schema.cql");
            if (Files.exists(p)) {
                String cql = Files.readString(p);
                for (String stmt : cql.split(";")) {
                    String s = stmt.trim();
                    if (!s.isEmpty()) {
                        session.execute(s);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
