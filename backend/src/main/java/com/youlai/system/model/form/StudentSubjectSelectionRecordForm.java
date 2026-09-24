package com.youlai.system.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StudentSubjectSelectionRecordForm {
    @NotNull(message = "学生不能为空")
    private Long studentId;
    private Long trackCourseId;
    private List<Long> electiveCourseIds;
    @NotBlank(message = "选科组合不能为空")
    private String combinationCode;
    private String note;
}
