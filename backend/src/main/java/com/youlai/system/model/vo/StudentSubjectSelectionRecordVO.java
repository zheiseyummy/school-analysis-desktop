package com.youlai.system.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class StudentSubjectSelectionRecordVO {
    private Long id;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private Long trackCourseId;
    private String trackCourseName;
    private List<Long> electiveCourseIds;
    private List<String> electiveCourseNames;
    private String combinationCode;
    private String source;
    private String note;
}
