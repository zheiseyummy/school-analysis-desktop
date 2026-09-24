package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

/** 学生学习状态与跟进记录。 */
@Data
@TableName("sys_student_followup")
public class SysStudentFollowup extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String learningStatus;
    private String specialSituation;
    private String followupContent;
    private String nextAction;
    private LocalDate followupDate;
    private Integer deleted;
}
