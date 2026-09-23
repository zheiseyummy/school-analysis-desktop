package com.youlai.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "课程表单对象")
@Data
public class CourseForm {

    @Schema(description="课程ID")
    private Long id;

    @Schema(description="课程名称")
    @NotBlank(message = "课程名称不能为空")
    private String name;

    @Schema(description="课程编码")
    @NotBlank(message = "课程编码不能为空")
    private String code;

    @Schema(description = "课程类型")
    @NotBlank(message = "课程类型不能为空")
    private String subjectType;

    @Schema(description = "课程满分")
    @NotNull(message = "课程满分不能为空")
    private Integer fullScore;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="课程状态(1-正常；0-停用)")
    private Integer status;

}
