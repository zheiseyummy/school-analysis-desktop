package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.form.GradeForm;
import com.youlai.system.model.query.GradePageQuery;
import com.youlai.system.model.vo.GradePageVO;

import java.util.List;
import java.util.Map;

public interface SysGradeService extends IService<SysGrade> {
    /**
     * 年级分页列表
     *
     * @param queryParams
     * @return
     */
    Page<GradePageVO> getGradePage(GradePageQuery queryParams);

    /**
     * 保存年级
     *
     * @param gradeForm
     * @return
     */
    boolean saveGrade(GradeForm gradeForm);


    /**
     * 更新年级
     *
     * @param gradeForm
     * @return
     */
    boolean updateGrade(Long gradeId, GradeForm gradeForm);

    /**
     * 查询单个年级
     *
     * @param gradeId
     * @return
     */
    GradeForm getGradeForm(Long gradeId);

    /**
     * 删除年级列表
     *
     * @param idsStr
     * @return
     */
    boolean deleteGrades(String idsStr);

    /**
     * 删除年级列表
     *
     * @param idList
     * @return
     */
    boolean deleteGrades(List<Long> idList);

    /**
     * 年级下拉列表
     *
     * @return
     */
    List<Option> listGradeOptions();

    Map<Long, String> allGradeIdNameMap();

    SysGrade getByGradeName(String gradeName);
}
