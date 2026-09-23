package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;

import java.time.LocalDate;

/**
 * 教师（职工表）表
 */
@Data
@TableName(value = "sys_teacher")
public class SysTeacher extends BaseEntity {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    @JsonIgnore
    @TableField(select = false, updateStrategy = FieldStrategy.NEVER)
    private String account;

    /**
     * 密码
     */
    @JsonIgnore
    @TableField(select = false, updateStrategy = FieldStrategy.NEVER)
    private String password;

    /**
     * 编码
     */
    private String code;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 状态
     */
    private Integer status;


    /**
     * 出生日期
     */
    private LocalDate birthDay;

    /**
     * 入职年份
     */
    private Integer year;


    /**
     * 电话
     */
    private String phone;


    /**
     * 头像
     */
    private String avatar;

    private Integer deleted;

    /**
     * 备注
     */
    private String remark;

}
