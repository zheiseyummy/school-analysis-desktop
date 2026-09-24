package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

/** 高中学生选科名单版本。 */
@Data
@TableName("sys_student_subject_selection_version")
public class SysStudentSubjectSelectionVersion extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gradeId;
    private String name;
    private LocalDate effectiveDate;
    private Integer status;
    private String note;
    private Integer deleted;
}
