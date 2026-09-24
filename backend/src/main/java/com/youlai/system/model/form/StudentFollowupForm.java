package com.youlai.system.model.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentFollowupForm {
    @NotBlank(message = "请选择学习状态")
    private String learningStatus;
    private String specialSituation;
    @NotBlank(message = "请输入跟进记录")
    private String followupContent;
    private String nextAction;
    @NotNull(message = "请选择跟进日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followupDate;
}
