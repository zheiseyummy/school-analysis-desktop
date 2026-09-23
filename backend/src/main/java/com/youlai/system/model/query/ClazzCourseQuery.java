package com.youlai.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班级课程查询条件")
public class ClazzCourseQuery {

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "教师ID")
    private Long teacherId;

}
