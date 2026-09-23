package com.youlai.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description ="教学安排分页对象")
@Data
public class ArrangePageVO {

    @Schema(description="教学安排ID")
    private Long id;
    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "班级名称")
    private String clazzName;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description="状态(1:启用;0:禁用)")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

}
