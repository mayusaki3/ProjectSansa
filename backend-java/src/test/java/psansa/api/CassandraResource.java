package psansa.api;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.CassandraContainer;

public class CassandraResource implements BeforeAllCallback {
static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:5");

@Override
public void beforeAll(ExtensionContext context) {
    if (!cassandra.isRunning()) {
        cassandra.start();
        System.setProperty("psansa.cassandra.contact-points",
            cassandra.getHost() + ":" + cassandra.getFirstMappedPort());
        System.setProperty("psansa.cassandra.local-dc", "datacenter1");
        System.setProperty("psansa.cassandra.keyspace", "sansa");
        // init schema (簡易): CQL を流す or アプリ起動時の IF NOT EXISTS に依存
        }
    }
}
