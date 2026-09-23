/**
 * 班级查询参数
 */
export interface ClazzQuery extends PageQuery {
  keywords?: string;
  managerId?: number;
  gradeId?: number;
  clazzType?: string;
}

/**
 * 班级分页对象
 */
export interface ClazzPageVO {
  /**
   * 班级ID
   */
  id?: number;
  /**
   * 班级编号
   */
  code?: string;
  /**
   * 班级名称
   */
  name?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 班级状态
   */
  status?: number;
  /**
   * 管理者
   */
  managerId?: number;

  /**
   * 管理者
   */
  managerName?: string;

  /**
   * 年级
   */
  gradeId?: number;
  gradeName?: string;
  /**
   * 班级类型
   */
  clazzType?: string;
  clazzTypeLabel?: string;

  /**
   * 创建时间
   */
  createTime?: Date;
  /**
   * 修改时间
   */
  updateTime?: Date;

  /**
   * 学生数量
   */
  studentCount?: number;
  /**
   * 学生姓名列表
   */
  studentNameList?: string;

  /**
   * 教学安排数量
   */
  arrangeCount?: number;

  /**
   * 教学安排科目教师列表
   */
  arrangeNameList?: string;
}

/**
 * 班级表单对象
 */
export interface ClazzForm {
  /**
   * 班级ID
   */
  id?: number;

  /**
   * 班级编号
   */
  code?: string;

  /**
   * 班级名称
   */
  name: string;

  /**
   * 管理者
   */
  managerId?: number;

  /**
   * 年级ID
   */
  gradeId?: number;
  /**
   * 班级类型
   */
  clazzType?: string;

  /**
   * 排序
   */
  sort?: number;

  /**
   * 班级状态(1-正常；0-停用)
   */
  status?: number;
}
