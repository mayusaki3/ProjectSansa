package com.sansa.auth;

import com.sansa.auth.repo.cassandra.CassandraConfig;
import com.sansa.auth.repo.cassandra.CassandraRepos;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {
        // Webサーバ系は不要（Tomcat 等を起動しない）
        ServletWebServerFactoryAutoConfiguration.class
})
@ComponentScan(
        basePackageClasses = {
                CassandraConfig.class,  // CqlSession の bean
                CassandraRepos.class    // UserRepo / SessionRepo
        },
        excludeFilters = {
                // Controller / Service 層はテスト対象外にする
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.sansa\\.auth\\.controller\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.sansa\\.auth\\.service\\..*")
        }
)
public class TestCassandraSlice {
    // 何も書かなくてOK。スキャン設定だけ持つクラス。
}
