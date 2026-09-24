package com.youlai.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class LocalDatabaseBackupService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    @Value("${spring.datasource.url:jdbc:sqlite:./data/school.db}")
    private String datasourceUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void backupOnStartup() { backup(); }

    public synchronized Path backup() {
        try {
            Path database = databasePath();
            if (!Files.exists(database)) return null;
            Path dir = database.getParent().resolve("backup");
            Files.createDirectories(dir);
            Path target = dir.resolve("school_" + LocalDateTime.now().format(FORMATTER) + ".db");
            Files.copy(database, target, StandardCopyOption.REPLACE_EXISTING);
            List<Path> backups;
            try (var stream = Files.list(dir)) { backups = stream.filter(p -> p.getFileName().toString().endsWith(".db")).sorted(Comparator.comparing(Path::toString).reversed()).toList(); }
            backups.stream().skip(30).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
            return target;
        } catch (IOException e) { throw new IllegalStateException("本地数据库备份失败", e); }
    }

    private Path databasePath() {
        String url = datasourceUrl.replace("jdbc:sqlite:", "");
        return Paths.get(url).toAbsolutePath().normalize();
    }
}
