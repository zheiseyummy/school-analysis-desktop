package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.bo.GradeClazzBO;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.form.ClazzForm;
import com.youlai.system.model.query.ClazzPageQuery;
import com.youlai.system.model.vo.ClazzPageVO;

import java.util.List;
import java.util.Map;

public interface SysClazzService extends IService<SysClazz> {
    /**
     * 班级分页列表
     *
     * @param queryParams
     * @return
     */
    Page<ClazzPageVO> getClazzPage(ClazzPageQuery queryParams);

    /**
     * 保存班级
     *
     * @param clazzForm
     * @return
     */
    boolean saveClazz(ClazzForm clazzForm);


    /**
     * 更新班级
     *
     * @param clazzForm
     * @return
     */
    boolean updateClazz(Long clazzId, ClazzForm clazzForm);

    /**
     * 查询单个班级
     *
     * @param clazzId
     * @return
     */
    ClazzForm getClazzForm(Long clazzId);

    /**
     * 删除班级列表
     *
     * @param idsStr
     * @return
     */
    boolean deleteClazzs(String idsStr);

    /**
     * 删除班级列表
     *
     * @param idList
     * @return
     */
    boolean deleteClazzs(List<Long> idList);

    /**
     * 班级下拉列表
     *
     * @return
     */
    List<Option> listClazzOptions();

    /**
     * 获取所有年级下班级数量和班级名称列表统计查询
     *
     * @return
     */
    List<GradeClazzBO> getAllGradeClazzCountNameList();


    /**
     * 获取所有年级下班级数量和班级名称列表统计查询
     *
     * @return
     */
    Map<Long, GradeClazzBO> getAllGradeClazzCountNameMap();

    SysClazz getByClazzName(Long gradeId, String clazzName);


    /**
     * 获取年级下班级的数量
     *
     * @param gradeIdList
     * @return
     */
    Long countByGradeIdList(List<Long> gradeIdList);


    /**
     * 获取年级下班级ID集合列表
     *
     * @param gradeId
     * @return
     */
    List<Long> clazzIdListByGradeId(Long gradeId);


    List<Long> gradeIdListByClazzIdList(List<Long> clazzIdList);
}
