package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_exam_course")
public class SysExamCourse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;
    private Long courseId;
    private Double fullScore;
    /** 是否计入考试总分：1 是，0 否。 */
    private Integer countInTotal;
    /** ORIGINAL 使用原始分，SCALED 使用赋分结果；规则未确认前默认 ORIGINAL。 */
    private String scoreMode;
    private Long scoringRuleId;
    private Integer sort;
}
