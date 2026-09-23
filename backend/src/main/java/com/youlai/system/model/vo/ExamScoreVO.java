package com.youlai.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "考试成绩对象")
public class ExamScoreVO {
    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试时间")
    private LocalDateTime examDate;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "成绩")
    private Double score;
}
