package com.youlai.system.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class StudentCourseScoreBO {
    private Long studentId;

    private String studentCode;

    private String studentName;

    private List<Double> courseScoreList;
}
