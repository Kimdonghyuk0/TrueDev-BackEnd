package com.kdh.truedev.config;

import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final DataSourceProperties properties;

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource real = properties.initializeDataSourceBuilder().build();
        return ProxyDataSourceBuilder
                .create(real)
                .name("DS-Proxy")
                .countQuery()           // 쿼리 카운트 ThreadLocal에 누적
                .build();
    }
}
