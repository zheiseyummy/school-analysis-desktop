package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试表
 */
@TableName(value = "sys_exam")
@Data
public class SysExam {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 考试名称
     */
    private String name;

    /**
     * 考试编码
     */
    private String code;

    /**
     * 考试年度
     */
    private Integer year;


    /**
     * 学期
     */
    private Integer semester;


    /**
     * 考试类型
     */
    private String examType;

    /**
     * 考试日期时间
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examDate;


    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 体检安排状态(1-正常；0-停用)
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
