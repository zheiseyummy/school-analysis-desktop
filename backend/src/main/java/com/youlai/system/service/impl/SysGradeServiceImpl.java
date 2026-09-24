package com.youlai.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.GradeConverter;
import com.youlai.system.mapper.SysGradeMapper;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.form.GradeForm;
import com.youlai.system.model.query.GradePageQuery;
import com.youlai.system.model.vo.GradePageVO;
import com.youlai.system.service.SysGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 年级业务接口
 */
@Service
@RequiredArgsConstructor
public class SysGradeServiceImpl extends ServiceImpl<SysGradeMapper, SysGrade> implements SysGradeService {
    private final GradeConverter gradeConverter;

    @Override
    public Page<GradePageVO> getGradePage(GradePageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        Long managerId = queryParams.getManagerId();

        LambdaQueryWrapper<SysGrade> queryWrapper = new LambdaQueryWrapper<SysGrade>()
                .eq(managerId != null, SysGrade::getManagerId, managerId)
                .and(StrUtil.isNotBlank(keywords),
                        wrapper ->
                                wrapper.like(StrUtil.isNotBlank(keywords), SysGrade::getName, keywords)
                                        .or().like(StrUtil.isNotBlank(keywords), SysGrade::getCode, keywords));
        //查询数据
        Page<SysGrade> gradePage = this.page(new Page<>(pageNum, pageSize), queryWrapper);

        return gradeConverter.entity2Page(gradePage);
    }

    @Override
    public boolean saveGrade(GradeForm gradeForm) {

        String code = gradeForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysGrade>().eq(SysGrade::getCode, code));
        Assert.isTrue(codeCount == 0, "年级编号已存在");

        String name = gradeForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysGrade>().eq(SysGrade::getName, name));
        Assert.isTrue(nameCount == 0, "年级名称已存在");
        // 实体转换
        SysGrade grade = gradeConverter.form2Entity(gradeForm);
        if (!"初中".equals(grade.getStage()) && !"高中".equals(grade.getStage())) grade.setStage("高中");
        return save(grade);
    }

    @Override
    public boolean updateGrade(Long gradeId, GradeForm gradeForm) {

        String code = gradeForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysGrade>()
                .eq(SysGrade::getCode, code)
                .ne(SysGrade::getId, gradeId)
        );
        Assert.isTrue(codeCount == 0, "年级编号已存在");

        String name = gradeForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysGrade>()
                .eq(SysGrade::getName, name)
                .ne(SysGrade::getId, gradeId)
        );
        Assert.isTrue(nameCount == 0, "年级名称已存在");

        // form -> entity
        SysGrade entity = gradeConverter.form2Entity(gradeForm);
        if (!"初中".equals(entity.getStage()) && !"高中".equals(entity.getStage())) entity.setStage("高中");

        // 修改年级
        return this.updateById(entity);
    }

    @Override
    public GradeForm getGradeForm(Long gradeId) {
        SysGrade entity = this.getById(gradeId);
        return gradeConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteGrades(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "年级删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteGrades(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listGradeOptions() {
        // 查询数据
        List<SysGrade> gradeList = this.list(new LambdaQueryWrapper<SysGrade>()
                .select(SysGrade::getId, SysGrade::getName)
                .orderByAsc(SysGrade::getSort)
        );

        // 实体转换
        return gradeConverter.entities2Options(gradeList);
    }

    @Override
    public Map<Long, String> allGradeIdNameMap() {
        List<Option> optionList = listGradeOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }

    @Override
    public SysGrade getByGradeName(String gradeName) {
        LambdaQueryWrapper<SysGrade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysGrade::getName, gradeName);
        return getOne(queryWrapper);
    }
}
