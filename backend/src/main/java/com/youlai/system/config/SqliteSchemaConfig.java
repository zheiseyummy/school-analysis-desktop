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
            try { statement.execute("UPDATE sys_grade SET stage = '高中' WHERE stage IS NULL OR stage = ''"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE sys_score ADD COLUMN status TEXT DEFAULT 'NORMAL'"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE sys_score ADD COLUMN scaled_score REAL"); } catch (SQLException ignored) { }
            try { statement.execute("UPDATE sys_score SET status = 'NORMAL' WHERE status IS NULL AND score IS NOT NULL"); } catch (SQLException ignored) { }
            statement.execute("CREATE TABLE IF NOT EXISTS sys_score_import_log (id INTEGER PRIMARY KEY AUTOINCREMENT, batch_id TEXT NOT NULL, exam_id INTEGER NOT NULL, file_name TEXT, row_number INTEGER, student_code TEXT, student_name TEXT, course_name TEXT, student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, action TEXT NOT NULL, before_json TEXT, after_json TEXT, undone INTEGER DEFAULT 0, create_time TEXT, update_time TEXT)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_score_import_log_batch ON sys_score_import_log(batch_id, id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_score_import_log_exam ON sys_score_import_log(exam_id, create_time)");
            statement.execute("CREATE TABLE IF NOT EXISTS sys_student_followup (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, learning_status TEXT NOT NULL, special_situation TEXT, followup_content TEXT NOT NULL, next_action TEXT, followup_date TEXT NOT NULL, deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_student_followup_student_date ON sys_student_followup(student_id, followup_date)");
            try { statement.execute("ALTER TABLE sys_exam_course ADD COLUMN count_in_total INTEGER DEFAULT 1"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE sys_exam_course ADD COLUMN score_mode TEXT DEFAULT 'ORIGINAL'"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE sys_exam_course ADD COLUMN scoring_rule_id INTEGER"); } catch (SQLException ignored) { }
            statement.execute("CREATE TABLE IF NOT EXISTS sys_score_rule (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, method TEXT NOT NULL DEFAULT 'PENDING', target_full_score REAL, config_json TEXT, status INTEGER DEFAULT 1, remark TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_score_rule_status ON sys_score_rule(status, id)");
            statement.execute("CREATE TABLE IF NOT EXISTS sys_student_subject_selection_version (id INTEGER PRIMARY KEY AUTOINCREMENT, grade_id INTEGER NOT NULL, name TEXT NOT NULL, effective_date TEXT, status INTEGER DEFAULT 1, note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0)");
            statement.execute("CREATE TABLE IF NOT EXISTS sys_student_subject_selection (id INTEGER PRIMARY KEY AUTOINCREMENT, version_id INTEGER NOT NULL, student_id INTEGER NOT NULL, track_course_id INTEGER, elective_course_ids TEXT, combination_code TEXT NOT NULL, source TEXT DEFAULT 'MANUAL', note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0, UNIQUE(version_id, student_id))");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_selection_version_grade ON sys_student_subject_selection_version(grade_id, effective_date)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_selection_record_version ON sys_student_subject_selection(version_id, student_id)");
            statement.execute("CREATE TABLE IF NOT EXISTS quality_final_scope (id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL, source_sheet TEXT NOT NULL DEFAULT '九下', create_time TEXT, update_time TEXT, UNIQUE(clazz_id, student_id))");
            statement.execute("CREATE TABLE IF NOT EXISTS quality_missing_review (id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL, semester TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', missing_dimensions TEXT, remark TEXT, updated_at TEXT, UNIQUE(clazz_id, student_id, semester))");
            try { statement.execute("ALTER TABLE quality_finalization ADD COLUMN a_ratio REAL DEFAULT 0.60"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE quality_finalization ADD COLUMN b_ratio REAL DEFAULT 0.35"); } catch (SQLException ignored) { }
            try { statement.execute("ALTER TABLE quality_finalization ADD COLUMN c_ratio REAL DEFAULT 0.05"); } catch (SQLException ignored) { }
            statement.execute("CREATE INDEX IF NOT EXISTS idx_quality_scope_class ON quality_final_scope(clazz_id, student_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_quality_missing_class ON quality_missing_review(clazz_id, status)");
        } catch (SQLException e) {
            throw new IllegalStateException("SQLite schema migration failed", e);
        }
    }
}
