package psansa.api.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CassandraProvider {
    private CqlSession session;

    @Produces @Singleton
    public CqlSession session() {
        if (session == null) {
            String cps = System.getProperty("psansa.cassandra.contact-points",
                System.getenv().getOrDefault("PSANSA_CASSANDRA_CONTACT_POINTS",
                    "127.0.0.1:9042"));
            String dc = System.getProperty("psansa.cassandra.local-dc",
                System.getenv().getOrDefault("PSANSA_CASSANDRA_LOCAL_DC", "datacenter1"));
            String keyspace = System.getProperty("psansa.cassandra.keyspace",
                System.getenv().getOrDefault("PSANSA_CASSANDRA_KEYSPACE", "sansa"));

            List<InetSocketAddress> addrs = new ArrayList<>();
            for (String cp : cps.split(",")) {
                String[] hp = cp.trim().split(":");
                addrs.add(new InetSocketAddress(hp[0], Integer.parseInt(hp[1])));
            }
            session = CqlSession.builder()
                .addContactPoints(addrs)
                .withLocalDatacenter(dc)
                .withKeyspace(keyspace)
                .build();
        }
        return session;
    }

    @PreDestroy
    void close() { if (session != null) session.close(); }
}
