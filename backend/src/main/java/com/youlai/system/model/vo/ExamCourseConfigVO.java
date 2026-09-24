package com.youlai.system.model.vo;

import lombok.Data;

@Data
public class ExamCourseConfigVO {
    private Long courseId;
    private String courseName;
    private Integer defaultFullScore;
    private Double fullScore;
    private Integer countInTotal;
    private String scoreMode;
    private Long scoringRuleId;
    private Integer sort;
    private Boolean selected;
}
