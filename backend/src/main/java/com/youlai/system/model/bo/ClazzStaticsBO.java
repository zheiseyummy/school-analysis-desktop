package com.youlai.system.model.bo;

import com.youlai.system.common.model.Statics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班级统计对象")
public class ClazzStaticsBO  extends Statics {
    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "班级学生人数")
    private Long clazzStudentCount;

}
