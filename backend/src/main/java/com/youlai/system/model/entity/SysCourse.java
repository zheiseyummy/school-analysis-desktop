package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 课程表
 */
@TableName(value ="sys_course")
@Data
public class SysCourse extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程编码
     */
    private String code;

    /**
     * 课程类型
     */
    private String subjectType;

    /**
     * 满分
     */
    private Integer fullScore;


    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 课程状态(1-正常；0-停用)
     */
    private Integer status;

    /**
     * 逻辑删除标识(0-未删除；1-已删除)
     */
    private Integer deleted;

    /**
     * 人员数量
     */
    @TableField(exist = false)
    private Integer userCount;

    /**
     * 人员列表
     */
    @TableField(exist = false)
    private String userList;
}