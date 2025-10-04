package com.sansa.auth.it;

import com.sansa.auth.TestCassandraSlice;
import com.sansa.auth.repo.cassandra.CassandraRepos;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestCassandraSlice.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("cassandra")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CassandraDaoIT {

    @Autowired CassandraRepos.UserRepo userRepo;
    @Autowired CassandraRepos.SessionRepo sessionRepo;

    @Test @Order(1)
    void contextLoads_andCassandraConnected() {
        // ここは任意。Session/DDL 初期化が通れば OK とみなす。
        Assertions.assertNotNull(userRepo);
        Assertions.assertNotNull(sessionRepo);
    }

    @Test @Order(2)
    void user_and_session_crud() {
        // 既存テスト内容でOK
    }
}
