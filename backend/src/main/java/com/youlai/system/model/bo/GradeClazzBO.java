package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "年级下班级数量列表统计对象")
public class GradeClazzBO {

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "班级数量")
    private Long clazzCount;

    @Schema(description = "班级名称列表")
    private String clazzNameList;
}
