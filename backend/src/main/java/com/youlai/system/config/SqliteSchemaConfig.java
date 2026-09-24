package com.youlai.system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/** Initializes the portable SQLite schema only when the production datasource is SQLite. */
@Component
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "org.sqlite.JDBC")
public class SqliteSchemaConfig {
    private final DataSource dataSource;

    public SqliteSchemaConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initialize() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.execute(dataSource);
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            try { statement.execute("ALTER TABLE sys_grade ADD COLUMN stage TEXT DEFAULT '高中'"); } catch (SQLException ignored) { }
        } catch (SQLException e) {
            throw new IllegalStateException("SQLite schema migration failed", e);
        }
    }
}
