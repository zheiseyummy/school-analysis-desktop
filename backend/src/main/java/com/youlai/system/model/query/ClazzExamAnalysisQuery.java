package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "班级考试分析查询对象")
public class ClazzExamAnalysisQuery {

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "考试类型")
    private String examType;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "考试ID")
    private List<Long> examIdList;

}
