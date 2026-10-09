package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysExamBodyMapper;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.query.ExamBodyPageQuery;
import com.youlai.system.model.vo.ExamBodyPageVO;
import com.youlai.system.service.SysExamBodyService;
import com.youlai.system.service.SysClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysExamBodyServiceImpl extends ServiceImpl<SysExamBodyMapper, SysExamBody> implements SysExamBodyService {
    private final SysClazzService clazzService;

    @Override
    public Page<ExamBodyPageVO> getExamBodyPage(ExamBodyPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<ExamBodyPageVO> pageResult = this.baseMapper.getExamBodyPage(new Page<>(pageNum, pageSize), queryParams);
        return pageResult;
    }

    @Override
    public List<SysExamBody> getListByExamId(Long examId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getExamId, examId);
        return list(queryWrapper);
    }

    @Override
    public List<Long> getGradeIdListByExamId(Long examId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getExamId, examId);
        queryWrapper.eq(SysExamBody::getGOrC, "G");
        queryWrapper.select(SysExamBody::getGradeClazzId);
        return list(queryWrapper).stream().map(SysExamBody::getGradeClazzId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getClazzIdListByExamId(Long examId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getExamId, examId);
        queryWrapper.eq(SysExamBody::getGOrC, "C");
        queryWrapper.select(SysExamBody::getGradeClazzId);
        return list(queryWrapper).stream().map(SysExamBody::getGradeClazzId).collect(Collectors.toList());
    }

    @Override
    public boolean removeByExamId(Long examId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getExamId, examId);
        return remove(queryWrapper);
    }

    @Override
    public List<Long> getExamIdListByClazzId(Long clazzId) {
        return getExamIdListByClazzIdList(List.of(clazzId));
    }

    @Override
    public List<Long> getExamIdListByClazzIdList(List<Long> clazzIdList) {
        if (clazzIdList == null || clazzIdList.isEmpty()) return List.of();
        List<Long> examIds = new java.util.ArrayList<>();
        LambdaQueryWrapper<SysExamBody> clazzQuery = new LambdaQueryWrapper<>();
        clazzQuery.in(SysExamBody::getGradeClazzId, clazzIdList);
        clazzQuery.eq(SysExamBody::getGOrC, "C");
        clazzQuery.select(SysExamBody::getExamId);
        examIds.addAll(list(clazzQuery).stream().map(SysExamBody::getExamId).toList());

        List<Long> gradeIds = clazzService.listByIds(clazzIdList).stream()
                .map(com.youlai.system.model.entity.SysClazz::getGradeId).filter(java.util.Objects::nonNull).distinct().toList();
        for (Long gradeId : gradeIds) examIds.addAll(getExamIdListByGradeId(gradeId));
        return examIds.stream().distinct().toList();
    }

    @Override
    public List<Long> getExamIdListByGradeId(Long gradeId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getGradeClazzId, gradeId);
        queryWrapper.eq(SysExamBody::getGOrC, "G");
        queryWrapper.select(SysExamBody::getExamId);
        return list(queryWrapper).stream().map(SysExamBody::getExamId).distinct().toList();
    }

    @Override
    public void removeByClazzId(Long clazzId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getGradeClazzId, clazzId);
        queryWrapper.eq(SysExamBody::getGOrC, "C");
        remove(queryWrapper);
    }

    @Override
    public SysExamBody getIdByExamIdAndClazzId(Long examId, Long clazzId) {
        LambdaQueryWrapper<SysExamBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysExamBody::getExamId, examId);
        queryWrapper.eq(SysExamBody::getGradeClazzId, clazzId);
        queryWrapper.eq(SysExamBody::getGOrC, "C");
        queryWrapper.select(SysExamBody::getId);
        return getOne(queryWrapper);
    }


}
