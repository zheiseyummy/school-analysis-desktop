package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班级下课程详细信息对象列表")
public class ClazzCourseBO {

    @Schema(description = "班级课程ID")
    private Long id;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;


    @Schema(description = "课程类型")
    private String subjectType;


    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师名称")
    private String teacherName;

    @Schema(description = "成绩数量")
    private Long scoreCount;

    @Schema(description = "课程满分")
    private Integer fullScore;
}
