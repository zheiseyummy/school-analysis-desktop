/**
 * 课程查询参数
 */
export interface CourseQuery extends PageQuery {
  keywords?: string;
  subjectType?: string;
}

/**
 * 课程分页对象
 */
export interface CoursePageVO {
  /**
   * 课程编码
   */
  code?: string;

  /**
   * 课程ID
   */
  id?: number;
  /**
   * 课程名称
   */
  name?: string;
  /**
   * 课程类型
   */
  subjectType?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 课程状态
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
   * 人员数量
   */
  userCount?: number;
  /**
   * 人员列表
   */
  userList?: string;
  /**
   * 课程满分
   */
  fullScore?: number;
}

/**
 * 课程表单对象
 */
export interface CourseForm {
  /**
   * 课程ID
   */
  id?: number;

  /**
   * 课程编码
   */
  code: string;

  /**
   * 课程名称
   */
  name: string;
  /**
   * 课程类型
   */
  subjectType?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 课程状态(1-正常；0-停用)
   */
  status?: number;

  /**
   * 课程满分
   */
  fullScore?: number;
}
