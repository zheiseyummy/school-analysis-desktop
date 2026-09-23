package com.youlai.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "班级表单对象")
@Data
public class ClazzForm {
    @Schema(description="班级ID")
    private Long id;

    @Schema(description="班级编号")
    @NotBlank(message = "班级编号不能为空")
    private String code;

    @Schema(description="班级名称")
    @NotBlank(message = "班级名称不能为空")
    private String name;
    @Schema(description="班主任")
    private Long managerId;

    @Schema(description="年级Id")
    private Long gradeId;

    @Schema(description="班级排序")
    private Integer sort;

    @Schema(description = "班级类型")
    @NotBlank(message = "班级类型不能为空")
    private String clazzType;

    @Schema(description="班级状态(1-正常；0-停用)")
    private Integer status;
}
