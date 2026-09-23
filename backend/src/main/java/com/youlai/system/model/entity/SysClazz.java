package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 班级表
 */
@TableName(value = "sys_clazz")
@Data
public class SysClazz extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级编号
     */
    private String code;


    /**
     * 班级名称
     */
    private String name;


    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 班级状态(1-正常；0-停用)
     */
    private Integer status;

    /**
     * 管理者
     */
    private Long managerId;

    /**
     * 年级Id
     */
    private Long gradeId;

    /**
     * 班级类型
     */
    private String clazzType;


    /**
     * 逻辑删除标识(0-未删除；1-已删除)
     */
    private Integer deleted;
}
