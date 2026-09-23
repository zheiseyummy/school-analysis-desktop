package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 学情档案表
 */
@TableName(value = "sys_archives")
@Data
public class SysArchives extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 年度
     */
    private Integer year;

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
     * 教师ID
     */
    private Long teacherId;


    /**
     * 学情档案名称
     */
    private String name;

    /**
     * 学情档案编码
     */
    private String code;

    /**
     * 学习需求
     */
    private String learningNeeds;

    /**
     * 学习动机
     */
    private String learningMotivation;

    /**
     * 学习兴趣
     */
    private String learningInterests;

    /**
     * 学习态度
     */
    private String learningAttitude;

    /**
     * 学习自信心
     */
    private String learningSelfConfidence;

    /**
     * 倾听
     */
    private String listening;

    /**
     * 质疑
     */
    private String query;

    /**
     * 独立思考
     */
    private String independentThinking;

    /**
     * 小组合作意识
     */
    private String groupCooperationAwareness;

    /**
     * 表达交流意识
     */
    private String expressingWillingnessToCommunicate;

    /**
     * 记录意识
     */
    private String recordAwareness;

    /**
     * 自我反思意识
     */
    private String selfReflectionConsciousness;

    /**
     * 复习整理意识
     */
    private String reviewOrganizeAwareness;

    /**
     * 预习意识
     */
    private String previewAwareness;

    /**
     * 学科思维
     */
    private String disciplinaryThinking;

    /**
     * 学科语言表达
     */
    private String subjectLanguageExpression;

    /**
     * 课外学习
     */
    private String outOfClassActivities;


    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 学情档案状态(1-正常；0-停用)
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
