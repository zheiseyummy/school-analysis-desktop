package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.ArrangeConverter;
import com.youlai.system.mapper.SysArrangeMapper;
import com.youlai.system.model.bo.ClazzArrangeBO;
import com.youlai.system.model.bo.ClazzCourseBO;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.form.ArrangeForm;
import com.youlai.system.model.query.ArrangePageQuery;
import com.youlai.system.model.query.ClazzCourseQuery;
import com.youlai.system.model.vo.ArrangePageVO;
import com.youlai.system.service.SysArrangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据教学安排项业务实现类
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class SysArrangeServiceImpl extends ServiceImpl<SysArrangeMapper, SysArrange> implements SysArrangeService {

    private final ArrangeConverter arrangeConverter;

    /**
     * 教学安排数据项分页列表
     *
     * @param queryParams
     * @return
     */
    @Override
    public Page<ArrangePageVO> getArrangePage(ArrangePageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<ArrangePageVO> pageResult = this.baseMapper.getArrangePage(new Page<>(pageNum, pageSize), queryParams);
        return pageResult;
    }

    /**
     * 教学安排数据项表单详情
     *
     * @param id 教学安排数据项ID
     * @return
     */
    @Override
    public ArrangeForm getArrangeForm(Long id) {
        // 获取entity
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArrange::getId, id);
        SysArrange entity = this.getOne(queryWrapper);
        Assert.isTrue(entity != null, "教学安排数据项不存在");

        // 实体转换
        ArrangeForm arrangeForm = arrangeConverter.entity2Form(entity);
        return arrangeForm;
    }

    /**
     * 新增教学安排数据项
     *
     * @param arrangeForm 教学安排数据项表单
     * @return
     */
    @Override
    public boolean saveArrange(ArrangeForm arrangeForm) {
        // 实体对象转换 form->entity
        SysArrange entity = arrangeConverter.form2Entity(arrangeForm);
        SysArrange db = this.getOneByClazzIdAndCourseId(arrangeForm.getClazzId(), arrangeForm.getCourseId());
        Assert.isTrue(db == null, "教学安排数据项已存在");
        // 持久化
        boolean result = this.save(entity);
        return result;
    }

    /**
     * 修改教学安排数据项
     *
     * @param id          教学安排数据项ID
     * @param arrangeForm 教学安排数据项表单
     * @return
     */
    @Override
    public boolean updateArrange(Long id, ArrangeForm arrangeForm) {
        SysArrange entity = arrangeConverter.form2Entity(arrangeForm);
        SysArrange db = this.getOneByClazzIdAndCourseIdAndNotId(arrangeForm.getClazzId(), arrangeForm.getCourseId(), id);
        Assert.isTrue(db == null, "教学安排数据项已存在");
        boolean result = this.updateById(entity);
        return result;
    }

    /**
     * 删除教学安排数据项
     *
     * @param idsStr 教学安排数据项ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteArrange(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除数据为空");
        //
        List<Long> ids = Arrays.asList(idsStr.split(",")).stream().map(id -> Long.parseLong(id)).collect(Collectors.toList());

        // 删除教学安排数据项
        boolean result = this.removeByIds(ids);
        return result;
    }

    /**
     * 获取教学安排下拉列表
     *
     * @param clazzId
     * @return
     */
    @Override
    public List<Option> listArrangeOptions(Long clazzId) {
        // 数据教学安排项
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<SysArrange>();
        queryWrapper.eq(SysArrange::getClazzId, clazzId).select(SysArrange::getCourseId, SysArrange::getTeacherId);
        List<SysArrange> arrangeList = this.list(queryWrapper);

        // 转换下拉数据
        List<Option> options = CollectionUtil.emptyIfNull(arrangeList).stream().map(arrangeItem -> new Option(arrangeItem.getCourseId(), arrangeItem.getTeacherId().toString())).collect(Collectors.toList());
        return options;
    }

    @Override
    public SysArrange getOneByClazzIdAndCourseId(Long clazzId, Long courseId) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArrange::getClazzId, clazzId);
        queryWrapper.eq(SysArrange::getCourseId, courseId);
        return getOne(queryWrapper);
    }

    @Override
    public SysArrange getOneByClazzIdAndCourseIdAndNotId(Long clazzId, Long courseId, Long id) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArrange::getClazzId, clazzId);
        queryWrapper.eq(SysArrange::getCourseId, courseId);
        queryWrapper.ne(SysArrange::getId, id);
        return getOne(queryWrapper);
    }

    @Override
    public List<ClazzArrangeBO> getAllClazzArrangeList() {
        return this.baseMapper.getAllClazzArrangeList();
    }

    @Override
    public Map<Long, ClazzArrangeBO> getAllClazzArrangeMap() {
        List<ClazzArrangeBO> clazzArrangeBOList = getAllClazzArrangeList();
        return clazzArrangeBOList.stream().collect(Collectors.toMap(ClazzArrangeBO::getClazzId, it -> it));
    }

    @Override
    public List<Long> getCourseIdListByClazzId(Long clazzId) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArrange::getClazzId, clazzId).select(SysArrange::getCourseId);
        return list(queryWrapper).stream().map(SysArrange::getCourseId).toList();
    }

    @Override
    public List<Long> getCourseIdListByClazzIdList(List<Long> clazzIdList) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysArrange::getClazzId, clazzIdList).select(SysArrange::getCourseId);
        return list(queryWrapper).stream().map(SysArrange::getCourseId).toList();
    }

    @Override
    public List<Long> getCourseIdListByClazzId(Long clazzId, Long courseId) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysArrange::getClazzId, clazzId).eq(SysArrange::getCourseId, courseId).select(SysArrange::getCourseId);
        return list(queryWrapper).stream().map(SysArrange::getCourseId).toList();
    }

    @Override
    public List<ClazzCourseBO> getClazzCourseBOList(Long clazzId) {
        ClazzCourseQuery query = new ClazzCourseQuery();
        query.setClazzId(clazzId);
        return this.baseMapper.getClazzCourseBOList(query);
    }

    @Override
    public List<Long> getClazzIdListByCourseIdAndTeacherId(Long courseId, Long teacherId) {
        LambdaQueryWrapper<SysArrange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(courseId != null, SysArrange::getCourseId, courseId);
        queryWrapper.eq(teacherId != null, SysArrange::getTeacherId, teacherId);
        queryWrapper.select(SysArrange::getClazzId);
        return list(queryWrapper).stream().map(SysArrange::getClazzId).distinct().toList();
    }
}




