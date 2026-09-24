package com.youlai.system.model.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentSubjectSelectionVersionForm {
    @NotNull(message = "请选择高中年级")
    private Long gradeId;
    @NotBlank(message = "请输入选科版本名称")
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    private Integer status = 1;
    private String note;
}
