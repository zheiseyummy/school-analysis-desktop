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
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.query.TeacherPageQuery;
import com.youlai.system.model.vo.TeacherExportVO;
import com.youlai.system.model.vo.TeacherPageVO;
import com.youlai.system.service.SysTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysTeacherServiceImpl extends ServiceImpl<SysTeacherMapper, SysTeacher> implements SysTeacherService {

    private final TeacherConverter teacherConverter;


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
        return teacherConverter.entity2Page(teacherPage);
    }

    @Override
    public List<TeacherExportVO> getTeacherExport(TeacherPageQuery queryParams) {
        List<SysTeacher> teacherList = this.list(builderQuery(queryParams));
        return teacherConverter.entity2Export(teacherList);
    }

    @Override
    public boolean saveTeacher(TeacherForm teacherForm) {

        String code = teacherForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysTeacher>().eq(SysTeacher::getCode, code));
        Assert.isTrue(codeCount == 0, "职工编号已存在");

        // 实体转换
        SysTeacher teacher = teacherConverter.form2Entity(teacherForm);
        // Compatibility only: the transitional MySQL schema requires these columns.
        teacher.setAccount("local_" + UUID.randomUUID());
        teacher.setPassword("!LOCAL_ONLY!");
        boolean saved = save(teacher);
        teacherForm.setId(teacher.getId());
        return saved;
    }

    @Override
    public boolean updateTeacher(Long teacherId, TeacherForm teacherForm) {

        String code = teacherForm.getCode();
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
