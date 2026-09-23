package com.youlai.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.constant.SystemConstants;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.form.ExamForm;
import com.youlai.system.model.query.ClazzExamAnalysisQuery;
import com.youlai.system.model.query.ExamPageQuery;
import com.youlai.system.model.vo.ExamPageVO;
import com.youlai.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试控制器
 */
@Tag(name = "16.考试接口")
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class SysExamController {

    private final SysExamService examService;

    private final SysDictService dictService;

    private final SchoolService schoolService;

    private final SysExamBodyService examBodyService;

    private final SysGradeService gradeService;

    private final SysClazzService clazzService;


    @Operation(summary = "考试分页列表")
    @GetMapping("/page")
    public PageResult<ExamPageVO> getExamPage(ExamPageQuery queryParams) {
        IPage<ExamPageVO> result = examService.getExamPage(queryParams);
        Map<String, String> typeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_EXAM_TYPE);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);


        result.getRecords().forEach(it -> {
            String type = typeMap.get(it.getExamType());
            it.setExamTypeStr(type);

            String semesterStr = semesterMap.get(it.getSemester().toString());
            it.setSemesterStr(semesterStr);

            List<Long> gradeIdList = examBodyService.getGradeIdListByExamId(it.getId());
            List<Long> clazzIdList = examBodyService.getClazzIdListByExamId(it.getId());
            StringBuilder sb = new StringBuilder();
            if (CollectionUtil.isNotEmpty(gradeIdList)) {
                sb.append("年级：").append(gradeIdList.size());
            }
            if (CollectionUtil.isNotEmpty(gradeIdList)) {
                sb.append("，");
            }
            if (CollectionUtil.isNotEmpty(clazzIdList)) {
                sb.append("班级：").append(clazzIdList.size());
            }
            it.setGradeClazzCount(sb.toString());

            if (CollectionUtil.isNotEmpty(gradeIdList)) {
                List<SysGrade> gradeList = gradeService.listByIds(gradeIdList);
                List<SysClazz> clazzList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(clazzIdList)) {
                    clazzList = clazzService.listByIds(clazzIdList);
                }
                StringBuilder cp = new StringBuilder();
                List<SysClazz> finalProjectList = clazzList;
                gradeList.forEach(grade -> {
                    cp.append(grade.getName()).append("<br/>");
                    cp.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(StrUtil.join("，", finalProjectList.stream().filter(pro -> pro.getGradeId().equals(grade.getId())).map(SysClazz::getName).collect(Collectors.toList())));
                    cp.append("<br/>");
                });
                it.setGradeClazzList(cp.toString());
            }
        });
        return PageResult.success(result);
    }

    @Operation(summary = "新增考试")
    @PostMapping
    public Result saveExam(@RequestBody @Valid ExamForm examForm) {
        boolean result = examService.saveExam(examForm);
        return Result.judge(result);
    }

    @Operation(summary = "考试表单数据")
    @GetMapping("/{examId}/form")
    public Result<ExamForm> getExamForm(@Parameter(description = "考试ID") @PathVariable Long examId) {
        ExamForm formData = examService.getExamForm(examId);
        return Result.success(formData);
    }


    @Operation(summary = "修改考试")
    @PutMapping(value = "/{examId}")
    public Result updateExam(@Parameter(description = "考试ID") @PathVariable Long examId, @RequestBody @Validated ExamForm examForm) {
        boolean result = examService.updateExam(examId, examForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除考试")
    @DeleteMapping("/{ids}")
    public Result deleteExams(@Parameter(description = "考试ID，多个以英文逗号(,)分割") @PathVariable String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "考试删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = examService.deleteExams(idList);
        return Result.judge(result);
    }

    @Operation(summary = "考试下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listDefaultExamOptions(@RequestParam(required = false) Long clazzId) {
        List<Option> list = null;
        if (clazzId != null) {
            List<Long> examIdList = examBodyService.getExamIdListByClazzId(clazzId);
            if (CollectionUtil.isNotEmpty(examIdList)) {
                list = examService.listExamOptions(examIdList);
            } else {
                return Result.success(new ArrayList<>());
            }
        } else {
            list = examService.listExamOptions();
        }

        return Result.success(list);
    }

    @Operation(summary = "考试下拉列表")
    @GetMapping("/options/exam")
    public Result<List<Option>> listExamOptions(ClazzExamAnalysisQuery query) {
        if (query.getClazzId() != null) {
            query.setExamIdList(examBodyService.getExamIdListByClazzId(query.getClazzId()));
        }

        if (query.getGradeId() != null) {
            List<Long> clazzIdList = clazzService.clazzIdListByGradeId(query.getGradeId());
            query.setExamIdList(examBodyService.getExamIdListByClazzIdList(clazzIdList));
        }

        List<Option> list = examService.listExamOptions(query);
        return Result.success(list);
    }

    @Operation(summary = "考试下年级班级树形结构列表")
    @GetMapping("/gradeClazz/options")
    public Result<List<Option<String>>> listGradeClazzOptions() {
        List<Option<String>> list = schoolService.getGradeClazzOptions();
        return Result.success(list);
    }

    @Operation(summary = "获取考试的年级或者班级ID集合")
    @GetMapping("/{examId}/gradeClazzIds")
    public Result<List<String>> getExamGradeClazzIds(@Parameter(description = "考试ID") @PathVariable Long examId) {
        List<String> gradeClazzIds = schoolService.getGradeClazzIdListByExamId(examId);
        return Result.success(gradeClazzIds);
    }

    @Operation(summary = "分配年级(包括班级)给考试")
    @PutMapping("/{examId}/gradeClazzIds")
    public Result assignGradeClazzToExam(@PathVariable Long examId, @RequestBody List<String> gradeClazzIds) {
        boolean result = schoolService.assignGradeClazzToExam(examId, gradeClazzIds);
        return Result.judge(result);
    }
}
