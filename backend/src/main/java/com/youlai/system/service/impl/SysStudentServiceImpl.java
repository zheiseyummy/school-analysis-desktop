package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.StudentConverter;
import com.youlai.system.mapper.SysStudentMapper;
import com.youlai.system.model.bo.StudentSexCountBO;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.query.StudentPageQuery;
import com.youlai.system.model.vo.StudentPageVO;
import com.youlai.system.service.SysStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysStudentServiceImpl extends ServiceImpl<SysStudentMapper, SysStudent> implements SysStudentService {

    private final StudentConverter studentConverter;



    LambdaQueryWrapper<SysStudent> builderQuery(StudentPageQuery queryParams) {
        String keywords = queryParams.getKeywords();
        List<Long> studentIdList = queryParams.getStudentIdList();


        Integer year = queryParams.getYear();

        LambdaQueryWrapper<SysStudent> queryWrapper = new LambdaQueryWrapper<SysStudent>();
        queryWrapper.eq(year != null, SysStudent::getYear, year);
        queryWrapper.in(CollectionUtil.isNotEmpty(studentIdList), SysStudent::getId, studentIdList);
        queryWrapper.and(StrUtil.isNotBlank(keywords), wrapper -> wrapper.like(StrUtil.isNotBlank(keywords), SysStudent::getName, keywords)
                .or().like(StrUtil.isNotBlank(keywords), SysStudent::getCode, keywords)
                .or().like(StrUtil.isNotBlank(keywords), SysStudent::getPhone, keywords)).orderByDesc(SysStudent::getCreateTime);

        return queryWrapper;
    }

    @Override
    public Page<StudentPageVO> getStudentPage(StudentPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        List<Long> studentIdList = queryParams.getStudentIdList();
        if (queryParams.getClazzId() != null && CollectionUtil.isEmpty(studentIdList)) {
            return new Page<>(pageNum, pageSize);
        }
        LambdaQueryWrapper<SysStudent> queryWrapper = builderQuery(queryParams);
        //查询数据
        Page<SysStudent> studentPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return studentConverter.entity2Page(studentPage);
    }

    @Override
    public List<SysStudent> getStudentExport(StudentPageQuery queryParams) {
        List<Long> studentIdList = queryParams.getStudentIdList();
        if (queryParams.getClazzId() != null && CollectionUtil.isEmpty(studentIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysStudent> queryWrapper = builderQuery(queryParams);
        List<SysStudent> studentList = this.list(queryWrapper);
        return studentList;
    }

    @Override
    public boolean saveStudent(StudentForm studentForm) {

        String code = studentForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysStudent>().eq(SysStudent::getCode, code));
        Assert.isTrue(codeCount == 0, "学生编号已存在");

        // 实体转换
        SysStudent student = studentConverter.form2Entity(studentForm);
        // Legacy schema columns remain non-functional; local records still need safe placeholders.
        student.setAccount("local_" + UUID.randomUUID());
        student.setPassword("!LOCAL_ONLY!");
        Boolean flag = save(student);
        studentForm.setId(student.getId());
        return flag;
    }

    @Override
    public boolean updateStudent(Long studentId, StudentForm studentForm) {

        String code = studentForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysStudent>().eq(SysStudent::getCode, code).ne(SysStudent::getId, studentId));
        Assert.isTrue(codeCount == 0, "学生编号已存在");

        // form -> entity
        SysStudent entity = studentConverter.form2Entity(studentForm);
        entity.setId(studentId);
        studentForm.setId(studentId);

        // 修改学生
        return this.updateById(entity);
    }

    @Override
    public StudentForm getStudentForm(Long studentId) {
        SysStudent entity = this.getById(studentId);
        return studentConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteStudents(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "学生删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteStudents(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listStudentOptions() {
        // 查询数据
        List<SysStudent> studentList = this.list(new LambdaQueryWrapper<SysStudent>().select(SysStudent::getId, SysStudent::getName).orderByAsc(SysStudent::getId));

        // 实体转换
        return studentConverter.entities2Options(studentList);
    }

    @Override
    public Map<Long, String> allStudentIdNameMap() {
        List<Option> optionList = listStudentOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }



    @Override
    public List<StudentSexCountBO> getAllStudentSexCount() {
        List<StudentSexCountBO> studentSexCountBOList = this.baseMapper.getAllStudentSexCount();
        studentSexCountBOList.forEach(studentSexCountBO -> {
            studentSexCountBO.setSexLabel(IBaseEnum.getLabelByValue(studentSexCountBO.getSex(), GenderEnum.class));
        });

        return studentSexCountBOList;
    }




    @Override
    public SysStudent getByCode(String code) {
        LambdaQueryWrapper<SysStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysStudent::getCode, code);
        return getOne(queryWrapper);
    }
}
