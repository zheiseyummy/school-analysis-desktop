package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Statics;
import com.youlai.system.common.util.ScoreUtils;
import com.youlai.system.mapper.SysScoreMapper;
import com.youlai.system.model.bo.*;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.service.SysScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩业务接口
 */
@Service
@RequiredArgsConstructor

public class SysScoreServiceImpl extends ServiceImpl<SysScoreMapper, SysScore> implements SysScoreService {
    @Override
    public SysScore getScoreByExamIdAndStudentIdAndCourseId(Long examId, Long studentId, Long courseId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId);
        queryWrapper.eq(SysScore::getStudentId, studentId);
        queryWrapper.eq(SysScore::getCourseId, courseId);
        return getOne(queryWrapper);
    }

    @Override
    public List<SysScore> getScoreListByExamIdAndClazzIdAndCourseId(Long examId, Long clazzId, Long courseId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId);
        queryWrapper.eq(SysScore::getClazzId, clazzId);
        queryWrapper.eq(SysScore::getCourseId, courseId);
        return list(queryWrapper);
    }

    @Override
    public Map<Long, SysScore> getScoreMapByExamIdAndClazzIdAndCourseId(Long examId, Long clazzId, Long courseId) {
        return getScoreListByExamIdAndClazzIdAndCourseId(examId, clazzId, courseId).stream().collect(Collectors.toMap(SysScore::getStudentId, it -> it));
    }

    @Override
    public List<CourseScoreCountBO> getCourseScoreCountList(Long examId, Long clazzId) {
        return this.baseMapper.getCourseScoreCount(examId, clazzId);
    }

    @Override
    public Map<Long, CourseScoreCountBO> getCourseScoreCountMap(Long examId, Long clazzId) {
        return getCourseScoreCountList(examId, clazzId).stream().collect(Collectors.toMap(CourseScoreCountBO::getCourseId, it -> it));
    }

    @Override
    public void removeByStudentId(Long studentId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getStudentId, studentId);
        remove(queryWrapper);
    }

    @Override
    public void removeByClazzId(Long clazzId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getClazzId, clazzId);
        remove(queryWrapper);
    }

    @Override
    public List<StudentScoreRankingBO> getStudentSummaryScoreRanking(Long examId, Long clazzId) {
        return this.baseMapper.getStudentSummaryScoreRanking(examId, clazzId);
    }

    @Override
    public List<StudentScoreRankingBO> getStudentSummaryScoreClazzRanking(Long examId, Long gradeId) {
        return this.baseMapper.getStudentSummaryScoreClazzRanking(examId, gradeId);
    }

    @Override
    public List<StudentScoreRankingBO> getGradeStudentSummaryScoreRanking(Long examId, Long gradeId) {
        return this.baseMapper.getGradeStudentSummaryScoreRanking(examId, gradeId);
    }

    @Override
    public List<SysScore> getScoreListByExamIdAndClazzId(Long examId, Long clazzId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId);
        queryWrapper.eq(SysScore::getClazzId, clazzId);
        return list(queryWrapper);
    }

    @Override
    public List<SysScore> getScoreListByExamIdAndGradeId(Long examId, Long gradeId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId);
        queryWrapper.eq(SysScore::getGradeId, gradeId);
        return list(queryWrapper);
    }

    @Override
    public List<StudentScoreRankingBO> getStudentCourseScoreRanking(Long examId, Long clazzId) {
        return this.baseMapper.getStudentCourseScoreRanking(examId, clazzId);
    }

    @Override
    public List<StudentScoreRankingBO> getStudentCourseScoreClazzRanking(Long examId, Long gradeId) {
        return this.baseMapper.getStudentCourseScoreClazzRanking(examId, gradeId);
    }

    @Override
    public List<StudentScoreRankingBO> getGradeStudentCourseScoreRanking(Long examId, Long gradeId) {
        return this.baseMapper.getGradeStudentCourseScoreRanking(examId, gradeId);
    }

    @Override
    public List<CourseStaticsBO> getCourseStaticsList(Long examId, Long clazzId) {
        List<CourseStaticsBO> courseStaticsBOList = this.baseMapper.getCourseStaticsList(examId, clazzId);
        return getCourseStaticsBOS(courseStaticsBOList);
    }

    @Override
    public List<CourseStaticsBO> getGradeCourseStaticsList(Long examId, Long gradeId) {
        List<CourseStaticsBO> courseStaticsBOList = this.baseMapper.getGradeCourseStaticsList(examId, gradeId);
        return getCourseStaticsBOS(courseStaticsBOList);
    }

    @Override
    public List<ClazzStaticsBO> getGradeClazzStaticsList(Long examId, Long gradeId) {
        List<ClazzStaticsBO> clazzStaticsBOList = this.baseMapper.getGradeClazzStaticsList(examId, gradeId);
        return getCourseStaticsBOS(clazzStaticsBOList);
    }

    @Override
    public List<CourseClazzStaticsBO> getGradeCourseClazzStaticsList(Long examId, Long gradeId) {
        List<CourseClazzStaticsBO> courseClazzStaticsBOList = this.baseMapper.getGradeCourseClazzStaticsList(examId, gradeId);
        return getCourseStaticsBOS(courseClazzStaticsBOList);
    }

    @Override
    public List<Long> getCourseIdListByStudentId(Long studentId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getStudentId, studentId).select(SysScore::getCourseId);
        return list(queryWrapper).stream().map(SysScore::getCourseId).distinct().toList();
    }

    @Override
    public List<Long> getExamIdListByStudentId(Long studentId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getStudentId, studentId).select(SysScore::getExamId);
        return list(queryWrapper).stream().map(SysScore::getExamId).distinct().toList();
    }

    @Override
    public Long getClazzIdByExamIdAndStudentId(Long examId, Long studentId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId).eq(SysScore::getStudentId, studentId);
        queryWrapper.select(SysScore::getClazzId);
        return list(queryWrapper).stream().map(SysScore::getClazzId).distinct().toList().get(0);
    }

    @Override
    public Long getGradeIdByClazzIdAndExamIdAndStudent(Long clazzId, Long examId, Long studentId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getExamId, examId).eq(SysScore::getStudentId, studentId);
        queryWrapper.eq(SysScore::getClazzId, clazzId);
        queryWrapper.select(SysScore::getGradeId);
        return list(queryWrapper).stream().map(SysScore::getGradeId).distinct().toList().get(0);
    }

    @Override
    public List<SysScore> getScoreListByStudentIdAndCourseId(Long studentId, Long courseId) {
        LambdaQueryWrapper<SysScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysScore::getStudentId, studentId);
        queryWrapper.eq(SysScore::getCourseId, courseId);
        return list(queryWrapper);
    }

    @Override
    public List<SysScore> getAllExamSumScoreList(Long studentId) {
        return this.baseMapper.getAllExamSumScoreList(studentId);
    }


    private static <T extends Statics> List<T> getCourseStaticsBOS(List<T> courseStaticsBOList) {
        courseStaticsBOList.forEach(courseStaticsBO -> {
            courseStaticsBO.setARate(ScoreUtils.aRate(courseStaticsBO.getACount(), courseStaticsBO.getBCount(), courseStaticsBO.getCCount(), courseStaticsBO.getDCount(), courseStaticsBO.getECount()));
            courseStaticsBO.setBRate(ScoreUtils.bRate(courseStaticsBO.getACount(), courseStaticsBO.getBCount(), courseStaticsBO.getCCount(), courseStaticsBO.getDCount(), courseStaticsBO.getECount()));
            courseStaticsBO.setCRate(ScoreUtils.cRate(courseStaticsBO.getACount(), courseStaticsBO.getBCount(), courseStaticsBO.getCCount(), courseStaticsBO.getDCount(), courseStaticsBO.getECount()));
            courseStaticsBO.setDRate(ScoreUtils.dRate(courseStaticsBO.getACount(), courseStaticsBO.getBCount(), courseStaticsBO.getCCount(), courseStaticsBO.getDCount(), courseStaticsBO.getECount()));
            courseStaticsBO.setERate(ScoreUtils.eRate(courseStaticsBO.getACount(), courseStaticsBO.getBCount(), courseStaticsBO.getCCount(), courseStaticsBO.getDCount(), courseStaticsBO.getECount()));

        });
        return courseStaticsBOList;
    }
}
