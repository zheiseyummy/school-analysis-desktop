package com.youlai.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

@Service
public class LocalDatabaseBackupService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String BACKUP_FILE_PATTERN = "school_\\d{8}_\\d{6}\\.db";
    private final DataSource dataSource;
    @Value("${spring.datasource.url:jdbc:sqlite:./data/school.db}")
    private String datasourceUrl;

    public LocalDatabaseBackupService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void backupOnStartup() { backup(); }

    public synchronized Path backup() {
        if (!isFileBackedDatabase()) return null;
        try {
            Path database = databasePath();
            if (!Files.exists(database)) return null;
            checkpointDatabase();
            Path dir = database.getParent().resolve("backup");
            Files.createDirectories(dir);
            Path target = dir.resolve("school_" + LocalDateTime.now().format(FORMATTER) + ".db");
            Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
            Files.copy(database, temporary, StandardCopyOption.REPLACE_EXISTING);
            verifyIntegrity(temporary);
            moveReplace(temporary, target);
            List<Path> backups;
            try (var stream = Files.list(dir)) { backups = stream.filter(p -> p.getFileName().toString().matches(BACKUP_FILE_PATTERN)).sorted(Comparator.comparing(Path::toString).reversed()).toList(); }
            backups.stream().skip(30).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
            return target;
        } catch (IOException | SQLException e) { throw new IllegalStateException("本地数据库备份失败", e); }
    }

    public List<String> listBackups() {
        if (!isFileBackedDatabase()) return List.of();
        try {
            Path dir = databasePath().getParent().resolve("backup");
            if (!Files.exists(dir)) return List.of();
            try (var stream = Files.list(dir)) { return stream.filter(p -> p.getFileName().toString().matches(BACKUP_FILE_PATTERN)).sorted(Comparator.comparing(Path::toString).reversed()).map(p -> p.getFileName().toString()).collect(Collectors.toList()); }
        } catch (IOException e) { throw new IllegalStateException("读取备份列表失败", e); }
    }

    public synchronized void restore(String fileName) {
        if (!isFileBackedDatabase()) throw new IllegalStateException("当前数据库不是本地文件，无法恢复备份");
        try {
            if (fileName == null || !fileName.matches(BACKUP_FILE_PATTERN)) throw new IllegalArgumentException("备份文件名无效");
            Path source = databasePath().getParent().resolve("backup").resolve(fileName).normalize();
            if (!Files.isRegularFile(source)) throw new IllegalArgumentException("备份文件不存在");
            verifyIntegrity(source);
            // 恢复前自动保留当前库，避免误操作后无法回退。
            backup();
            // SQLite 文件通常仍被 Hikari/SQLite 连接占用，Windows 下直接替换文件会失败。
            // 使用 sqlite-jdbc 提供的 backup/restore 扩展把源库恢复到当前连接，避免文件句柄冲突。
            restoreInPlace(source);
            verifyIntegrity(databasePath());
        } catch (IOException | SQLException e) { throw new IllegalStateException("恢复数据库失败", e); }
    }

    private void checkpointDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA wal_checkpoint(TRUNCATE)");
        }
    }

    private void verifyIntegrity(Path path) throws SQLException {
        String url = "jdbc:sqlite:" + path.toAbsolutePath().normalize();
        try (Connection connection = DriverManager.getConnection(url); Statement statement = connection.createStatement(); var result = statement.executeQuery("PRAGMA integrity_check")) {
            if (!result.next() || !"ok".equalsIgnoreCase(result.getString(1))) throw new IllegalStateException("SQLite 数据库完整性检查失败");
        }
    }

    private void moveReplace(Path source, Path target) throws IOException {
        try { Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException | AccessDeniedException ignored) {
            // Windows 可能因 SQLite 连接仍持有数据库文件句柄而拒绝 move；覆盖复制可以安全替换主库，随后刷新连接池。
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(source);
        }
    }

    private void evictPoolConnections() {
        if (dataSource instanceof HikariDataSource hikari && hikari.getHikariPoolMXBean() != null) hikari.getHikariPoolMXBean().softEvictConnections();
    }

    private void restoreInPlace(Path source) throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            String escapedSource = source.toAbsolutePath().toString().replace("'", "''");
            statement.execute("restore main from '" + escapedSource + "'");
        } catch (SQLException restoreException) {
            // 对不支持扩展命令的驱动保留文件替换兜底路径。
            Path temporary = databasePath().resolveSibling(databasePath().getFileName() + ".restore.tmp");
            Files.deleteIfExists(temporary);
            Files.copy(source, temporary, StandardCopyOption.REPLACE_EXISTING);
            verifyIntegrity(temporary);
            evictPoolConnections();
            moveReplace(temporary, databasePath());
        }
    }

    private Path databasePath() {
        String url = datasourceUrl.substring("jdbc:sqlite:".length());
        return Paths.get(url).toAbsolutePath().normalize();
    }

    private boolean isFileBackedDatabase() {
        return datasourceUrl != null && datasourceUrl.startsWith("jdbc:sqlite:");
    }
}
