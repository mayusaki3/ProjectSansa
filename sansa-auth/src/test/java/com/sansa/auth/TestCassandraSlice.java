package com.sansa.auth;

import com.sansa.auth.repo.cassandra.CassandraConfig;
import com.sansa.auth.repo.cassandra.CassandraRepos;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { ServletWebServerFactoryAutoConfiguration.class })
@ComponentScan(
    basePackageClasses = { CassandraConfig.class, CassandraRepos.class },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.sansa\\.auth\\.controller\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.sansa\\.auth\\.service\\..*")
    }
)
public class TestCassandraSlice { }
