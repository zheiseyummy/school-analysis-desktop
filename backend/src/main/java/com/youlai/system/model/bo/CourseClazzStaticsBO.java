package com.youlai.system.model.bo;

import com.youlai.system.common.model.Statics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "课程班级统计情况")
@Data
public class CourseClazzStaticsBO extends Statics {


    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程满分")
    private Integer fullScore;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "班级学生人数")
    private Long clazzStudentCount;

}
