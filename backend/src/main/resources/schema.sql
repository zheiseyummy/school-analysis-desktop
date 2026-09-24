-- SQLite core schema for the local single-user application.
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS sys_grade (
  id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT NOT NULL, name TEXT NOT NULL,
  sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, manager_id INTEGER, stage TEXT DEFAULT '高中',
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_clazz (
  id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT NOT NULL, name TEXT NOT NULL,
  sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, manager_id INTEGER,
  grade_id INTEGER, clazz_type TEXT, deleted INTEGER DEFAULT 0,
  create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_student (
  id INTEGER PRIMARY KEY AUTOINCREMENT, account TEXT NOT NULL, password TEXT NOT NULL,
  code TEXT NOT NULL UNIQUE, name TEXT NOT NULL, sex INTEGER NOT NULL, status INTEGER DEFAULT 1,
  birth_day TEXT, year INTEGER, phone TEXT, avatar TEXT, deleted INTEGER DEFAULT 0,
  remark TEXT, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_teacher (
  id INTEGER PRIMARY KEY AUTOINCREMENT, account TEXT NOT NULL, password TEXT NOT NULL,
  code TEXT NOT NULL UNIQUE, name TEXT NOT NULL, sex INTEGER NOT NULL, status INTEGER DEFAULT 1,
  birth_day TEXT, year INTEGER, phone TEXT, avatar TEXT, deleted INTEGER DEFAULT 0,
  remark TEXT, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_course (
  id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, code TEXT,
  subject_type TEXT, full_score INTEGER, sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_clazz_student (
  student_id INTEGER NOT NULL, clazz_id INTEGER NOT NULL, year INTEGER NOT NULL,
  PRIMARY KEY (student_id, clazz_id, year)
);
CREATE TABLE IF NOT EXISTS sys_exam (
  id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, code TEXT,
  year INTEGER, semester INTEGER, exam_type TEXT, exam_date TEXT,
  sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, deleted INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sys_exam_body (
  id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL,
  grade_clazz_id INTEGER NOT NULL, g_or_c TEXT
);
CREATE TABLE IF NOT EXISTS sys_exam_course (
  id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL, course_id INTEGER NOT NULL,
    full_score REAL, count_in_total INTEGER DEFAULT 1, score_mode TEXT DEFAULT 'ORIGINAL', scoring_rule_id INTEGER, sort INTEGER DEFAULT 0, UNIQUE(exam_id, course_id)
);
CREATE TABLE IF NOT EXISTS sys_score_rule (
  id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, method TEXT NOT NULL DEFAULT 'PENDING',
  target_full_score REAL, config_json TEXT, status INTEGER DEFAULT 1, remark TEXT,
  create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sys_student_subject_selection_version (
  id INTEGER PRIMARY KEY AUTOINCREMENT, grade_id INTEGER NOT NULL, name TEXT NOT NULL,
  effective_date TEXT, status INTEGER DEFAULT 1, note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sys_student_subject_selection (
  id INTEGER PRIMARY KEY AUTOINCREMENT, version_id INTEGER NOT NULL, student_id INTEGER NOT NULL,
  track_course_id INTEGER, elective_course_ids TEXT, combination_code TEXT NOT NULL,
  source TEXT DEFAULT 'MANUAL', note TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0,
  UNIQUE(version_id, student_id)
);
CREATE TABLE IF NOT EXISTS quality_dimension (
  id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, sort_order INTEGER NOT NULL UNIQUE,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS quality_record (
  id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, semester TEXT NOT NULL,
  dimension TEXT NOT NULL, level_or_score TEXT NOT NULL, comment TEXT,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT,
  UNIQUE(student_id, semester, dimension)
);
CREATE TABLE IF NOT EXISTS quality_roster_entry (
  id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, semester TEXT NOT NULL,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT,
  UNIQUE(student_id, semester)
);
CREATE TABLE IF NOT EXISTS quality_final_scope (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL,
  source_sheet TEXT NOT NULL DEFAULT '九上', create_time TEXT, update_time TEXT,
  UNIQUE(clazz_id, student_id)
);
CREATE TABLE IF NOT EXISTS quality_missing_review (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL,
  semester TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', missing_dimensions TEXT,
  remark TEXT, updated_at TEXT, UNIQUE(clazz_id, student_id, semester)
);
CREATE TABLE IF NOT EXISTS quality_finalization (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL UNIQUE,
  is_locked INTEGER DEFAULT 0, generated_at TEXT, locked_at TEXT,
  a_ratio REAL DEFAULT 0.60, b_ratio REAL DEFAULT 0.35, c_ratio REAL DEFAULT 0.05,
  create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS quality_final_result (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER NOT NULL, student_id INTEGER NOT NULL,
  dimension TEXT NOT NULL, cumulative_score REAL NOT NULL, class_rank INTEGER NOT NULL,
  automatic_level TEXT NOT NULL, final_level TEXT NOT NULL, is_manually_adjusted INTEGER DEFAULT 0,
  available_terms INTEGER DEFAULT 0, contains_na INTEGER DEFAULT 0,
  UNIQUE(student_id, dimension)
);
CREATE INDEX IF NOT EXISTS idx_quality_record_student ON quality_record(student_id, semester);
CREATE INDEX IF NOT EXISTS idx_quality_final_class ON quality_final_result(clazz_id, dimension);
CREATE INDEX IF NOT EXISTS idx_quality_scope_class ON quality_final_scope(clazz_id, student_id);
CREATE INDEX IF NOT EXISTS idx_quality_missing_class ON quality_missing_review(clazz_id, status);
CREATE TABLE IF NOT EXISTS sys_score (
  id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL, grade_id INTEGER,
  grade_name TEXT, clazz_id INTEGER, clazz_name TEXT, student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL, teacher_id INTEGER, score REAL, scaled_score REAL, status TEXT DEFAULT 'NORMAL', degree INTEGER,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_score_import_log (
  id INTEGER PRIMARY KEY AUTOINCREMENT, batch_id TEXT NOT NULL, exam_id INTEGER NOT NULL,
  file_name TEXT, row_number INTEGER, student_code TEXT, student_name TEXT, course_name TEXT,
  student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, action TEXT NOT NULL,
  before_json TEXT, after_json TEXT, undone INTEGER DEFAULT 0,
  create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_arrange (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER, course_id INTEGER,
  teacher_id INTEGER, sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, remark TEXT
);
CREATE TABLE IF NOT EXISTS sys_archives (
  id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER, teacher_id INTEGER,
  admin_id INTEGER, content TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sys_student_followup (
  id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL,
  learning_status TEXT NOT NULL, special_situation TEXT, followup_content TEXT NOT NULL,
  next_action TEXT, followup_date TEXT NOT NULL, deleted INTEGER DEFAULT 0,
  create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_dict_type (
  id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, code TEXT NOT NULL UNIQUE,
  status INTEGER DEFAULT 1, remark TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sys_dict (
  id INTEGER PRIMARY KEY AUTOINCREMENT, type_code TEXT NOT NULL, name TEXT NOT NULL,
  value TEXT, sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, defaulted INTEGER DEFAULT 0,
  remark TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_score_exam_student ON sys_score(exam_id, student_id);
CREATE INDEX IF NOT EXISTS idx_score_exam_course ON sys_score(exam_id, course_id);
CREATE INDEX IF NOT EXISTS idx_score_exam_grade_clazz ON sys_score(exam_id, grade_id, clazz_id);
CREATE INDEX IF NOT EXISTS idx_score_student_course ON sys_score(student_id, course_id, exam_id);
CREATE INDEX IF NOT EXISTS idx_score_import_log_batch ON sys_score_import_log(batch_id, id);
CREATE INDEX IF NOT EXISTS idx_score_import_log_exam ON sys_score_import_log(exam_id, create_time);
CREATE INDEX IF NOT EXISTS idx_exam_body_exam_clazz ON sys_exam_body(exam_id, grade_clazz_id);
CREATE INDEX IF NOT EXISTS idx_exam_course_exam ON sys_exam_course(exam_id, course_id);
CREATE INDEX IF NOT EXISTS idx_score_rule_status ON sys_score_rule(status, id);
CREATE INDEX IF NOT EXISTS idx_selection_version_grade ON sys_student_subject_selection_version(grade_id, effective_date);
CREATE INDEX IF NOT EXISTS idx_selection_record_version ON sys_student_subject_selection(version_id, student_id);
CREATE INDEX IF NOT EXISTS idx_clazz_student_student ON sys_clazz_student(student_id, year);
CREATE INDEX IF NOT EXISTS idx_clazz_student_clazz ON sys_clazz_student(clazz_id, year);
CREATE INDEX IF NOT EXISTS idx_student_followup_student_date ON sys_student_followup(student_id, followup_date);
