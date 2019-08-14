package com.simple.tracker.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class H2Config {
    @Bean
    @ConfigurationProperties("spring.datasource.test")
    public DataSource dataSource() throws SQLException {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
