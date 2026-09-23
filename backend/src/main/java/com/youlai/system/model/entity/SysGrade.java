package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 年级表
 */
@TableName(value = "sys_grade")
@Data
public class SysGrade extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 年级编号
     */
    private String code;


    /**
     * 年级名称
     */
    private String name;


    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 年级状态(1-正常；0-停用)
     */
    private Integer status;

    /**
     * 管理者
     */
    private Long managerId;

    /**
     * 逻辑删除标识(0-未删除；1-已删除)
     */
    private Integer deleted;
}
