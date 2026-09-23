package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.util.CommonUtils;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SysGradeService gradeService;

    private final SysClazzService clazzService;

    private final SysStudentService studentService;

    private final SysClazzStudentService clazzStudentService;

    private final SysExamBodyService examBodyService;

    @Override
    public List<Option<Long>> listGradeClazzOptions() {
        List<SysGrade> gradeList = gradeService.list();
        List<SysClazz> clazzList = clazzService.list();
        List<Option<Long>> optionList = new ArrayList<>();
        gradeList.forEach(grade -> {
            Option<Long> parent = new Option<>(grade.getId(), grade.getName());
            parent.setChildren(new ArrayList<>());
            clazzList.forEach(clazz -> {
                if (clazz.getGradeId().equals(grade.getId())) {
                    Option<Long> child = new Option<>(clazz.getId(), grade.getName() + "/" + clazz.getName());
                    parent.getChildren().add(child);
                }
            });
            optionList.add(parent);
        });
        return optionList;
    }

    @Override
    public List<Option<Long>> listStudentOptions(Long clazzId, Integer year) {
        List<Long> studentIdList = clazzStudentService.getStudentIdListBy(clazzId, year != null ? year : DateUtil.thisYear());
        if (CollectionUtil.isNotEmpty(studentIdList)) {
            List<SysStudent> studentList = studentService.listByIds(studentIdList);
            return studentList.stream().map(it -> new Option<>(it.getId(), it.getName())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Option<String>> getGradeClazzOptions() {
        List<SysGrade> gradeList = gradeService.list();
        List<SysClazz> clazzList = clazzService.list();
        return CommonUtils.getOptions(gradeList, clazzList);
    }

    @Override
    public List<String> getGradeClazzIdListByExamId(Long examId) {
        List<SysExamBody> examBodies = examBodyService.getListByExamId(examId);
        return examBodies.stream().map(it -> it.getGOrC() + "_" + it.getGradeClazzId()).collect(Collectors.toList());
    }

    @Override
    public boolean assignGradeClazzToExam(Long examId, List<String> gradeClazzIdGroupList) {
        examBodyService.removeByExamId(examId);
        gradeClazzIdGroupList.forEach(it -> {
            String[] array = it.split("_");
            SysExamBody examBody = new SysExamBody();
            examBody.setExamId(examId);
            examBody.setGradeClazzId(Long.parseLong(array[1]));
            examBody.setGOrC(array[0]);
            examBodyService.save(examBody);
        });
        return true;
    }
}
