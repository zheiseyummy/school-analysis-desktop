package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.bo.*;
import com.youlai.system.model.entity.SysScore;

import java.util.List;
import java.util.Map;

public interface SysScoreService extends IService<SysScore> {


    /**
     * @param examId
     * @param studentId
     * @param courseId
     * @return
     */
    SysScore getScoreByExamIdAndStudentIdAndCourseId(Long examId, Long studentId, Long courseId);

    /**
     * @param examId
     * @param clazzId
     * @param courseId
     * @return
     */
    List<SysScore> getScoreListByExamIdAndClazzIdAndCourseId(Long examId, Long clazzId, Long courseId);

    /**
     * @param examId
     * @param clazzId
     * @param courseId
     * @return
     */

    Map<Long, SysScore> getScoreMapByExamIdAndClazzIdAndCourseId(Long examId, Long clazzId, Long courseId);

    /**
     * @param examId
     * @param clazzId
     * @return
     */
    List<CourseScoreCountBO> getCourseScoreCountList(Long examId, Long clazzId);

    Map<Long, CourseScoreCountBO> getCourseScoreCountMap(Long examId, Long clazzId);


    /**
     * 删除学生的关联信息
     *
     * @param studentId
     */
    void removeByStudentId(Long studentId);

    /**
     * 删除班级的关联信息
     *
     * @param clazzId
     */
    void removeByClazzId(Long clazzId);

    /**
     * 获取某个班级某次考试总分排名
     *
     * @param examId
     * @param clazzId
     * @return
     */
    List<StudentScoreRankingBO> getStudentSummaryScoreRanking(Long examId, Long clazzId);

    /**
     * 获取年级下每个学生总分在各自班级的排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getStudentSummaryScoreClazzRanking(Long examId, Long gradeId);

    /**
     * 获取某个年级某次考试总分排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getGradeStudentSummaryScoreRanking(Long examId, Long gradeId);


    /**
     * 获取某个班级某次考试所有的考试记录
     *
     * @param examId
     * @param clazzId
     * @return
     */
    List<SysScore> getScoreListByExamIdAndClazzId(Long examId, Long clazzId);

    /**
     * 获取某个年级某次考试所有的考试记录
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<SysScore> getScoreListByExamIdAndGradeId(Long examId, Long gradeId);

    /**
     * 获取某次考试某个班级单科排名
     *
     * @param examId
     * @param clazzId
     * @return
     */
    List<StudentScoreRankingBO> getStudentCourseScoreRanking(Long examId, Long clazzId);

    /**
     * 获取年级下每个学生单科在各自班级的排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getStudentCourseScoreClazzRanking(Long examId, Long gradeId);


    /**
     * 获取某次考试某个年级单科排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getGradeStudentCourseScoreRanking(Long examId, Long gradeId);

    /**
     * 获取某次考试某个班级课程的统计信息
     *
     * @param examId
     * @param clazzId
     * @return
     */
    List<CourseStaticsBO> getCourseStaticsList(Long examId, Long clazzId);

    /**
     * 获取某次考试某个年级课程的统计情况
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<CourseStaticsBO> getGradeCourseStaticsList(Long examId, Long gradeId);


    /**
     * 获取某次考试某个年级下班级的统计情况
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<ClazzStaticsBO> getGradeClazzStaticsList(Long examId, Long gradeId);

    /**
     * 获取某次考试某个年级下各科目中各班级的统计情况
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<CourseClazzStaticsBO> getGradeCourseClazzStaticsList(Long examId, Long gradeId);


    /**
     * 获取某个学生所有考试的课程ID
     *
     * @param studentId
     * @return
     */
    List<Long> getCourseIdListByStudentId(Long studentId);

    /**
     * 获取某个学生所有考试的考试ID
     *
     * @param studentId
     * @return
     */
    List<Long> getExamIdListByStudentId(Long studentId);

    Long getClazzIdByExamIdAndStudentId(Long examId, Long studentId);

    Long getGradeIdByClazzIdAndExamIdAndStudent(Long clazzId, Long examId, Long studentId);

    /**
     * 根据学生ID和课程ID获取成绩列表
     *
     * @param studentId
     * @param courseId
     * @return
     */
    List<SysScore> getScoreListByStudentIdAndCourseId(Long studentId, Long courseId);

    List<SysScore> getAllExamSumScoreList(Long studentId);
}
