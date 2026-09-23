package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "班级学生分析查询条件")
@Data
public class AClazzStudentQuery {

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "年级ID")
    private Long studentId;


}
