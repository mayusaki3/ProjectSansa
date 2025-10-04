package com.sansa.auth.it;

import com.sansa.auth.model.Models.User;
import com.sansa.auth.repo.RepoInterfaces.IUserRepo;
import com.sansa.auth.repo.RepoInterfaces.ISessionRepo;
import com.sansa.auth.TestCassandraSlice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.CassandraContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Locale;
import java.util.UUID;

@SpringBootTest(
    classes = TestCassandraSlice.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("cassandra")
@Testcontainers
class CassandraDaoIT {

    // Cassandra 5 をテスト中だけ起動
    @Container
    static final CassandraContainer<?> CASSANDRA = new CassandraContainer<>("cassandra:5");

    // Spring の接続先を Testcontainers の情報で上書き
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("sansa.cassandra.contactPoint", CASSANDRA::getHost);
        r.add("sansa.cassandra.port", CASSANDRA::getFirstMappedPort);
        r.add("sansa.cassandra.datacenter", () -> "datacenter1");
        r.add("sansa.cassandra.keyspace", () -> "sansa_auth");
    }

    @Autowired
    IUserRepo users;

    @Autowired
    ISessionRepo sessions;

    @Test
    void contextLoads_andCassandraConnected() {
        // system_schema から keyspace の存在を確認
        // （CassandraConfig#applySchema が動いていれば作成済み）
        // 直接 CqlSession を注入しない構成なら、ユーザCRUDで間接確認でもOK。
        Assertions.assertDoesNotThrow(() -> users.findByEmail("not-exist@example.com"));
    }

    @Test
    void userRepo_roundTrip_saveAndFind() {
        String account = "ituser_" + UUID.randomUUID().toString().substring(0, 8);
        String email = account + "@example.com";

        User u = new User();
        u.accountId = account;
        u.email = email.toLowerCase(Locale.ROOT);
        u.emailVerified = true;
        users.save(u);

        var f1 = users.findByAccountId(account).orElseThrow();
        Assertions.assertEquals(email.toLowerCase(Locale.ROOT), f1.email);

        var f2 = users.findByEmail(email).orElseThrow();
        Assertions.assertEquals(account, f2.accountId);
    }
}
