package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "成绩录入查询对象")
public class ScoreEntryQuery {

    @Schema(description = "考试主体ID")
    private Long examBodyId;

    @Schema(description = "课程ID")
    private Long courseId;
}
