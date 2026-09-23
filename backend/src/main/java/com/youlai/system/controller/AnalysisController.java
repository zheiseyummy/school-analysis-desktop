package com.youlai.system.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.youlai.system.common.exception.BusinessException;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.views.ExamPointScorePDFView;
import com.youlai.system.model.bo.ClazzStaticsBO;
import com.youlai.system.model.bo.CourseClazzStaticsBO;
import com.youlai.system.model.bo.CourseStaticsBO;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.query.ClazzExamAnalysisQuery;
import com.youlai.system.model.query.StudentScoreAnalysisQuery;
import com.youlai.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试分析控制器
 */
@Tag(name = "18.考试分析接口")
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final BusinessService businessService;

    private final SysExamBodyService examBodyService;

    private final SysScoreService scoreService;

    private final SysCourseService courseService;

    private final SysClazzService clazzService;

    private final SysClazzStudentService clazzStudentService;

    private final SysExamService examService;

    @Operation(summary = "班级考试维度数据列表")
    @GetMapping("/clazzExamAnalysisData")
    public Result<Map<String, Object>> clazzExamAnalysisData(ClazzExamAnalysisQuery query) {
        SysExamBody examBody = examBodyService.getIdByExamIdAndClazzId(query.getExamId(), query.getClazzId());
        if (examBody == null) {
            return Result.failed("没有找到相关数据");
        }
        Map<String, Object> resultMap = businessService.clazzExamAllCourseScoreSummaryData(examBody.getId(), query.getCourseId());

        List<CourseStaticsBO> courseStaticsBOList = scoreService.getCourseStaticsList(query.getExamId(), query.getClazzId());
        return getMapResult(resultMap, courseStaticsBOList);
    }

    @Operation(summary = "班级某次考试成绩打印")
    @RequestMapping("/clazzExamAnalysisDataToPdf")
    public ModelAndView clazzExamAnalysisDataToPdf(ClazzExamAnalysisQuery query) {
        SysExamBody examBody = examBodyService.getIdByExamIdAndClazzId(query.getExamId(), query.getClazzId());
        if (examBody == null) {
           throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Map<String, Object> resultMap = businessService.clazzExamAllCourseScoreSummaryExportData(examBody.getId());
        ExamPointScorePDFView examPointScorePDFView = new ExamPointScorePDFView();
        return new ModelAndView(examPointScorePDFView, resultMap);
    }

    @Operation(summary = "班级某次考试成绩导出")
    @RequestMapping("/clazzExamAnalysisDataToExcel")
    public void clazzExamAnalysisDataToExcel(ClazzExamAnalysisQuery query, HttpServletResponse response) throws IOException {
        SysExamBody examBody = examBodyService.getIdByExamIdAndClazzId(query.getExamId(), query.getClazzId());
        if (examBody == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Map<String, Object> resultMap = businessService.clazzExamAllCourseScoreSummaryExportData(examBody.getId());
        String[][] result = (String[][]) resultMap.get("result");
        String title = (String) resultMap.get("title");
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 1; i < result.length; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int j = 0; j < result[i].length; j++) {
                row.put(result[0][j], result[i][j]);
            }
            rows.add(row);
        }
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.passCurrentRow();
        writer.merge(result[0].length - 1, title);
        writer.write(rows, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8") + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }


    @Operation(summary = "年级考试维度数据列表")
    @GetMapping("/gradeExamAnalysisData")
    public Result<Map<String, Object>> gradeExamAnalysisData(ClazzExamAnalysisQuery query) {
        Map<String, Object> resultMap = businessService.gradeExamAllCourseScoreSummaryData(query.getGradeId(), query.getExamId());
        List<CourseStaticsBO> courseStaticsBOList = scoreService.getGradeCourseStaticsList(query.getExamId(), query.getGradeId());
        List<ClazzStaticsBO> clazzStaticsBOList = scoreService.getGradeClazzStaticsList(query.getExamId(), query.getGradeId());
        List<SysClazz> clazzList = clazzService.list();
        Map<Long, SysClazz> clazzMap = clazzList.stream().collect(Collectors.toMap(SysClazz::getId, it -> it));
        SysExam exam = examService.getById(query.getExamId());
        clazzStaticsBOList.forEach(clazzStaticsBO -> {
            SysClazz clazz = clazzMap.get(clazzStaticsBO.getClazzId());
            if (clazz != null) {
                clazzStaticsBO.setClazzName(clazz.getName());
                clazzStaticsBO.setClazzStudentCount(clazzStudentService.studentCountBy(clazz.getId(), exam.getYear()));
            }
        });

        resultMap.put("clazzStaticsList", clazzStaticsBOList);

        List<CourseClazzStaticsBO> courseClazzStaticsBOList = scoreService.getGradeCourseClazzStaticsList(query.getExamId(), query.getGradeId());
        List<SysCourse> courseList = courseService.list();
        Map<Long, SysCourse> courseMap = courseList.stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        courseClazzStaticsBOList.forEach(courseClazzStaticsBO -> {
            SysClazz clazz = clazzMap.get(courseClazzStaticsBO.getClazzId());
            if (clazz != null) {
                courseClazzStaticsBO.setClazzName(clazz.getName());
                courseClazzStaticsBO.setClazzStudentCount(clazzStudentService.studentCountBy(clazz.getId(), exam.getYear()));
            }

            SysCourse course = courseMap.get(courseClazzStaticsBO.getCourseId());
            if (course != null) {
                courseClazzStaticsBO.setCourseName(course.getName());
                courseClazzStaticsBO.setFullScore(course.getFullScore());
            }
        });



        resultMap.put("courseClazzStaticsList", courseClazzStaticsBOList);
        return getMapResult(resultMap, courseStaticsBOList);
    }


    @Operation(summary = "个人成绩分析数据列表")
    @GetMapping("/studentScoreAnalysisData")
    public Result<Map<String, Object>> studentScoreAnalysisData(StudentScoreAnalysisQuery query) {
        Map<String, Object> resultMap = businessService.studentAllScoreSummaryData(query.getStudentId());
        return Result.success(resultMap);
    }

    @Operation(summary = "个人单个课程成绩分析数据列表")
    @GetMapping("/studentSingleScoreAnalysisData")
    public Result<Map<String, Object>> studentSingleCourseAnalysisData(StudentScoreAnalysisQuery query) {
        Map<String, Object> resultMap = businessService.studentSingleCourseAnalysisData(query.getStudentId(), query.getCourseId());
        return Result.success(resultMap);
    }

    @NotNull
    private Result<Map<String, Object>> getMapResult(Map<String, Object> resultMap, List<CourseStaticsBO> courseStaticsBOList) {
        List<SysCourse> courseList = courseService.list();
        Map<Long, SysCourse> courseMap = courseList.stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        courseStaticsBOList.forEach(courseStaticsBO -> {
            SysCourse course = courseMap.get(courseStaticsBO.getCourseId());
            if (course != null) {
                courseStaticsBO.setCourseName(course.getName());
                courseStaticsBO.setFullScore(course.getFullScore());
            }
        });
        resultMap.put("courseStaticsList", courseStaticsBOList);
        return Result.success(resultMap);
    }
}
