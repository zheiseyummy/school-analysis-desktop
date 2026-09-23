package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.query.ExamBodyPageQuery;
import com.youlai.system.model.vo.ExamBodyPageVO;

import java.util.List;

public interface SysExamBodyService extends IService<SysExamBody> {

    Page<ExamBodyPageVO> getExamBodyPage(ExamBodyPageQuery queryParams);


    /**
     * @param examId
     * @return
     */
    List<SysExamBody> getListByExamId(Long examId);


    /**
     * 获取考核年级ID列表
     *
     * @param examId
     * @return
     */
    List<Long> getGradeIdListByExamId(Long examId);

    /**
     * 获取考核班级ID列表
     *
     * @param examId
     * @return
     */
    List<Long> getClazzIdListByExamId(Long examId);

    /**
     * 根据考试ID删除
     *
     * @param examId
     * @return
     */
    boolean removeByExamId(Long examId);

    /**
     * 获取某个的所有考试ID集合
     *
     * @param clazzId
     * @return
     */
    List<Long> getExamIdListByClazzId(Long clazzId);

    /**
     * 获取某个年级的所有考试ID集合
     *
     * @param clazzIdList
     * @return
     */
    List<Long> getExamIdListByClazzIdList(List<Long> clazzIdList);

    /**
     * 删除班级的考试信息
     *
     * @param clazzId
     */
    void removeByClazzId(Long clazzId);


    /**
     * 根据考试ID和班级ID查询考试主体ID
     *
     * @param examId
     * @param clazzId
     * @return
     */
    SysExamBody getIdByExamIdAndClazzId(Long examId, Long clazzId);
}
