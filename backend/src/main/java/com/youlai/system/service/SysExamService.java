package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.form.ExamForm;
import com.youlai.system.model.query.ClazzExamAnalysisQuery;
import com.youlai.system.model.query.ExamPageQuery;
import com.youlai.system.model.vo.ExamPageVO;

import java.util.List;
import java.util.Map;

/**
 * 考试接口
 */
public interface SysExamService extends IService<SysExam> {
    /**
     * 考试分页列表
     *
     * @param queryParams
     * @return
     */
    Page<ExamPageVO> getExamPage(ExamPageQuery queryParams);

    /**
     * 保存考试
     *
     * @param examForm
     * @return
     */
    boolean saveExam(ExamForm examForm);


    /**
     * 更新考试
     *
     * @param examForm
     * @return
     */
    boolean updateExam(Long examId, ExamForm examForm);

    /**
     * 查询单个考试
     *
     * @param examId
     * @return
     */
    ExamForm getExamForm(Long examId);

    /**
     * 删除考试列表
     *
     * @param idsStr
     * @return
     */
    boolean deleteExams(String idsStr);

    /**
     * 删除考试列表
     *
     * @param idList
     * @return
     */
    boolean deleteExams(List<Long> idList);

    /**
     * 考试下拉列表
     *
     * @return
     */
    List<Option> listExamOptions();

    List<Option> listExamOptions(ClazzExamAnalysisQuery query);

    List<Option> listExamOptions(List<Long> examIdList);


    /**
     * 查询特定考试对象的考试
     *
     * @param examType
     * @return
     */
    List<Option> listExamOptionsByPeople(String examType);


    Map<Long, String> allExamIdNameMap();
}
