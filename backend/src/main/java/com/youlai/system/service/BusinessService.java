package com.youlai.system.service;

import com.youlai.system.model.form.ScoreEntryForm;
import com.youlai.system.model.query.ScoreEntryQuery;
import com.youlai.system.model.vo.ScoreEntryVO;

import java.util.List;
import java.util.Map;

public interface BusinessService {


    List<ScoreEntryVO> getScoreEntryList(ScoreEntryQuery query);


    void saveScore(ScoreEntryForm scoreEntryForm);

    /**
     * 获取某次考试某个班级考试情况数据汇总表格数据生成
     *
     * @param examBodyId
     * @return
     */
    Map<String, Object> clazzExamAllCourseScoreSummaryData(Long examBodyId, Long courseId);

    /**
     * 获取某次考试某个班级考试情况数据汇总导出数据生成
     *
     * @param examBodyId
     * @return
     */
    Map<String, Object> clazzExamAllCourseScoreSummaryExportData(Long examBodyId);


    /**
     * 获取某次考试某个年级考试情况数据汇总表格数据生成
     *
     * @param gradeId
     * @param examId
     * @return
     */
    Map<String, Object> gradeExamAllCourseScoreSummaryData(Long gradeId, Long examId);

    /**
     * 个人所有成绩统计情况分析数据
     *
     * @param studentId
     * @return
     */
    Map<String, Object> studentAllScoreSummaryData(Long studentId);

    /**
     * 学生单个课程的所有成绩数据统计情况
     *
     * @param studentId
     * @param courseId
     * @return
     */
    Map<String, Object> studentSingleCourseAnalysisData(Long studentId, Long courseId);
}
