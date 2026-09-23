package com.youlai.system.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "教学安排表单对象")
@Data
public class ArrangeForm {

    @Schema(description="教学安排ID")
    private Long id;

    @Schema(description = "班级ID")
    @NotNull(message = "班级不能为空")
    private Long clazzId;

    @Schema(description = "课程ID")
    @NotNull(message = "课程不能为空")
    private Long courseId;

    @Schema(description = "教师ID")
    @NotNull(message = "教师不能为空")
    private Long teacherId;

    @Schema(description="状态(1:启用;0:禁用)")
    private Integer status;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description = "教学安排备注")
    private String remark;

}
