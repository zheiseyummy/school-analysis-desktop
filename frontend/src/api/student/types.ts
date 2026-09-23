/**
 * 学生查询参数
 */
export interface StudentQuery extends PageQuery {
  keywords?: string;
  clazzId?: number;
  year?: number;
}

/**
 * 学生分页对象
 */
export interface StudentPageVO {
  /**
   * 学生ID
   */
  id?: number;
  /**
   * 学生学号
   */
  code?: string;
  /**
   * 学生名称
   */
  name?: string;
  /**
   * 性别数字
   */
  sex?: number;
  /**
   * 性别中文
   */
  sexLabel?: string;
  /**
   * 出生日期
   */
  birthDay?: string;
  /**
   * 年龄
   */
  age?: string;
  /**
   * 入学年份
   */
  year?: number;
  /**
   * 电话
   */
  phone?: string;
  /**
   * 头像
   */
  avatar?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 学生状态
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
   * 备注
   */
  remark?: string;
  /**
   * 班级数量
   */
  clazzCount?: number;
  /**
   * 班级名称列表
   */
  clazzNameList?: string;
}

export interface StudentClazzForm {
  clazzId?: number;
  year?: number;
}

/**
 * 学生表单对象
 */
export interface StudentForm {
  /**
   * 学生ID
   */
  id?: number;
  /**
   * 学生学号
   */
  code: string;
  /**
   * 学生名称
   */
  name: string;
  /**
   * 性别
   */
  sex?: number;
  /**
   * 出生日期
   */
  birthDay?: string;
  /**
   * 入学年份
   */
  year?: number;
  /**
   * 手机号
   */
  phone?: string;
  /**
   * 头像
   */
  avatar?: string;
  /**
   * 排序
   */
  sort?: number;
  /**
   * 学生状态(1-正常；0-停用)
   */
  status?: number;
  /**
   * 备注
   */
  remark?: string;
  /**
   * 学生的班级列表
   */
  clazzList?: StudentClazzForm[];
}
