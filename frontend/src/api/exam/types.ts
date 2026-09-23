/**
 * 考试查询参数
 */
export interface ExamQuery extends PageQuery {
  keywords?: string;
  year?: string;
  examType?: string;
  semester?: number;
}

/**
 * 考试分页对象
 */
export interface ExamPageVO {
  /**
   * 考试编码
   */
  code?: string;

  /**
   * 考试ID
   */
  id?: number;
  /**
   * 考试名称
   */
  name?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 考试状态
   */
  status?: number;
  /**
   * 创建时间
   */
  createTime?: Date;
  /**
   * 修改时间
   */
  updateTime?: Date;
  /**
   * 年级班级数量
   */
  gradeClazzCount?: number;
  /**
   * 年级班级列表
   */
  gradeClazzList?: string;
  /**
   * 年度
   */
  year?: string;
  /**
   * 考试类型
   */
  examType?: string;
  /**
   * 考试类型中文
   */
  examTypeStr?: string;

  /**
   * 考试日期
   */
  examDate?: Date;
  /**
   * 样本数
   */
  sampleCount?: number;
  /**
   * 学期
   */
  semester?: number;
  semesterStr?: string;
}

/**
 * 考试表单对象
 */
export interface ExamForm {
  /**
   * 考试ID
   */
  id?: number;

  /**
   * 考试编码
   */
  code: string;

  /**
   * 考试名称
   */
  name: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 考试状态(1-正常；0-停用)
   */
  status?: number;
  /**
   * 年度
   */
  year?: string;
  /**
   * 考试类型
   */
  examType?: string;

  /**
   * 体检日期
   */
  examDate?: Date;

  /**
   * 学期
   */
  semester?: number;
}
