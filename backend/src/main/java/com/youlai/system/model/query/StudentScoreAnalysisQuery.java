package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentScoreAnalysisQuery {

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "班级")
    private Long clazzId;

    @Schema(description = "学生Id")
    private Long studentId;

    @Schema(description = "课程ID")
    private Long courseId;
}
