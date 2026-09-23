package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 班级学生关联表
 */
@TableName(value = "sys_clazz_student")
@Data
public class SysClazzStudent {

    /**
     * 班级ID
     */
    private Long clazzId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 年度
     */
    private Integer year;
}
