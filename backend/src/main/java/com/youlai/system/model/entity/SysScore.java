package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 成绩表
 */
@TableName(value = "sys_score")
@Data
public class SysScore extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 年级ID
     */
    private Long gradeId;

    /**
     * 年级名称
     */
    private String gradeName;

    /**
     * 班级ID
     */
    private Long clazzId;

    /**
     * 班级名称
     */
    private String clazzName;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 分数
     */
    private Double score;

    /** 赋分结果；原始分仍保存在 score，具体算法确认后再写入。 */
    private Double scaledScore;

    /** NORMAL=正常分数（包括 0 分），ABSENT=缺考，NOT_SELECTED=未选科。 */
    private String status;

    /**
     * 等级
     */
    private Integer degree;

    /**
     * 逻辑删除标识(0-未删除；1-已删除)
     */
    private Integer deleted;


}
