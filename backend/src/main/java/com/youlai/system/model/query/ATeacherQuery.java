package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "教师体检数据查询对象")
public class ATeacherQuery {

    @Schema(description = "职务ID")
    private Long positionId;

    @Schema(description = "教师ID")
    private Long teacherId;
}
