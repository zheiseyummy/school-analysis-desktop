package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/** 成绩导入批次中的单项变更记录，用于审计和整批撤销。 */
@Data
@TableName("sys_score_import_log")
public class SysScoreImportChangeLog extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchId;
    private Long examId;
    private String fileName;
    private Integer rowNumber;
    private String studentCode;
    private String studentName;
    private String courseName;
    private Long studentId;
    private Long courseId;
    private String action;
    /** 撤销前成绩对象快照；新增时为空。 */
    private String beforeJson;
    /** 导入后成绩对象快照；清除时为空。 */
    private String afterJson;
    private Integer undone = 0;
}
