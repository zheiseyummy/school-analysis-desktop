package com.youlai.system.model.vo;

import lombok.Data;

/** 单个学生单个科目的导入差异。 */
@Data
public class ScoreImportChangeVO {
    private int rowNumber;
    private String studentCode;
    private String studentName;
    private String courseName;
    private Long studentId;
    private Long examBodyId;
    private Long courseId;
    private String action;
    private Double oldScore;
    private Double newScore;
    private String oldStatus;
    private String newStatus;
    private String message;
}
