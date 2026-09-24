package com.youlai.system.model.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExamCourseConfigForm {
    @NotNull(message = "考试科目不能为空")
    private Long courseId;
    @NotNull(message = "科目满分不能为空")
    @Positive(message = "科目满分必须大于0")
    private Double fullScore;
    /** 1 计入总分，0 不计入总分。 */
    private Integer countInTotal = 1;
    private String scoreMode = "ORIGINAL";
    private Long scoringRuleId;
    private Integer sort = 0;
}
