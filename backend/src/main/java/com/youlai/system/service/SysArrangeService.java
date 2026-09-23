package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.bo.ClazzArrangeBO;
import com.youlai.system.model.bo.ClazzCourseBO;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.form.ArrangeForm;
import com.youlai.system.model.query.ArrangePageQuery;
import com.youlai.system.model.vo.ArrangePageVO;

import java.util.List;
import java.util.Map;

/**
 * 教学安排接口
 *
 * @author haoxr
 * @since 2023/3/4
 */
public interface SysArrangeService extends IService<SysArrange> {
    /**
     * 教学安排数据项分页列表
     *
     * @param queryParams
     * @return
     */
    Page<ArrangePageVO> getArrangePage(ArrangePageQuery queryParams);

    /**
     * 教学安排数据项表单
     *
     * @param id 教学安排数据项ID
     * @return
     */
    ArrangeForm getArrangeForm(Long id);

    /**
     * 新增教学安排数据项
     *
     * @param arrangeForm 教学安排数据项表单
     * @return
     */
    boolean saveArrange(ArrangeForm arrangeForm);

    /**
     * 修改教学安排数据项
     *
     * @param id       教学安排数据项ID
     * @param arrangeForm 教学安排数据项表单
     * @return
     */
    boolean updateArrange(Long id, ArrangeForm arrangeForm);

    /**
     * 删除教学安排数据项
     *
     * @param idsStr 教学安排数据项ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteArrange(String idsStr);

    /**
     * 获取教学安排下拉列表
     *
     * @param clazzId
     * @return
     */
    List<Option> listArrangeOptions(Long clazzId);


    SysArrange getOneByClazzIdAndCourseId(Long clazzId, Long courseId);

    SysArrange getOneByClazzIdAndCourseIdAndNotId(Long clazzId, Long courseId, Long id);

    List<ClazzArrangeBO> getAllClazzArrangeList();


    Map<Long, ClazzArrangeBO> getAllClazzArrangeMap();


    List<Long> getCourseIdListByClazzId(Long clazzId);

    List<Long> getCourseIdListByClazzIdList(List<Long> clazzIdList);

    List<Long> getCourseIdListByClazzId(Long clazzId, Long courseId);


    List<ClazzCourseBO> getClazzCourseBOList(Long clazzId);

    List<Long> getClazzIdListByCourseIdAndTeacherId(Long courseId, Long teacherId);

}
