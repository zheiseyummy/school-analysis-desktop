package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysClazzStudentMapper;
import com.youlai.system.model.bo.ClazzStudentBO;
import com.youlai.system.model.bo.ClazzYearBO;
import com.youlai.system.model.bo.StudentClazzBO;
import com.youlai.system.model.dto.StudentInfo;
import com.youlai.system.model.entity.SysClazzStudent;
import com.youlai.system.service.SysClazzStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysClazzStudentServiceImpl extends ServiceImpl<SysClazzStudentMapper, SysClazzStudent> implements SysClazzStudentService {
    @Override
    public boolean saveOrUpdateClazzStudent(Long studentId, Long clazzId, Integer year) {

        if (studentId == null || clazzId == null || year == null) {
            return false;
        }

        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId);
        queryWrapper.eq(SysClazzStudent::getClazzId, clazzId);
        queryWrapper.eq(SysClazzStudent::getYear, year);
        SysClazzStudent sysClazzStudent = getOne(queryWrapper);
        if (sysClazzStudent != null) {
            sysClazzStudent.setYear(year);
            update(sysClazzStudent, queryWrapper);
        } else {
            sysClazzStudent = new SysClazzStudent();
            sysClazzStudent.setStudentId(studentId);
            sysClazzStudent.setClazzId(clazzId);
            sysClazzStudent.setYear(year);
            save(sysClazzStudent);
        }
        return true;
    }

    @Override
    public List<ClazzYearBO> getByStudentId(Long studentId) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId).orderByDesc(SysClazzStudent::getYear);
        List<SysClazzStudent> clazzStudentList = list(queryWrapper);
        return clazzStudentList.stream().map(it -> {
            ClazzYearBO clazzYearBO = new ClazzYearBO();
            clazzYearBO.setClazzId(it.getClazzId());
            clazzYearBO.setYear(it.getYear());
            return clazzYearBO;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteByStudentId(Long studentId) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId);
        remove(queryWrapper);
    }

    @Override
    public List<ClazzStudentBO> getAllClazzStudentCountNameList(Integer year) {
        return this.baseMapper.getAllClazzStudentCountNameList(year);
    }

    @Override
    public Map<Long, ClazzStudentBO> getAllClazzStudentCountNameMap(Integer year) {
        return getAllClazzStudentCountNameList(year).stream().collect(Collectors.toMap(ClazzStudentBO::getClazzId, it -> it));
    }

    @Override
    public List<Long> getStudentIdListBy(Long clazzId, Integer year) {

        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getClazzId, clazzId);
        queryWrapper.eq(SysClazzStudent::getYear, year);
        queryWrapper.select(SysClazzStudent::getStudentId);

        return list(queryWrapper).stream().map(SysClazzStudent::getStudentId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<StudentClazzBO> getStudentClazzCountNameList(List<Long> studentIdList) {
        return this.baseMapper.getStudentClazzCountNameList(studentIdList);
    }

    @Override
    public Map<Long, StudentClazzBO> getStudentClazzCountNameMap(List<Long> studentIdList) {
        return getStudentClazzCountNameList(studentIdList).stream().collect(Collectors.toMap(StudentClazzBO::getStudentId, it -> it));
    }

    @Override
    public SysClazzStudent getLastOneBy(Long studentId) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId).orderByDesc(SysClazzStudent::getYear).last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    public Long studentCountBy(Long clazzId, Integer year) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getClazzId, clazzId);
        queryWrapper.eq(SysClazzStudent::getYear, year);
        return count(queryWrapper);
    }

    @Override
    public void removeByStudentId(Long studentId) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId);
        remove(queryWrapper);
    }

    @Override
    public void removeByClazzId(Long clazzId) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getClazzId, clazzId);
        remove(queryWrapper);
    }

    @Override
    public List<StudentInfo> getStudentInfoListByGradeIdAndYear(Long gradeId, Integer year) {
        return this.baseMapper.getStudentInfoListByGradeIdAndYear(gradeId, year);
    }

    @Override
    public List<StudentInfo> getStudentInfoListByClazzIdListAndYear(List<Long> clazzIdList, Integer year) {
        return this.baseMapper.getStudentInfoListByClazzIdListAndYear(clazzIdList, year);
    }

    @Override
    public Long getClazzIdByCondition(List<Long> clazzIdList, Long studentId, Integer year) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysClazzStudent::getClazzId, clazzIdList);
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId);
        queryWrapper.eq(SysClazzStudent::getYear, year);
        queryWrapper.select(SysClazzStudent::getClazzId);
        return getOne(queryWrapper).getClazzId();
    }

    @Override
    public boolean existStudentInClazz(Long clazzId, Long studentId, Integer year) {
        LambdaQueryWrapper<SysClazzStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazzStudent::getClazzId, clazzId);
        queryWrapper.eq(SysClazzStudent::getStudentId, studentId);
        queryWrapper.eq(SysClazzStudent::getYear, year);
        return count(queryWrapper) > 0;
    }
}
