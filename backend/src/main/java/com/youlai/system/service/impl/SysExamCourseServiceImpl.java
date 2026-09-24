package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysExamCourseMapper;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.entity.SysExamCourse;
import com.youlai.system.model.form.ExamCourseConfigForm;
import com.youlai.system.model.vo.ExamCourseConfigVO;
import com.youlai.system.service.SysCourseService;
import com.youlai.system.service.SysExamCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysExamCourseServiceImpl extends ServiceImpl<SysExamCourseMapper, SysExamCourse> implements SysExamCourseService {
    private final SysCourseService courseService;

    @Override
    public SysExamCourse getByExamIdAndCourseId(Long examId, Long courseId) {
        return getOne(new LambdaQueryWrapper<SysExamCourse>().eq(SysExamCourse::getExamId, examId).eq(SysExamCourse::getCourseId, courseId));
    }

    @Override
    public boolean hasConfig(Long examId) {
        return examId != null && count(new LambdaQueryWrapper<SysExamCourse>().eq(SysExamCourse::getExamId, examId)) > 0;
    }

    @Override
    public List<ExamCourseConfigVO> getConfig(Long examId) {
        if (examId == null) throw new IllegalArgumentException("考试不能为空");
        Map<Long, SysExamCourse> configured = list(new LambdaQueryWrapper<SysExamCourse>()
                .eq(SysExamCourse::getExamId, examId)).stream()
                .collect(Collectors.toMap(SysExamCourse::getCourseId, Function.identity()));
        return courseService.list(new LambdaQueryWrapper<SysCourse>().orderByAsc(SysCourse::getSort).orderByAsc(SysCourse::getId))
                .stream().map(course -> {
                    SysExamCourse setting = configured.get(course.getId());
                    ExamCourseConfigVO vo = new ExamCourseConfigVO();
                    vo.setCourseId(course.getId()); vo.setCourseName(course.getName()); vo.setDefaultFullScore(course.getFullScore());
                    vo.setSelected(setting != null || configured.isEmpty()); vo.setFullScore(setting == null ? course.getFullScore() == null ? null : course.getFullScore().doubleValue() : setting.getFullScore());
                    vo.setCountInTotal(setting == null || setting.getCountInTotal() == null ? 1 : setting.getCountInTotal());
                    vo.setScoreMode(setting == null || setting.getScoreMode() == null ? "ORIGINAL" : setting.getScoreMode());
                    vo.setScoringRuleId(setting == null ? null : setting.getScoringRuleId());
                    vo.setSort(setting == null || setting.getSort() == null ? Optional.ofNullable(course.getSort()).orElse(0) : setting.getSort());
                    return vo;
                }).toList();
    }

    @Override
    @Transactional
    public boolean saveConfig(Long examId, List<ExamCourseConfigForm> forms) {
        if (examId == null) throw new IllegalArgumentException("考试不能为空");
        List<ExamCourseConfigForm> safeForms = forms == null ? List.of() : forms;
        if (safeForms.isEmpty()) throw new IllegalArgumentException("至少启用一门考试科目");
        Set<Long> ids = new HashSet<>();
        Map<Long, SysCourse> courses = courseService.listByIds(safeForms.stream().filter(Objects::nonNull).map(ExamCourseConfigForm::getCourseId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(SysCourse::getId, Function.identity()));
        for (ExamCourseConfigForm form : safeForms) {
            if (form == null || form.getCourseId() == null || !ids.add(form.getCourseId())) throw new IllegalArgumentException("考试科目不能重复或为空");
            if (!courses.containsKey(form.getCourseId())) throw new IllegalArgumentException("考试科目不存在");
            if (form.getFullScore() == null || form.getFullScore() <= 0) throw new IllegalArgumentException("科目满分必须大于0");
            if (form.getCountInTotal() == null) form.setCountInTotal(1);
            if (form.getCountInTotal() != 0 && form.getCountInTotal() != 1) throw new IllegalArgumentException("计入总分配置无效");
            if (form.getScoreMode() == null || !("ORIGINAL".equals(form.getScoreMode()) || "SCALED".equals(form.getScoreMode()))) throw new IllegalArgumentException("成绩口径配置无效");
        }
        remove(new LambdaQueryWrapper<SysExamCourse>().eq(SysExamCourse::getExamId, examId));
        for (ExamCourseConfigForm form : safeForms) {
            SysExamCourse entity = new SysExamCourse(); entity.setExamId(examId); entity.setCourseId(form.getCourseId()); entity.setFullScore(form.getFullScore()); entity.setCountInTotal(form.getCountInTotal()); entity.setScoreMode(form.getScoreMode()); entity.setScoringRuleId(form.getScoringRuleId()); entity.setSort(form.getSort() == null ? 0 : form.getSort()); save(entity);
        }
        return true;
    }
}
