package com.youlai.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.TeacherConverter;
import com.youlai.system.mapper.SysTeacherMapper;
import com.youlai.system.model.bo.TeacherSexCountBO;
import com.youlai.system.model.entity.SysTeacher;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.form.ArrangeForm;
import com.youlai.system.model.query.TeacherPageQuery;
import com.youlai.system.model.vo.TeacherExportVO;
import com.youlai.system.model.vo.TeacherPageVO;
import com.youlai.system.service.SysTeacherService;
import com.youlai.system.service.SysArrangeService;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysTeacherServiceImpl extends ServiceImpl<SysTeacherMapper, SysTeacher> implements SysTeacherService {

    private final TeacherConverter teacherConverter;
    private final SysArrangeService arrangeService;
    private final SysClazzService clazzService;
    private final SysCourseService courseService;

    @Override
    public List<ArrangeForm> getTeacherArrangements(Long teacherId) {
        return arrangeService.list(new LambdaQueryWrapper<SysArrange>()
                        .eq(SysArrange::getTeacherId, teacherId)
                        .eq(SysArrange::getStatus, 1)
                        .orderByAsc(SysArrange::getClazzId)
                        .orderByAsc(SysArrange::getCourseId))
                .stream().map(item -> {
                    ArrangeForm form = new ArrangeForm();
                    form.setId(item.getId());
                    form.setClazzId(item.getClazzId());
                    form.setCourseId(item.getCourseId());
                    form.setTeacherId(item.getTeacherId());
                    form.setStatus(item.getStatus());
                    form.setSort(item.getSort());
                    form.setRemark(item.getRemark());
                    return form;
                }).toList();
    }

    @Override
    @Transactional
    public boolean replaceTeacherArrangements(Long teacherId, List<ArrangeForm> arrangements) {
        arrangeService.remove(new LambdaQueryWrapper<SysArrange>().eq(SysArrange::getTeacherId, teacherId));
        if (arrangements == null || arrangements.isEmpty()) return true;
        arrangements.stream().filter(item -> item.getClazzId() != null && item.getCourseId() != null)
                .forEach(item -> {
                    SysArrange entity = new SysArrange();
                    entity.setClazzId(item.getClazzId());
                    entity.setCourseId(item.getCourseId());
                    entity.setTeacherId(teacherId);
                    entity.setStatus(1);
                    entity.setSort(item.getSort());
                    entity.setRemark(item.getRemark());
                    arrangeService.save(entity);
                });
        return true;
    }


    private LambdaQueryWrapper<SysTeacher> builderQuery(TeacherPageQuery queryParams) {
        String keywords = queryParams.getKeywords();
        Integer year = queryParams.getYear();
        LambdaQueryWrapper<SysTeacher> queryWrapper = new LambdaQueryWrapper<SysTeacher>();
        queryWrapper.eq(year != null, SysTeacher::getYear, year);
        queryWrapper.and(StrUtil.isNotBlank(keywords), wrapper -> wrapper.like(StrUtil.isNotBlank(keywords), SysTeacher::getName, keywords).or().like(StrUtil.isNotBlank(keywords), SysTeacher::getCode, keywords).or().like(StrUtil.isNotBlank(keywords), SysTeacher::getPhone, keywords));
        queryWrapper.orderByDesc(SysTeacher::getCreateTime);
        return queryWrapper;
    }

    @Override
    public Page<TeacherPageVO> getTeacherPage(TeacherPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();

        //查询数据
        Page<SysTeacher> teacherPage = this.page(new Page<>(pageNum, pageSize), builderQuery(queryParams));
        Page<TeacherPageVO> result = teacherConverter.entity2Page(teacherPage);
        attachTeachingInfo(result.getRecords());
        return result;
    }

    /** 为教师列表补充任教班级和学科，来源于现有教学安排关系。 */
    private void attachTeachingInfo(List<TeacherPageVO> teachers) {
        if (teachers == null || teachers.isEmpty()) return;
        Set<Long> teacherIds = teachers.stream().map(TeacherPageVO::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<SysArrange> arrangements = arrangeService.list(new LambdaQueryWrapper<SysArrange>().in(SysArrange::getTeacherId, teacherIds).eq(SysArrange::getStatus, 1));
        Map<Long, String> clazzNames = clazzService.listByIds(arrangements.stream().map(SysArrange::getClazzId).filter(Objects::nonNull).distinct().toList())
                .stream().collect(Collectors.toMap(SysClazz::getId, SysClazz::getName, (a, b) -> a));
        Map<Long, String> courseNames = courseService.listByIds(arrangements.stream().map(SysArrange::getCourseId).filter(Objects::nonNull).distinct().toList())
                .stream().collect(Collectors.toMap(SysCourse::getId, SysCourse::getName, (a, b) -> a));
        Map<Long, List<SysArrange>> byTeacher = arrangements.stream().collect(Collectors.groupingBy(SysArrange::getTeacherId));
        teachers.forEach(teacher -> {
            List<SysArrange> items = byTeacher.getOrDefault(teacher.getId(), List.of());
            teacher.setClazzNames(items.stream().map(SysArrange::getClazzId).map(clazzNames::get).filter(StrUtil::isNotBlank).distinct().sorted().collect(Collectors.joining("、")));
            teacher.setCourseNames(items.stream().map(SysArrange::getCourseId).map(courseNames::get).filter(StrUtil::isNotBlank).distinct().sorted().collect(Collectors.joining("、")));
        });
    }

    @Override
    public List<TeacherExportVO> getTeacherExport(TeacherPageQuery queryParams) {
        List<SysTeacher> teacherList = this.list(builderQuery(queryParams));
        return teacherConverter.entity2Export(teacherList);
    }

    @Override
    public boolean saveTeacher(TeacherForm teacherForm) {

        String code = teacherForm.getCode();
        if (StrUtil.isBlank(code)) {
            code = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
            teacherForm.setCode(code);
        }
        if (teacherForm.getSex() == null) teacherForm.setSex(1);
        if (teacherForm.getStatus() == null) teacherForm.setStatus(1);
        long codeCount = this.count(new LambdaQueryWrapper<SysTeacher>().eq(SysTeacher::getCode, code));
        Assert.isTrue(codeCount == 0, "职工编号已存在");

        // 实体转换
        SysTeacher teacher = teacherConverter.form2Entity(teacherForm);
        // Legacy schema columns remain non-functional; local records still need safe placeholders.
        teacher.setAccount("local_" + UUID.randomUUID());
        teacher.setPassword("!LOCAL_ONLY!");
        boolean saved = save(teacher);
        teacherForm.setId(teacher.getId());
        return saved;
    }

    @Override
    public boolean updateTeacher(Long teacherId, TeacherForm teacherForm) {

        String code = teacherForm.getCode();
        SysTeacher existing = this.getById(teacherId);
        if (existing != null && StrUtil.isBlank(code)) {
            code = existing.getCode();
            teacherForm.setCode(code);
        }
        if (existing != null && teacherForm.getSex() == null) teacherForm.setSex(existing.getSex());
        if (existing != null && teacherForm.getStatus() == null) teacherForm.setStatus(existing.getStatus());
        long codeCount = this.count(new LambdaQueryWrapper<SysTeacher>().eq(SysTeacher::getCode, code).ne(SysTeacher::getId, teacherId));
        Assert.isTrue(codeCount == 0, "职工编号已存在");

        // form -> entity
        SysTeacher entity = teacherConverter.form2Entity(teacherForm);
        entity.setId(teacherId);
        teacherForm.setId(teacherId);

        // 修改职工
        return this.updateById(entity);
    }

    @Override
    public TeacherForm getTeacherForm(Long teacherId) {
        SysTeacher entity = this.getById(teacherId);
        return teacherConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteTeachers(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "职工删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteTeachers(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listTeacherOptions() {
        // 查询数据
        List<SysTeacher> teacherList = this.list(new LambdaQueryWrapper<SysTeacher>().select(SysTeacher::getId, SysTeacher::getName).orderByAsc(SysTeacher::getId));

        // 实体转换
        return teacherConverter.entities2Options(teacherList);
    }


    @Override
    public Map<Long, String> allTeacherIdNameMap() {
        List<Option> optionList = listTeacherOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }





    @Override
    public List<TeacherSexCountBO> getAllTeacherSexCount() {
        List<TeacherSexCountBO> teacherSexCountBOList = this.baseMapper.getAllTeacherSexCount();
        teacherSexCountBOList.forEach(teacherSexCountBO -> {
            teacherSexCountBO.setSexLabel(IBaseEnum.getLabelByValue(teacherSexCountBO.getSex(), GenderEnum.class));
        });
        return teacherSexCountBOList;
    }



    @Override
    public SysTeacher getByCode(String code) {
        return getOne(new LambdaQueryWrapper<SysTeacher>().eq(SysTeacher::getCode, code));
    }
}
