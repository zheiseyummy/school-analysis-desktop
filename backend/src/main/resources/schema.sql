-- SQLite core schema for the local single-user application.
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS sys_grade (
  id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT NOT NULL, name TEXT NOT NULL,
  sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, manager_id INTEGER,
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
CREATE TABLE IF NOT EXISTS sys_score (
  id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL, grade_id INTEGER,
  grade_name TEXT, clazz_id INTEGER, clazz_name TEXT, student_id INTEGER NOT NULL,
  course_id INTEGER NOT NULL, teacher_id INTEGER, score REAL, degree INTEGER,
  deleted INTEGER DEFAULT 0, create_time TEXT, update_time TEXT
);
CREATE TABLE IF NOT EXISTS sys_arrange (
  id INTEGER PRIMARY KEY AUTOINCREMENT, clazz_id INTEGER, course_id INTEGER,
  teacher_id INTEGER, sort INTEGER DEFAULT 0, status INTEGER DEFAULT 1, remark TEXT
);
CREATE TABLE IF NOT EXISTS sys_archives (
  id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER, teacher_id INTEGER,
  admin_id INTEGER, content TEXT, create_time TEXT, update_time TEXT, deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_score_exam_student ON sys_score(exam_id, student_id);
CREATE INDEX IF NOT EXISTS idx_score_exam_course ON sys_score(exam_id, course_id);
CREATE INDEX IF NOT EXISTS idx_clazz_student_student ON sys_clazz_student(student_id, year);
CREATE INDEX IF NOT EXISTS idx_clazz_student_clazz ON sys_clazz_student(clazz_id, year);
