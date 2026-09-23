/**
 * 学情档案查询参数
 */
export interface ArchivesQuery extends PageQuery {
  keywords?: string;
  year?: number;
  clazzId?: number;
  studentId?: number;
}

/**
 * 学情档案分页对象
 */
export interface ArchivesPageVO {
  /**
   * 学情档案编码
   */
  code?: string;

  /**
   * 学情档案ID
   */
  id?: number;
  /**
   * 学情档案名称
   */
  name?: string;

  /**
   * 学习需求
   */
  learningNeeds?: string;

  /**
   * 学习动机
   */
  learningMotivation?: string;

  /**
   * 学校兴趣
   */
  learningInterests?: string;

  /**
   * 学习态度
   */
  learningAttitude?: string;

  /**
   * 学习自信心
   */
  learningSelfConfidence?: string;

  /**
   * 倾听
   */
  listening?: string;

  /**
   * 质疑
   */
  query?: string;

  /**
   * 独立思考
   */
  independentThinking?: string;

  /**
   * 小组合作意识
   */
  groupCooperationAwareness?: string;

  /**
   * 表达交流意识
   */
  expressingWillingnessToCommunicate?: string;

  /**
   * 记录意识
   */
  recordAwareness?: string;

  /**
   * 自我反思意识
   */
  selfReflectionConsciousness?: string;

  /**
   * 复习整理意识
   */
  reviewOrganizeAwareness?: string;

  /**
   * 预习意识
   */
  previewAwareness?: string;

  /**
   * 学科思维
   */
  disciplinaryThinking?: string;

  /**
   * 学科语言表达
   */
  subjectLanguageExpression?: string;

  /**
   * 课外学习
   */
  outOfClassActivities?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 学情档案状态
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
   * 年度
   */
  year?: number;

  /**
   * 年级ID
   */
  gradeId?: number;

  /**
   * 年级名称
   */
  gradeName?: string;

  /**
   * 班级ID
   */
  clazzId?: number;

  /**
   * 班级名称
   */
  clazzName?: string;

  /**
   * 学生ID
   */
  studentId?: number;

  /**
   * 学生名称
   */
  studentName?: string;

  /**
   * 教师ID
   */
  teacherId?: number;

  /**
   * 教师名称
   */
  teacherName?: string;
}

/**
 * 学情档案表单对象
 */
export interface ArchivesForm {
  /**
   * 学情档案ID
   */
  id?: number;

  /**
   * 年度
   */
  year?: number;

  /**
   * 年级ID
   */
  gradeId?: number;
  /**
   * 年级名称
   */
  gradeName?: string;

  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 班级名称
   */
  clazzName?: string;

  /**
   * 学生ID
   */
  studentId?: number;

  /**
   * 学生姓名
   */
  studentName?: string;

  /**
   * 教师ID
   */
  teacherId?: number;

  /**
   * 教师名称
   */
  teacherName?: string;

  /**
   * 学情档案编码
   */
  code: string;

  /**
   * 学情档案名称
   */
  name: string;

  /**
   * 学习需求
   */
  learningNeeds?: string;

  /**
   * 学习动机
   */
  learningMotivation?: string;

  /**
   * 学校兴趣
   */
  learningInterests?: string;

  /**
   * 学习态度
   */
  learningAttitude?: string;

  /**
   * 学习自信心
   */
  learningSelfConfidence?: string;

  /**
   * 倾听
   */
  listening?: string;

  /**
   * 质疑
   */
  query?: string;

  /**
   * 独立思考
   */
  independentThinking?: string;

  /**
   * 小组合作意识
   */
  groupCooperationAwareness?: string;

  /**
   * 表达交流意识
   */
  expressingWillingnessToCommunicate?: string;

  /**
   * 记录意识
   */
  recordAwareness?: string;

  /**
   * 自我反思意识
   */
  selfReflectionConsciousness?: string;

  /**
   * 复习整理意识
   */
  reviewOrganizeAwareness?: string;

  /**
   * 预习意识
   */
  previewAwareness?: string;

  /**
   * 学科思维
   */
  disciplinaryThinking?: string;

  /**
   * 学科语言表达
   */
  subjectLanguageExpression?: string;

  /**
   * 课外学习
   */
  outOfClassActivities?: string;

  /**
   * 排序
   */
  sort?: number;
  /**
   * 学情档案状态(1-正常；0-停用)
   */
  status?: number;
}
