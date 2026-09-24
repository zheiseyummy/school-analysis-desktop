package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/** 高中学生在某个选科版本中的 3+1+2 组合。 */
@Data
@TableName("sys_student_subject_selection")
public class SysStudentSubjectSelection extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long versionId;
    private Long studentId;
    private Long trackCourseId;
    /** 选择性科目 ID，使用英文逗号保存，后续可平滑迁移到明细表。 */
    private String electiveCourseIds;
    private String combinationCode;
    private String source;
    private String note;
    private Integer deleted;
}
