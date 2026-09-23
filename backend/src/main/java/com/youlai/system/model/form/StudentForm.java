package com.youlai.system.model.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youlai.system.model.bo.ClazzYearBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "学生表单对象")
@Data
public class StudentForm {
    @Schema(description="学生ID")
    private Long id;
    @Schema(description = "编码")
    @NotBlank(message = "编号不能为空")
    private String code;
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;
    @Schema(description = "性别")
    @NotNull(message = "请选择性别")
    private Integer sex;
    @Schema(description = "状态")
    @NotNull(message = "请选择状态")
    private Integer status;
    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;
    @Schema(description = "入学年份")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;
    @Schema(description = "电话")
    private String phone;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "班级列表")
    // null preserves memberships; [] explicitly clears them.
    private List<ClazzYearBO> clazzList;


}
