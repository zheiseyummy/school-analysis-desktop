package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.ExamConverter;
import com.youlai.system.mapper.SysExamMapper;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.form.ExamForm;
import com.youlai.system.model.query.ClazzExamAnalysisQuery;
import com.youlai.system.model.query.ExamPageQuery;
import com.youlai.system.model.vo.ExamPageVO;
import com.youlai.system.service.SysExamService;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysExamBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysExamServiceImpl extends ServiceImpl<SysExamMapper, SysExam> implements SysExamService {

    private final ExamConverter examConverter;
    private final SysClazzService clazzService;
    private final SysExamBodyService examBodyService;


    @Override
    public Page<ExamPageVO> getExamPage(ExamPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        Integer year =queryParams.getYear();
        String examType = queryParams.getExamType();
        Integer semester = queryParams.getSemester();

        LambdaQueryWrapper<SysExam> queryWrapper = new LambdaQueryWrapper<SysExam>();
        queryWrapper.eq(year != null, SysExam::getYear, year);
        queryWrapper.eq(semester != null, SysExam::getSemester, semester);
        queryWrapper.eq(examType != null, SysExam::getExamType, examType);
        queryWrapper.and(StrUtil.isNotBlank(keywords),
                wrapper ->
                        wrapper.like(StrUtil.isNotBlank(keywords), SysExam::getName, keywords)
                                .or()
                                .like(StrUtil.isNotBlank(keywords), SysExam::getCode, keywords)
        );
        //查询数据
        Page<SysExam> examPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return examConverter.entity2Page(examPage);
    }

    @Override
    @Transactional
    public boolean saveExam(ExamForm examForm) {
        String name = examForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysExam>().eq(SysExam::getName, name));
        Assert.isTrue(nameCount == 0, "考试名称已存在");

        String code = examForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysExam>().eq(SysExam::getCode, code));
        Assert.isTrue(codeCount == 0, "考试编号已存在");

        // 实体转换
        SysExam exam = examConverter.form2Entity(examForm);
        boolean saved = save(exam);
        if (saved) {
            clazzService.list().forEach(clazz -> {
                SysExamBody body = new SysExamBody();
                body.setExamId(exam.getId());
                body.setGradeClazzId(clazz.getId());
                body.setGOrC("C");
                examBodyService.save(body);
            });
        }
        return saved;
    }

    @Override
    public boolean updateExam(Long examId, ExamForm examForm) {
        String name = examForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysExam>()
                .eq(SysExam::getName, name)
                .ne(SysExam::getId, examId)
        );
        Assert.isTrue(nameCount == 0, "考试名称已存在");

        String code = examForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysExam>()
                .eq(SysExam::getCode, code)
                .ne(SysExam::getId, examId));
        Assert.isTrue(codeCount == 0, "考试编号已存在");

        // form -> entity
        SysExam entity = examConverter.form2Entity(examForm);

        // 修改考试
        return this.updateById(entity);
    }

    @Override
    public ExamForm getExamForm(Long examId) {
        SysExam entity = this.getById(examId);
        return examConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteExams(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "考试删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteExams(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listExamOptions() {
        // 查询数据
        List<SysExam> examList = this.list(new LambdaQueryWrapper<SysExam>()
                .select(SysExam::getId, SysExam::getName)
                .orderByAsc(SysExam::getSort)
        );

        // 实体转换
        return examConverter.entities2Options(examList);
    }

    @Override
    public List<Option> listExamOptions(ClazzExamAnalysisQuery query) {
        Integer year = query.getYear();
        Integer semester = query.getSemester();
        String examType = query.getExamType();
        List<Long> examIdList = query.getExamIdList();
        LambdaQueryWrapper<SysExam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(year != null, SysExam::getYear, year);
        queryWrapper.eq(semester != null, SysExam::getSemester, semester);
        queryWrapper.eq(examType != null, SysExam::getExamType, examType);
        queryWrapper.in(CollectionUtil.isNotEmpty(examIdList), SysExam::getId, examIdList);
        queryWrapper.select(SysExam::getId, SysExam::getName);
        queryWrapper.orderByAsc(SysExam::getSort);
        List<SysExam> examList = this.list(queryWrapper);
        // 实体转换
        return examConverter.entities2Options(examList);
    }

    @Override
    public List<Option> listExamOptions(List<Long> examIdList) {
        // 查询数据
        List<SysExam> examList = this.list(new LambdaQueryWrapper<SysExam>()
                .in(SysExam::getId, examIdList)
                .select(SysExam::getId, SysExam::getName)
                .orderByAsc(SysExam::getSort)
        );

        // 实体转换
        return examConverter.entities2Options(examList);
    }

    @Override
    public List<Option> listExamOptionsByPeople(String examType) {
        // 查询数据
        List<SysExam> examList = this.list(new LambdaQueryWrapper<SysExam>()
                .eq(SysExam::getExamType, examType)
                .select(SysExam::getId, SysExam::getName)
                .orderByAsc(SysExam::getSort)
        );

        // 实体转换
        return examConverter.entities2Options(examList);
    }

    @Override
    public Map<Long, String> allExamIdNameMap() {
        List<Option> optionList = listExamOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }
}
