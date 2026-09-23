package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.*;
import com.youlai.system.model.entity.SysScore;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysScoreMapper extends BaseMapper<SysScore> {


    List<CourseScoreCountBO> getCourseScoreCount(Long examId, Long clazzId);


    List<StudentScoreRankingBO> getStudentSummaryScoreRanking(Long examId, Long clazzId);

    /**
     * 获取年级下每个学生总分在各自班级的排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getStudentSummaryScoreClazzRanking(Long examId, Long gradeId);


    List<StudentScoreRankingBO> getGradeStudentSummaryScoreRanking(Long examId, Long gradeId);


    List<StudentScoreRankingBO> getStudentCourseScoreRanking(Long examId, Long clazzId);


    /**
     * 获取年级下每个学生单科在各自班级的排名
     *
     * @param examId
     * @param gradeId
     * @return
     */
    List<StudentScoreRankingBO> getStudentCourseScoreClazzRanking(Long examId, Long gradeId);


    List<StudentScoreRankingBO> getGradeStudentCourseScoreRanking(Long examId, Long gradeId);

    List<CourseStaticsBO> getCourseStaticsList(Long examId, Long clazzId);

    List<CourseStaticsBO> getGradeCourseStaticsList(Long examId, Long gradeId);

    List<ClazzStaticsBO> getGradeClazzStaticsList(Long examId, Long gradeId);


    List<CourseClazzStaticsBO> getGradeCourseClazzStaticsList(Long examId, Long gradeId);


    /**
     * 获取某个学生所有考试总分列表
     *
     * @param studentId
     * @return
     */
    List<SysScore> getAllExamSumScoreList(Long studentId);
}
