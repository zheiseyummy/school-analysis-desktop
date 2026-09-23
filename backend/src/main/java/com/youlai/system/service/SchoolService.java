package com.youlai.system.service;

import com.youlai.system.common.model.Option;

import java.util.List;

public interface SchoolService {

    /**
     * 年级，班级树形结构（年级不可选，班级可选）
     *
     * @return
     */
    List<Option<Long>> listGradeClazzOptions();


    /**
     * 获取班级下的学生下拉列表
     *
     * @param clazzId
     * @return
     */
    List<Option<Long>> listStudentOptions(Long clazzId, Integer year);

    /**
     * 年级，班级树形结构(年级、班级皆可以选择)
     * @return
     */
    List<Option<String>> getGradeClazzOptions();


    /**
     * 获取考试的年级或者班级ID集合
     *
     * @param examId
     * @return
     */
    List<String> getGradeClazzIdListByExamId(Long examId);


    /**
     * 给考试分配年级或者班级
     *
     * @param examId
     * @param gradeClazzIdGroupList
     * @return
     */
    boolean assignGradeClazzToExam(Long examId, List<String> gradeClazzIdGroupList);
}
