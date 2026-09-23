package com.youlai.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "学生完整个人信息")
public class StudentInfo {

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

    @Schema(description = "班级类型")
    private String clazzType;

    @Schema(description = "班级类型中文表示")
    private String clazzTypeLabel;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生编号")
    private String studentCode;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生性别")
    private Integer studentSex;

    @Schema(description = "学生性别中文表示")
    private String studentSexLabel;
}
