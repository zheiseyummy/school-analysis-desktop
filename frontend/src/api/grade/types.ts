/**
 * 年级查询参数
 */
export interface GradeQuery extends PageQuery {
  keywords?: string;
  managerId?: number;
}

/**
 * 年级分页对象
 */
export interface GradePageVO {
  /**
   * 年级ID
   */
  id?: number;
  /**
   * 年级编号
   */
  code?: string;
  /**
   * 年级名称
   */
  name?: string;
  stage?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 年级状态
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
   * 创建时间
   */
  createTime?: Date;
  /**
   * 修改时间
   */
  updateTime?: Date;
  /**
   * 班级数量
   */
  clazzCount?: number;
  /**
   * 班级姓名列表
   */
  clazzNameList?: string;
}

/**
 * 年级表单对象
 */
export interface GradeForm {
  /**
   * 年级ID
   */
  id?: number;

  /**
   * 年级编号
   */
  code?: string;

  /**
   * 年级名称
   */
  name: string;
  stage?: "初中" | "高中";

  /**
   * 管理者
   */
  managerId?: number;

  /**
   * 排序
   */
  sort?: number;

  /**
   * 年级状态(1-正常；0-停用)
   */
  status?: number;
}
