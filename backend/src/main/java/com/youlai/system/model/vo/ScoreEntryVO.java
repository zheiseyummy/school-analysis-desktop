package com.youlai.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "成绩录入返回对象")
public class ScoreEntryVO {

    @Schema(description = "成绩ID")
    private Long scoreId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学号")
    private String studentCode;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "分数")
    private Double score;
}
