package com.youlai.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "年级表单对象")
@Data
public class GradeForm {
    @Schema(description="年级ID")
    private Long id;

    @Schema(description="年级编号")
    @NotBlank(message = "年级编号不能为空")
    private String code;

    @Schema(description="年级名称")
    @NotBlank(message = "年级名称不能为空")
    private String name;

    @Schema(description="学段：初中/高中")
    private String stage = "高中";

    @Schema(description = "年级主任ID")
    private Long managerId;

    @Schema(description = "年级主任姓名")
    private String managerName;


    @Schema(description="年级排序")
    private Integer sort;

    @Schema(description="年级状态(1-正常；0-停用)")
    private Integer status;
}
