package com.youlai.system.model.bo;

import com.youlai.system.common.model.Statics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "课程统计对象")
public class CourseStaticsBO extends Statics {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程满分")
    private Integer fullScore;

}
