package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "学生就读班级情况")
@Data
public class StudentClazzBO {

    @Schema(description = "学生ID")
    private Long studentId;
    @Schema(description = "班级数量")
    private Long clazzCount;
    @Schema(description = "班级名称列表")
    private String clazzNameList;
}
