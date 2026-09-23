export interface ClazzExamAnalysisQuery {
  /**
   * 年度
   */
  year?: string;

  /**
   * 学期
   */
  semester?: number;

  /**
   * 考试类型
   */
  examType?: string;

  /**
   * 年级ID
   */
  gradeId?: number;

  /**
   * 班级ID
   */
  clazzId?: number;

  /**
   * 考试ID
   */
  examId?: number;

  /**
   * 课程ID
   */
  courseId?: number;
}

interface Statics {
  /**
   * 最高分
   */
  maxScore?: number;

  /**
   * 最低分
   */
  minScore?: number;

  /**
   * 平均分
   */
  avgScore?: number;

  /**
   * 优秀个数
   */
  aCount?: number;
  /**
   * 良好个数
   */
  bCount?: number;

  /**
   * 中等个数
   */
  cCount?: number;
  /**
   * 合格个数
   */
  dCount?: number;
  /**
   * 不合格个数
   */
  eCount?: number;

  /**
   * 优秀率
   */
  aRate?: number;
  /**
   * 良好率
   */
  bRate?: number;
  /**
   * 中等率
   */
  cRate?: number;
  /**
   * 合格率
   */
  dRate?: number;
  /**
   * 不合格率
   */
  eRate?: number;
}

export interface CourseStaticsBO extends Statics {
  /**
   * 课程ID
   */
  courseId?: number;
  /**
   * 课程名称
   */
  courseName?: string;

  /**
   * 课程满分
   */
  fullScore?: number;
}

export interface ClazzStaticsBO extends Statics {
  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 班级名称
   */
  clazzName?: string;

  /**
   * 班级学生人数
   */
  clazzStudentCount?: number;
}

export interface CourseClazzStaticsBO extends Statics {
  [key: string]: any;
  /**
   * 课程ID
   */
  courseId?: number;
  /**
   * 课程名称
   */
  courseName?: string;

  /**
   * 课程满分
   */
  fullScore?: number;
  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 班级名称
   */
  clazzName?: string;

  /**
   * 班级学生人数
   */
  clazzStudentCount?: number;
}

export interface StudentCourseScoreBO {
  studentId?: number;
  studentCode?: string;
  studentName?: string;
  courseScoreList?: number[];
}

export interface StudentScoreAnalysisQuery {
  year?: number;
  clazzId?: number;
  studentId?: number;
  courseId?: number;
}

export interface ExamScoreBO {
  examId?: number;
  examDate?: string;
  examName?: string;
  score?: number;
}
