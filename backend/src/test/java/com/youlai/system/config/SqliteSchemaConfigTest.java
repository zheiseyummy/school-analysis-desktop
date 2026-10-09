package com.youlai.system.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sqlite.SQLiteDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.youlai.system.service.LocalDatabaseBackupService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class SqliteSchemaConfigTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void upgradesLegacyDatabasePreservesDataAndIsRepeatable() throws Exception {
        Path database = temporaryDirectory.resolve("legacy.db");
        String url = "jdbc:sqlite:" + database;
        createLegacyDatabase(url);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        SqliteSchemaConfig config = new SqliteSchemaConfig(dataSource);
        config.initialize();

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM app_schema_version"))
                    .isEqualTo(SqliteSchemaConfig.CURRENT_SCHEMA_VERSION);
            assertThat(singleInt(statement, "PRAGMA user_version"))
                    .isEqualTo(SqliteSchemaConfig.CURRENT_SCHEMA_VERSION);
            assertThat(singleString(statement, "SELECT name FROM sys_grade WHERE id=1")).isEqualTo("旧年级");
            assertThat(singleString(statement, "SELECT stage FROM sys_grade WHERE id=1")).isEqualTo("高中");
            assertThat(singleString(statement, "SELECT status FROM sys_score WHERE id=1")).isEqualTo("NORMAL");
            assertThat(singleInt(statement, "SELECT count_in_total FROM sys_exam_course WHERE id=1")).isEqualTo(1);
            assertThat(singleString(statement, "SELECT source_sheet FROM quality_final_scope WHERE id=1")).isEqualTo("九下");
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sys_dict WHERE type_code='semester' AND deleted=0")).isEqualTo(2);
            assertThat(hasColumn(statement, "quality_final_result", "available_terms")).isTrue();
            assertThat(hasColumn(statement, "quality_final_result", "contains_na")).isTrue();
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='local_score_dataset'")).isEqualTo(1);
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='local_score_value'")).isEqualTo(1);
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name LIKE 'local_quality_%'")).isEqualTo(7);
        }

        Path backupDirectory = temporaryDirectory.resolve("backup");
        long backupsBeforeRepeat;
        try (var files = Files.list(backupDirectory)) {
            backupsBeforeRepeat = files.filter(path -> path.getFileName().toString().startsWith("school_pre_migration_")).count();
        }
        assertThat(backupsBeforeRepeat).isEqualTo(1);
        LocalDatabaseBackupService backupService = new LocalDatabaseBackupService(dataSource);
        ReflectionTestUtils.setField(backupService, "datasourceUrl", url);
        assertThat(backupService.listBackups()).singleElement()
                .asString().startsWith("school_pre_migration_v0_to_v7_");

        config.initialize();
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM app_schema_version"))
                    .isEqualTo(SqliteSchemaConfig.CURRENT_SCHEMA_VERSION);
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sys_grade WHERE id=1")).isEqualTo(1);
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sys_dict WHERE type_code='semester' AND deleted=0")).isEqualTo(2);
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name LIKE 'local_score_%'")).isEqualTo(4);
        }
        try (var files = Files.list(backupDirectory)) {
            assertThat(files.filter(path -> path.getFileName().toString().startsWith("school_pre_migration_")).count())
                    .isEqualTo(backupsBeforeRepeat);
        }
    }

    @Test
    void initializesNewDatabaseWithoutCreatingMeaninglessBackup() throws Exception {
        Path database = temporaryDirectory.resolve("new.db");
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + database);

        new SqliteSchemaConfig(dataSource).initialize();

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM app_schema_version"))
                    .isEqualTo(SqliteSchemaConfig.CURRENT_SCHEMA_VERSION);
            assertThat(singleString(statement, "SELECT dflt_value FROM pragma_table_info('quality_final_scope') WHERE name='source_sheet'"))
                    .contains("九下");
            assertThat(singleInt(statement, "SELECT COUNT(*) FROM sys_dict WHERE type_code='semester' AND deleted=0")).isEqualTo(2);
            assertThat(singleString(statement, "SELECT name FROM sys_dict WHERE type_code='semester' AND value='1' AND deleted=0")).isEqualTo("上学期");
        }
        assertThat(Files.exists(temporaryDirectory.resolve("backup"))).isFalse();
    }

    private static void createLegacyDatabase(String url) throws Exception {
        try (Connection connection = DriverManager.getConnection(url); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE sys_grade (id INTEGER PRIMARY KEY, code TEXT, name TEXT, sort INTEGER, status INTEGER, manager_id INTEGER, deleted INTEGER, create_time TEXT, update_time TEXT)");
            statement.execute("INSERT INTO sys_grade(id,code,name) VALUES(1,'G1','旧年级')");
            statement.execute("CREATE TABLE sys_score (id INTEGER PRIMARY KEY, exam_id INTEGER, grade_id INTEGER, grade_name TEXT, clazz_id INTEGER, clazz_name TEXT, student_id INTEGER, course_id INTEGER, teacher_id INTEGER, score REAL, degree INTEGER, deleted INTEGER, create_time TEXT, update_time TEXT)");
            statement.execute("INSERT INTO sys_score(id,exam_id,student_id,course_id,score) VALUES(1,1,1,1,0)");
            statement.execute("CREATE TABLE sys_exam_course (id INTEGER PRIMARY KEY, exam_id INTEGER, course_id INTEGER, full_score REAL, sort INTEGER, UNIQUE(exam_id,course_id))");
            statement.execute("INSERT INTO sys_exam_course(id,exam_id,course_id,full_score) VALUES(1,1,1,100)");
            statement.execute("CREATE TABLE quality_finalization (id INTEGER PRIMARY KEY, clazz_id INTEGER UNIQUE, is_locked INTEGER, generated_at TEXT, locked_at TEXT, create_time TEXT, update_time TEXT)");
            statement.execute("CREATE TABLE quality_final_result (id INTEGER PRIMARY KEY, clazz_id INTEGER, student_id INTEGER, dimension TEXT, cumulative_score REAL, class_rank INTEGER, automatic_level TEXT, final_level TEXT, is_manually_adjusted INTEGER, UNIQUE(student_id,dimension))");
            statement.execute("CREATE TABLE quality_final_scope (id INTEGER PRIMARY KEY, clazz_id INTEGER, student_id INTEGER, source_sheet TEXT NOT NULL DEFAULT '九上', create_time TEXT, update_time TEXT, UNIQUE(clazz_id,student_id))");
            statement.execute("INSERT INTO quality_final_scope(id,clazz_id,student_id,source_sheet) VALUES(1,1,1,'九上')");
        }
    }

    private static int singleInt(Statement statement, String sql) throws Exception {
        try (ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1);
        }
    }

    private static String singleString(Statement statement, String sql) throws Exception {
        try (ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getString(1);
        }
    }

    private static boolean hasColumn(Statement statement, String table, String column) throws Exception {
        try (ResultSet result = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (result.next()) {
                if (column.equalsIgnoreCase(result.getString("name"))) return true;
            }
            return false;
        }
    }
}
