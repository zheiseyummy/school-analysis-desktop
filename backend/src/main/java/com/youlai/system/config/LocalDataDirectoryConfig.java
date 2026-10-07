package com.youlai.system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Creates writable runtime directories outside the future desktop installation directory. */
@Configuration
public class LocalDataDirectoryConfig {
    private final Path storageRoot;
    private final String datasourceUrl;
    private final Path filesDirectory;
    private final Path logDirectory;

    public LocalDataDirectoryConfig(
            @Value("${app.storage.root:.}") String storageRoot,
            @Value("${spring.datasource.url:jdbc:sqlite:./data/school.db}") String datasourceUrl,
            @Value("${oss.local.directory:files}") String filesDirectory,
            @Value("${logging.file.path:logs}") String logDirectory) {
        this.storageRoot = normalize(storageRoot);
        this.datasourceUrl = datasourceUrl;
        this.filesDirectory = normalize(filesDirectory);
        this.logDirectory = normalize(logDirectory);
    }

    @PostConstruct
    public void createDirectories() throws IOException {
        Files.createDirectories(storageRoot.resolve("data"));
        Files.createDirectories(storageRoot.resolve("backup"));
        Files.createDirectories(storageRoot.resolve("export"));
        Files.createDirectories(filesDirectory);
        Files.createDirectories(logDirectory);
        Path databaseParent = sqliteDatabaseParent(datasourceUrl);
        if (databaseParent != null) Files.createDirectories(databaseParent);
    }

    static Path normalize(String value) {
        String path = value == null || value.isBlank() ? "." : value;
        return Paths.get(path).toAbsolutePath().normalize();
    }

    static Path sqliteDatabaseParent(String url) {
        if (url == null || !url.startsWith("jdbc:sqlite:") || url.contains(":memory:")) return null;
        String path = url.substring("jdbc:sqlite:".length());
        if (path.isBlank()) return null;
        return normalize(path).getParent();
    }
}
