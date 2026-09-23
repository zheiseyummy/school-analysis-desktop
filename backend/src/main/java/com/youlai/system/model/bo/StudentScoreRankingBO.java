package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "学生成绩排名对象")
public class StudentScoreRankingBO {

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "学生分数")
    private Double studentScore;

    @Schema(description = "成绩等级")
    private Integer studentDegree;

    @Schema(description = "学生排名")
    private Integer studentRank;

}
