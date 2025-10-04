package com.sansa.auth.repo.cassandra;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sansa.cassandra")
public class CassandraProps {
    private String contactPoint = "127.0.0.1";
    private int port = 9042;
    private String datacenter = "datacenter1";
    private String keyspace = "sansa_auth";

    public String getContactPoint() { return contactPoint; }
    public void setContactPoint(String contactPoint) { this.contactPoint = contactPoint; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getDatacenter() { return datacenter; }
    public void setDatacenter(String datacenter) { this.datacenter = datacenter; }

    public String getKeyspace() { return keyspace; }
    public void setKeyspace(String keyspace) { this.keyspace = keyspace; }
}
