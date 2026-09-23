/**
 * 教学安排查询参数
 */
export interface ArrangeQuery extends PageQuery {
  /**
   * 班级ID
   */
  clazzId?: number;

  /**
   * 课程ID
   */
  courseId?: number;
  /**
   * 教师ID
   */
  teacherId?: number;
}

/**
 * 教学安排分页对象
 */
export interface ArrangePageVO {
  /**
   * 教学安排ID
   */
  id?: number;

  /**
   * 状态(1:启用;0:禁用)
   */
  status?: number;

  /**
   * 班级ID
   */
  clazzId?: number;

  /**
   * 班级名称
   */

  ClazzName?: string;
  /**
   * 课程ID
   */
  courseId?: number;

  /**
   * 课程名称
   */
  courseName?: string;
  /**
   * 教师ID
   */
  teacherId?: number;

  /**
   * 教师姓名
   */
  teacherName?: string;

  /**
   * 排序
   */
  sort?: number;
}

/**
 * 教学安排分页
 */
export type ArrangePageResult = PageResult<ArrangePageVO[]>;

/**
 * 教学安排表单
 */
export interface ArrangeForm {
  /**
   * 教学安排ID
   */
  id?: number;

  /**
   * 排序
   */
  sort?: number;
  /**
   * 状态(1:启用;0:禁用)
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 班级ID
   */
  clazzId?: number;
  /**
   * 课程ID
   */
  courseId?: number;
  /**
   * 教师ID
   */
  teacherId?: number;
}
