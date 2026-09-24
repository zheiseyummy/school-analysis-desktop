package com.youlai.system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Creates the portable application's local data directories before the datasource opens. */
@Configuration
public class LocalDataDirectoryConfig {
    @PostConstruct
    public void createDirectories() throws IOException {
        Files.createDirectories(Path.of("data"));
        Files.createDirectories(Path.of("files"));
        Files.createDirectories(Path.of("backup"));
        Files.createDirectories(Path.of("export"));
    }
}
