package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "学情档案分页对象")
@Data
public class ArchivesPageVO {

    @Schema(description = "学情档案ID")
    private Long id;

    @Schema(description = "学情档案名称")
    private String name;

    @Schema(description = "学情档案编码")
    private String code;

    /**
     *
     */
    @Schema(description = "学习需求")
    private String learningNeeds;

    /**
     *
     */
    @Schema(description = "学习动机")
    private String learningMotivation;

    /**
     *
     */
    @Schema(description = "学习兴趣")
    private String learningInterests;

    /**
     *
     */
    @Schema(description = "学习态度")
    private String learningAttitude;

    /**
     *
     */
    @Schema(description = "学习自信心")
    private String learningSelfConfidence;

    /**
     *
     */
    @Schema(description = "倾听")
    private String listening;

    /**
     *
     */
    @Schema(description = "质疑")
    private String query;

    /**
     *
     */
    @Schema(description = "独立思考")
    private String independentThinking;

    /**
     *
     */
    @Schema(description = "小组合作意识")
    private String groupCooperationAwareness;

    /**
     *
     */
    @Schema(description = "表达交流意识")
    private String expressingWillingnessToCommunicate;

    /**
     *
     */
    @Schema(description = "记录意识")
    private String recordAwareness;

    /**
     *
     */
    @Schema(description = "自我反思意识")
    private String selfReflectionConsciousness;

    /**
     *
     */
    @Schema(description = "复习整理意识")
    private String reviewOrganizeAwareness;

    /**
     *
     */
    @Schema(description = "预习意识")
    private String previewAwareness;

    /**
     *
     */
    @Schema(description = "学科思维")
    private String disciplinaryThinking;

    /**
     *
     */
    @Schema(description = "学科语言表达")
    private String subjectLanguageExpression;

    /**
     *
     */
    @Schema(description = "课外学习")
    private String outOfClassActivities;


    @Schema(description = "学情档案状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "人员数量")
    private Long userCount;

    @Schema(description = "人员列表")
    private String userList;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师名称")
    private String teacherName;


    @Schema(description = "是否可编辑")
    private Boolean canEdit = false;
}
