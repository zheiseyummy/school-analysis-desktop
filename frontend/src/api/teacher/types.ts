/**
 * 教师查询参数
 */
export interface TeacherQuery extends PageQuery {
  keywords?: string;
  year?: number;
}

/**
 * 教师分页对象
 */
export interface TeacherPageVO {
  /**
   * 教师ID
   */
  id?: number;
  /**
   * 教师编码
   */
  code?: string;
  /**
   * 教师名称
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
   * 入职年份
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
   * 教师状态
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
}

/**
 * 教师表单对象
 */
export interface TeacherForm {
  /**
   * 教师ID
   */
  id?: number;
  /**
   * 教师编码
   */
  code: string;
  /**
   * 教师名称
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
   * 入职年份
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
   * 教师状态(1-正常；0-停用)
   */
  status?: number;
  /**
   * 备注
   */
  remark?: string;
}
