package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班级下学生数量统计")
public class ClazzStudentBO {

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "学生数量")
    private Long studentCount;

    @Schema(description = "学生姓名列表")
    private String studentNameList;
}
