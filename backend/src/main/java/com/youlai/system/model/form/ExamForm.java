package com.youlai.system.model.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "考试表单对象")
@Data
public class ExamForm {
    @Schema(description="考试ID")
    private Long id;

    @Schema(description="考试名称")
    @NotBlank(message = "考试名称不能为空")
    private String name;

    @Schema(description="考试编码")
    @NotBlank(message = "考试编码不能为空")
    private String code;

    @Schema(description = "学期")
    @NotNull(message = "学期不能为空")
    private Integer semester;

    @Schema(description = "年度")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "年度不能为空")
    private Integer year;

    @Schema(description = "考试类型")
    @NotBlank(message = "考试类型不能为空")
    private String examType;
    @Schema(description = "考试日期")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examDate;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="考试状态(1-正常；0-停用)")
    private Integer status;
}
