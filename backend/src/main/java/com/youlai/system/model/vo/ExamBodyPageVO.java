package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youlai.system.model.bo.ClazzCourseBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "考试主体查询结果分页对象")
public class ExamBodyPageVO {

    @Schema(description = "考试主体ID")
    private Long id;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "学期中文")
    private String semesterStr;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "考试类型")
    private String examType;

    @Schema(description = "考试类型中文")
    private String examTypeStr;

    @Schema(description = "考试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examDate;

    @Schema(description = "班级教学安排信息")
    private List<ClazzCourseBO> courseList;

    @Schema(description = "学生数量")
    private Long studentCount;
}
