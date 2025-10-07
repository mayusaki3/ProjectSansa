package com.sansa.auth.repo.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetSocketAddress;

@Configuration
@Profile("cassandra")
@EnableConfigurationProperties(CassandraProps.class)
public class CassandraConfig {
    private final CassandraProps p;
    public CassandraConfig(CassandraProps p) { this.p = p; }

    @Bean
    public CqlSession cqlSession() {
        // 1) system セッション（keyspace なし）で KS を作成
        try (var sys = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(p.getContactPoint(), p.getPort()))
                .withLocalDatacenter(p.getDatacenter())
                .build()) {
            sys.execute("CREATE KEYSPACE IF NOT EXISTS " + p.getKeyspace() +
                    " WITH replication = {'class':'SimpleStrategy','replication_factor':1}");
        }
        // 2) keyspace を指定して本セッション（Bean）を返す
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(p.getContactPoint(), p.getPort()))
                .withLocalDatacenter(p.getDatacenter())
                .withKeyspace(p.getKeyspace())
                .build();
    }
}
