export interface ExamBodyQuery extends PageQuery {
  /**
   * 年度
   */
  year?: number;
  /**
   * 学期
   */
  semester?: number;

  /**
   * 年级ID
   */
  gradeId?: number;

  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 关键字
   */
  keywords?: string;

  /**
   * 考试类型
   */
  examType?: string;
}

export interface ExamBodyPageVO {
  id?: number;
  /**
   * 年度
   */
  year?: number;
  /**
   * 学期
   */
  semester?: number;

  /**
   * 学期中文
   */
  semesterStr?: string;
  /**
   * 考试ID
   */
  examId?: number;
  /**
   * 考试名称
   */
  examName?: string;

  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 班级名称
   */
  clazzName?: string;

  /**
   * 年级ID
   */
  gradeId?: number;

  /**
   * 年级名称
   */
  gradeName?: string;

  /**
   * 考试类型
   */
  examType?: string;

  /**
   * 考试类型中文
   */
  examTypeStr?: string;

  /**
   * 考试时间
   */
  examDate?: string;
  /**
   * 学生数量
   */
  studentCount?: number;
}

/**
 * 成绩录入查询对象
 */
export interface ScoreEntryQuery {
  /**
   * 考试主体ID
   */
  examBodyId?: number;
  /**
   * 课程ID
   */
  courseId?: number;
}

export interface ScoreEntryVO {
  /**
   * 成绩ID
   */
  scoreId?: number;

  /**
   * 学生ID
   */
  studentId?: number;

  /**
   * 学号
   */
  studentCode?: string;

  /**
   * 姓名
   */
  studentName?: string;

  /**
   * 成绩
   */
  score?: number;
}

/**
 * 保存成绩对象
 */
export interface ScoreEntryForm {
  examBodyId?: number;
  courseId?: number;
  scoreList?: ScoreEntryVO[];
}
