package com.youlai.system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Initializes and upgrades the portable SQLite schema. */
@Component
@ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "org.sqlite.JDBC")
public class SqliteSchemaConfig {
    static final int CURRENT_SCHEMA_VERSION = 5;
    private static final DateTimeFormatter BACKUP_TIME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final String VERSION_TABLE = "app_schema_version";

    private final DataSource dataSource;

    public SqliteSchemaConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initialize() {
        try (Connection connection = dataSource.getConnection()) {
            enableForeignKeys(connection);
            Set<Integer> appliedVersions = appliedVersions(connection);
            rejectNewerDatabase(appliedVersions);
            List<Migration> pending = MIGRATIONS.stream()
                    .filter(migration -> !appliedVersions.contains(migration.version()))
                    .toList();
            if (!pending.isEmpty() && hasApplicationTables(connection)) {
                createPreMigrationBackup(connection, latestAppliedVersion(appliedVersions));
            }
            ensureVersionTable(connection);
            for (Migration migration : pending) {
                applyMigration(connection, migration);
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("SQLite schema migration failed", e);
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        populator.execute(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            enableForeignKeys(connection);
            validateCurrentSchema(connection);
            execute(connection, "PRAGMA user_version = " + CURRENT_SCHEMA_VERSION);
        } catch (SQLException e) {
            throw new IllegalStateException("SQLite schema validation failed", e);
        }
    }

    private static final List<Migration> MIGRATIONS = List.of(
            new Migration(1, "年级学段与成绩状态", connection -> {
                addColumnIfMissing(connection, "sys_grade", "stage", "TEXT DEFAULT '高中'");
                addColumnIfMissing(connection, "sys_score", "status", "TEXT DEFAULT 'NORMAL'");
                addColumnIfMissing(connection, "sys_score", "scaled_score", "REAL");
                if (tableExists(connection, "sys_grade")) {
                    execute(connection, "UPDATE sys_grade SET stage = '高中' WHERE stage IS NULL OR stage = ''");
                }
                if (tableExists(connection, "sys_score")) {
                    execute(connection, "UPDATE sys_score SET status = 'NORMAL' WHERE status IS NULL AND score IS NOT NULL");
                }
            }),
            new Migration(2, "考试科目配置与高中选科框架", connection -> {
                addColumnIfMissing(connection, "sys_exam_course", "count_in_total", "INTEGER DEFAULT 1");
                addColumnIfMissing(connection, "sys_exam_course", "score_mode", "TEXT DEFAULT 'ORIGINAL'");
                addColumnIfMissing(connection, "sys_exam_course", "scoring_rule_id", "INTEGER");
                execute(connection, "CREATE TABLE IF NOT EXISTS sys_score_rule (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, method TEXT NOT NULL DEFAULT 'PENDING', target_full_score REAL, config_json TEXT, status INTEGER DEFAULT 1, remark TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0)");
                execute(connection, "CREATE TABLE IF NOT EXISTS sys_student_subject_selection_version (id INTEGER PRIMARY KEY AUTOINCREMENT, grade_id INTEGER NOT NULL, name TEXT NOT NULL, effective_date TEXT, status INTEGER DEFAULT 1, note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0)");
                execute(connection, "CREATE TABLE IF NOT EXISTS sys_student_subject_selection (id INTEGER PRIMARY KEY AUTOINCREMENT, version_id INTEGER NOT NULL, student_id INTEGER NOT NULL, track_course_id INTEGER, elective_course_ids TEXT, combination_code TEXT NOT NULL, source TEXT DEFAULT 'MANUAL', note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0, UNIQUE(version_id, student_id))");
                if (tableExists(connection, "sys_exam_course")) {
                    execute(connection, "UPDATE sys_exam_course SET count_in_total = 1 WHERE count_in_total IS NULL");
                    execute(connection, "UPDATE sys_exam_course SET score_mode = 'ORIGINAL' WHERE score_mode IS NULL OR score_mode = ''");
                }
            }),
            new Migration(3, "成绩导入日志与学生跟进记录", connection -> {
                execute(connection, "CREATE TABLE IF NOT EXISTS sys_score_import_log (id INTEGER PRIMARY KEY AUTOINCREMENT, batch_id TEXT NOT NULL, exam_id INTEGER NOT NULL, file_name TEXT, row_number INTEGER, student_code TEXT, student_name TEXT, course_name TEXT, student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, action TEXT NOT NULL, before_json TEXT, after_json TEXT, undone INTEGER DEFAULT 0, create_time TEXT, update_time TEXT)");
                execute(connection, "CREATE TABLE IF NOT EXISTS sys_student_followup (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, learning_status TEXT NOT NULL, special_situation TEXT, followup_content TEXT NOT NULL, next_action TEXT, followup_date TEXT NOT NULL, deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT)");
            }),
            new Migration(4, "综合素质最终范围与缺失确认", connection -> {
                execute(connection, "CREATE TABLE IF NOT EXISTS quality_final_scope (id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL, source_sheet TEXT NOT NULL DEFAULT '九下', create_time TEXT, update_time TEXT, UNIQUE(clazz_id, student_id))");
                execute(connection, "CREATE TABLE IF NOT EXISTS quality_missing_review (id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL, semester TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', missing_dimensions TEXT, remark TEXT, updated_at TEXT, UNIQUE(clazz_id, student_id, semester))");
                addColumnIfMissing(connection, "quality_finalization", "a_ratio", "REAL DEFAULT 0.60");
                addColumnIfMissing(connection, "quality_finalization", "b_ratio", "REAL DEFAULT 0.35");
                addColumnIfMissing(connection, "quality_finalization", "c_ratio", "REAL DEFAULT 0.05");
                addColumnIfMissing(connection, "quality_final_result", "available_terms", "INTEGER DEFAULT 0");
                addColumnIfMissing(connection, "quality_final_result", "contains_na", "INTEGER DEFAULT 0");
            }),
            new Migration(5, "最终学期范围口径修正", connection -> {
                if (tableExists(connection, "quality_final_scope")) {
                    execute(connection, "UPDATE quality_final_scope SET source_sheet = '九下' WHERE source_sheet IS NULL OR source_sheet = '' OR source_sheet = '九上'");
                }
            })
    );

    private static void applyMigration(Connection connection, Migration migration) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            migration.action().apply(connection);
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + VERSION_TABLE + "(version, description, installed_at) VALUES(?,?,?)")) {
                statement.setInt(1, migration.version());
                statement.setString(2, migration.description());
                statement.setString(3, LocalDateTime.now().toString());
                statement.executeUpdate();
            }
            execute(connection, "PRAGMA user_version = " + migration.version());
            connection.commit();
        } catch (SQLException exception) {
            connection.rollback();
            throw new SQLException("执行 SQLite 升级 V" + migration.version() + "（" + migration.description() + "）失败", exception);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static void addColumnIfMissing(Connection connection, String table, String column, String definition) throws SQLException {
        if (tableExists(connection, table) && !columnExists(connection, table, column)) {
            execute(connection, "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?")) {
            statement.setString(1, table);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }
        }
    }

    private static boolean columnExists(Connection connection, String table, String column) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (result.next()) {
                if (column.equalsIgnoreCase(result.getString("name"))) return true;
            }
            return false;
        }
    }

    private static Set<Integer> appliedVersions(Connection connection) throws SQLException {
        if (!tableExists(connection, VERSION_TABLE)) return Set.of();
        Set<Integer> versions = new HashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT version FROM " + VERSION_TABLE + " ORDER BY version")) {
            while (result.next()) versions.add(result.getInt(1));
        }
        return versions;
    }

    private static void ensureVersionTable(Connection connection) throws SQLException {
        execute(connection, "CREATE TABLE IF NOT EXISTS " + VERSION_TABLE + " (version INTEGER PRIMARY KEY, description TEXT NOT NULL, installed_at TEXT NOT NULL)");
    }

    private static boolean hasApplicationTables(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name<>?")) {
            statement.setString(1, VERSION_TABLE);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }
        }
    }

    private static int latestAppliedVersion(Set<Integer> versions) {
        return versions.stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    private static void rejectNewerDatabase(Set<Integer> versions) {
        int latest = latestAppliedVersion(versions);
        if (latest > CURRENT_SCHEMA_VERSION) {
            throw new IllegalStateException("数据库版本 V" + latest + " 高于当前程序支持的 V" + CURRENT_SCHEMA_VERSION + "，请使用更新版本的程序");
        }
    }

    private static void enableForeignKeys(Connection connection) throws SQLException {
        execute(connection, "PRAGMA foreign_keys = ON");
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void createPreMigrationBackup(Connection connection, int fromVersion) throws SQLException, IOException {
        String url = connection.getMetaData().getURL();
        if (url == null || !url.startsWith("jdbc:sqlite:") || url.contains(":memory:")) return;
        String rawPath = url.substring("jdbc:sqlite:".length());
        if (rawPath.isBlank()) return;
        Path database = Paths.get(rawPath).toAbsolutePath().normalize();
        if (!Files.isRegularFile(database)) return;
        execute(connection, "PRAGMA wal_checkpoint(TRUNCATE)");
        Path parent = database.getParent();
        if (parent == null) return;
        Path backupDirectory = parent.resolve("backup");
        Files.createDirectories(backupDirectory);
        Path target = backupDirectory.resolve("school_pre_migration_v" + fromVersion + "_to_v" + CURRENT_SCHEMA_VERSION
                + "_" + LocalDateTime.now().format(BACKUP_TIME) + ".db");
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.copy(database, temporary, StandardCopyOption.REPLACE_EXISTING);
        verifyIntegrity(temporary);
        Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void verifyIntegrity(Path database) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("PRAGMA integrity_check")) {
            if (!result.next() || !"ok".equalsIgnoreCase(result.getString(1))) {
                throw new SQLException("升级前数据库备份完整性检查失败");
            }
        }
    }

    private static void validateCurrentSchema(Connection connection) throws SQLException {
        List<String> missing = new ArrayList<>();
        requireColumns(connection, missing, "sys_grade", "stage");
        requireColumns(connection, missing, "sys_score", "status", "scaled_score");
        requireColumns(connection, missing, "sys_exam_course", "count_in_total", "score_mode", "scoring_rule_id");
        requireColumns(connection, missing, "quality_finalization", "a_ratio", "b_ratio", "c_ratio");
        requireColumns(connection, missing, "quality_final_result", "available_terms", "contains_na");
        for (String table : List.of("sys_score_import_log", "sys_student_followup", "sys_score_rule",
                "sys_student_subject_selection_version", "sys_student_subject_selection",
                "quality_final_scope", "quality_missing_review")) {
            if (!tableExists(connection, table)) missing.add(table);
        }
        if (!missing.isEmpty()) throw new SQLException("SQLite 结构不完整：" + String.join(", ", missing));
    }

    private static void requireColumns(Connection connection, List<String> missing, String table, String... columns) throws SQLException {
        if (!tableExists(connection, table)) {
            missing.add(table);
            return;
        }
        for (String column : columns) {
            if (!columnExists(connection, table, column)) missing.add(table + "." + column);
        }
    }

    @FunctionalInterface
    private interface MigrationAction {
        void apply(Connection connection) throws SQLException;
    }

    private record Migration(int version, String description, MigrationAction action) { }
}
