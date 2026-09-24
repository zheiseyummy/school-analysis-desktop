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
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.entity.SysTeacher;
import com.youlai.system.model.entity.SysStudent;
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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final SysArrangeService arrangeService;
    private final SysTeacherService teacherService;
    private final SysStudentService studentService;

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

    @Operation(summary = "年级成绩诊断指标")
    @GetMapping("/gradeInsights")
    public Result<Map<String, Object>> gradeInsights(Long gradeId, Long examId, Double excellentLine, Double passLine) {
        List<SysScore> scores = scoreService.getScoreListByExamIdAndGradeId(examId, gradeId);
        Map<Long, Double> totals = scores.stream().filter(s -> s.getScore() != null).collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(SysScore::getScore)));
        List<Double> values = totals.values().stream().sorted().toList();
        double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0D);
        double maximum = values.stream().mapToDouble(Double::doubleValue).max().orElse(0D);
        double minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0D);
        double median = values.isEmpty() ? 0D : values.get(values.size() / 2);
        double variance = values.isEmpty() ? 0D : values.stream().mapToDouble(v -> Math.pow(v - average, 2)).average().orElse(0D);
        double stdDev = Math.sqrt(variance);
        Map<Long, SysCourse> insightCourses = courseService.listByIds(scores.stream().map(SysScore::getCourseId).filter(Objects::nonNull).distinct().toList()).stream().collect(Collectors.toMap(SysCourse::getId, c -> c));
        double configuredMaximum = insightCourses.values().stream().map(SysCourse::getFullScore).filter(Objects::nonNull).mapToDouble(Integer::doubleValue).sum();
        double fullScore = configuredMaximum > 0D ? configuredMaximum : maximum;
        double excellent = excellentLine == null ? fullScore * .85D : excellentLine;
        double pass = passLine == null ? fullScore * .60D : passLine;
        Map<Long, SysStudent> studentMap = studentService.listByIds(new ArrayList<>(totals.keySet())).stream().collect(Collectors.toMap(SysStudent::getId, s -> s));
        List<Map<String, Object>> studentRows = new ArrayList<>();
        List<Long> sortedIds = totals.entrySet().stream().sorted(Map.Entry.<Long, Double>comparingByValue().reversed()).map(Map.Entry::getKey).toList();
        for (int i = 0; i < sortedIds.size(); i++) {
            Long studentId = sortedIds.get(i); double total = totals.get(studentId); Map<String, Object> row = new LinkedHashMap<>();
            SysStudent student = studentMap.get(studentId); row.put("studentId", studentId); row.put("studentName", student == null ? "" : student.getName()); row.put("studentCode", student == null ? "" : student.getCode()); row.put("totalScore", total); row.put("rank", i + 1); row.put("percentile", sortedIds.size() <= 1 ? 100D : (sortedIds.size() - i - 1) * 100D / (sortedIds.size() - 1)); row.put("distanceToExcellent", total - excellent); row.put("distanceToPass", total - pass); row.put("stabilityScore", stdDev); studentRows.add(row);
        }
        Map<Long, List<SysScore>> classScores = scores.stream().filter(s -> s.getClazzId() != null).collect(Collectors.groupingBy(SysScore::getClazzId));
        List<Map<String, Object>> classRows = new ArrayList<>();
        Map<Long, SysClazz> clazzMap = clazzService.listByIds(new ArrayList<>(classScores.keySet())).stream().collect(Collectors.toMap(SysClazz::getId, c -> c));
        classScores.forEach((clazzId, list) -> { Map<Long, Double> classTotals = list.stream().filter(s -> s.getScore() != null).collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(SysScore::getScore))); double classAvg = classTotals.values().stream().mapToDouble(Double::doubleValue).average().orElse(0D); long excellentCount = classTotals.values().stream().filter(v -> v >= excellent).count(); long passCount = classTotals.values().stream().filter(v -> v >= pass).count(); Map<String, Object> row = new LinkedHashMap<>(); row.put("clazzId", clazzId); row.put("clazzName", clazzMap.get(clazzId) == null ? String.valueOf(clazzId) : clazzMap.get(clazzId).getName()); row.put("studentCount", classTotals.size()); row.put("averageScore", classAvg); row.put("gradeAverageScore", average); row.put("averageDifference", classAvg - average); row.put("excellentRate", classTotals.isEmpty() ? 0D : excellentCount * 1D / classTotals.size()); row.put("passRate", classTotals.isEmpty() ? 0D : passCount * 1D / classTotals.size()); classRows.add(row); });
        Map<String, Object> distribution = new LinkedHashMap<>(); distribution.put("belowPass", totals.values().stream().filter(v -> v < pass).count()); distribution.put("passToExcellent", totals.values().stream().filter(v -> v >= pass && v < excellent).count()); distribution.put("excellent", totals.values().stream().filter(v -> v >= excellent).count());
        Map<String, Object> result = new LinkedHashMap<>(); result.put("studentCount", totals.size()); result.put("average", average); result.put("median", median); result.put("maximum", maximum); result.put("minimum", minimum); result.put("standardDeviation", stdDev); result.put("fullScore", fullScore); result.put("excellentLine", excellent); result.put("passLine", pass); result.put("distribution", distribution); result.put("classRows", classRows); result.put("studentRows", studentRows); result.put("excellentCritical", studentRows.stream().filter(r -> (Double) r.get("totalScore") < excellent && (Double) r.get("totalScore") >= excellent - 10).toList()); result.put("passCritical", studentRows.stream().filter(r -> (Double) r.get("totalScore") < pass && (Double) r.get("totalScore") >= pass - 10).toList());
        return Result.success(result);
    }

    @Operation(summary = "任课教师成绩分析")
    @GetMapping("/teacherAnalysisData")
    public Result<List<Map<String, Object>>> teacherAnalysisData(Long examId, Long gradeId) {
        List<SysScore> scores = scoreService.getScoreListByExamIdAndGradeId(examId, gradeId);
        Map<Long, SysTeacher> teachers = teacherService.list().stream().collect(Collectors.toMap(SysTeacher::getId, it -> it));
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        Map<Long, SysClazz> clazzes = clazzService.list().stream().collect(Collectors.toMap(SysClazz::getId, it -> it));
        Map<String, List<SysScore>> groups = scores.stream().collect(Collectors.groupingBy(s -> s.getTeacherId() + ":" + s.getCourseId() + ":" + s.getClazzId()));
        Map<Long, Double> gradeAvg = scores.stream().collect(Collectors.groupingBy(SysScore::getCourseId, Collectors.averagingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        List<Map<String, Object>> result = new ArrayList<>();
        groups.forEach((key, list) -> {
            SysScore first = list.get(0);
            double avg = list.stream().mapToDouble(s -> s.getScore() == null ? 0D : s.getScore()).average().orElse(0D);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("teacherId", first.getTeacherId()); row.put("teacherName", teachers.containsKey(first.getTeacherId()) ? teachers.get(first.getTeacherId()).getName() : "");
            row.put("courseId", first.getCourseId()); row.put("courseName", courses.containsKey(first.getCourseId()) ? courses.get(first.getCourseId()).getName() : "");
            row.put("clazzId", first.getClazzId()); row.put("clazzName", clazzes.containsKey(first.getClazzId()) ? clazzes.get(first.getClazzId()).getName() : "");
            row.put("averageScore", avg); row.put("gradeAverageScore", gradeAvg.getOrDefault(first.getCourseId(), 0D));
            row.put("averageDifference", avg - gradeAvg.getOrDefault(first.getCourseId(), 0D));
            row.put("maxScore", list.stream().map(SysScore::getScore).filter(java.util.Objects::nonNull).max(Double::compareTo).orElse(0D));
            row.put("minScore", list.stream().map(SysScore::getScore).filter(java.util.Objects::nonNull).min(Double::compareTo).orElse(0D));
            row.put("scoreCount", list.size());
            long excellentCount = list.stream().filter(s -> Integer.valueOf(1).equals(s.getDegree())).count();
            long passCount = list.stream().filter(s -> s.getDegree() != null && s.getDegree() <= 4).count();
            row.put("excellentCount", excellentCount);
            row.put("excellentRate", list.isEmpty() ? 0D : excellentCount * 1D / list.size());
            row.put("passCount", passCount);
            row.put("passRate", list.isEmpty() ? 0D : passCount * 1D / list.size());
            result.add(row);
        });
        return Result.success(result);
    }

    @Operation(summary = "班级学科考试进退步分析")
    @GetMapping("/clazzSubjectProgress")
    public Result<List<Map<String, Object>>> clazzSubjectProgress(Long clazzId, Long gradeId, Long currentExamId, Long previousExamId) {
        List<SysScore> currentClazz = scoreService.getScoreListByExamIdAndClazzId(currentExamId, clazzId);
        List<SysScore> previousClazz = scoreService.getScoreListByExamIdAndClazzId(previousExamId, clazzId);
        List<SysScore> currentGrade = scoreService.getScoreListByExamIdAndGradeId(currentExamId, gradeId);
        List<SysScore> previousGrade = scoreService.getScoreListByExamIdAndGradeId(previousExamId, gradeId);
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        Map<Long, List<SysScore>> cg = currentClazz.stream().collect(Collectors.groupingBy(SysScore::getCourseId));
        Map<Long, List<SysScore>> pg = previousClazz.stream().collect(Collectors.groupingBy(SysScore::getCourseId));
        Map<Long, List<SysScore>> cgr = currentGrade.stream().collect(Collectors.groupingBy(SysScore::getCourseId));
        Map<Long, List<SysScore>> pgr = previousGrade.stream().collect(Collectors.groupingBy(SysScore::getCourseId));
        List<Map<String, Object>> result = new ArrayList<>();
        courses.forEach((courseId, course) -> {
            double ca = average(cg.get(courseId)), pa = average(pg.get(courseId));
            double cga = average(cgr.get(courseId)), pga = average(pgr.get(courseId));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseId", courseId); row.put("courseName", course.getName());
            row.put("currentAverage", ca); row.put("previousAverage", pa); row.put("averageChange", ca - pa);
            row.put("gradeAverageChange", cga - pga); row.put("relativeChange", (ca - pa) - (cga - pga));
            row.put("currentExcellentRate", rate(cg.get(courseId), 1)); row.put("previousExcellentRate", rate(pg.get(courseId), 1));
            row.put("currentPassRate", passRate(cg.get(courseId))); row.put("previousPassRate", passRate(pg.get(courseId)));
            result.add(row);
        });
        return Result.success(result);
    }

    @Operation(summary = "班级学生进退步榜")
    @GetMapping("/studentProgressRanking")
    public Result<List<Map<String, Object>>> studentProgressRanking(Long clazzId, Long currentExamId, Long previousExamId) {
        List<SysScore> current = scoreService.getScoreListByExamIdAndClazzId(currentExamId, clazzId);
        List<SysScore> previous = scoreService.getScoreListByExamIdAndClazzId(previousExamId, clazzId);
        Map<Long, Double> currentMap = current.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, Double> previousMap = previous.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, Integer> currentRanks = rankMap(currentMap);
        Map<Long, Integer> previousRanks = rankMap(previousMap);
        Map<Long, String> names = new HashMap<>();
        studentService.listByIds(new ArrayList<>(currentMap.keySet())).forEach(s -> names.put(s.getId(), s.getName()));
        List<Map<String, Object>> result = new ArrayList<>();
        currentMap.forEach((studentId, score) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            double old = previousMap.getOrDefault(studentId, 0D);
            row.put("studentId", studentId); row.put("studentName", names.getOrDefault(studentId, ""));
            row.put("currentScore", score); row.put("previousScore", old); row.put("scoreChange", score - old);
            row.put("currentRank", currentRanks.get(studentId)); row.put("previousRank", previousRanks.get(studentId));
            row.put("rankChange", previousRanks.getOrDefault(studentId, currentRanks.get(studentId)) - currentRanks.get(studentId));
            result.add(row);
        });
        result.sort((a, b) -> Double.compare((Double) b.get("scoreChange"), (Double) a.get("scoreChange")));
        return Result.success(result);
    }

    private Map<Long, Integer> rankMap(Map<Long, Double> scores) {
        List<Long> ids = scores.entrySet().stream().sorted(Map.Entry.<Long, Double>comparingByValue().reversed()).map(Map.Entry::getKey).toList();
        Map<Long, Integer> ranks = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) ranks.put(ids.get(i), i + 1);
        return ranks;
    }

    @Operation(summary = "班级学科落后预警")
    @GetMapping("/clazzSubjectWarnings")
    public Result<List<Map<String, Object>>> clazzSubjectWarnings(Long clazzId, Long gradeId, Long examId, Double threshold) {
        double limit = threshold == null ? 5D : threshold;
        List<SysScore> clazzScores = scoreService.getScoreListByExamIdAndClazzId(examId, clazzId);
        List<SysScore> gradeScores = scoreService.getScoreListByExamIdAndGradeId(examId, gradeId);
        Map<Long, Double> clazzAvg = clazzScores.stream().collect(Collectors.groupingBy(SysScore::getCourseId, Collectors.averagingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, Double> gradeAvg = gradeScores.stream().collect(Collectors.groupingBy(SysScore::getCourseId, Collectors.averagingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        List<Map<String, Object>> result = new ArrayList<>();
        clazzAvg.forEach((courseId, avg) -> {
            double gap = avg - gradeAvg.getOrDefault(courseId, 0D);
            if (gap <= -limit) {
                Map<String, Object> row = new LinkedHashMap<>(); row.put("courseId", courseId); row.put("courseName", courses.get(courseId) == null ? "" : courses.get(courseId).getName()); row.put("clazzAverage", avg); row.put("gradeAverage", gradeAvg.getOrDefault(courseId, 0D)); row.put("difference", gap); result.add(row);
            }
        });
        result.sort((a, b) -> Double.compare((Double) a.get("difference"), (Double) b.get("difference")));
        return Result.success(result);
    }

    @Operation(summary = "班级学生偏科分析")
    @GetMapping("/clazzSubjectBalance")
    public Result<List<Map<String, Object>>> clazzSubjectBalance(Long clazzId, Long examId) {
        List<SysScore> scores = scoreService.getScoreListByExamIdAndClazzId(examId, clazzId);
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        Map<Long, Map<Long, Double>> studentCourse = new HashMap<>();
        scores.forEach(s -> {
            if (s.getScore() != null) studentCourse.computeIfAbsent(s.getStudentId(), k -> new HashMap<>()).put(s.getCourseId(), s.getScore());
        });
        Map<Long, String> names = new HashMap<>();
        studentService.listByIds(new ArrayList<>(studentCourse.keySet())).forEach(s -> names.put(s.getId(), s.getName()));
        List<Map<String, Object>> result = new ArrayList<>();
        studentCourse.forEach((studentId, values) -> {
            if (values.isEmpty()) return;
            Map.Entry<Long, Double> best = values.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            Map.Entry<Long, Double> weak = values.entrySet().stream().min(Map.Entry.comparingByValue()).orElse(null);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("studentId", studentId); row.put("studentName", names.getOrDefault(studentId, ""));
            row.put("strongestCourse", best == null || courses.get(best.getKey()) == null ? "" : courses.get(best.getKey()).getName());
            row.put("weakestCourse", weak == null || courses.get(weak.getKey()) == null ? "" : courses.get(weak.getKey()).getName());
            row.put("strongestScore", best == null ? 0D : best.getValue()); row.put("weakestScore", weak == null ? 0D : weak.getValue());
            row.put("scoreGap", best == null || weak == null ? 0D : best.getValue() - weak.getValue());
            result.add(row);
        });
        result.sort((a, b) -> Double.compare((Double) b.get("scoreGap"), (Double) a.get("scoreGap")));
        return Result.success(result);
    }

    @Operation(summary = "年级学生进退分层")
    @GetMapping("/progressBands")
    public Result<Map<String, List<Map<String, Object>>>> progressBands(Long gradeId, Long currentExamId, Long previousExamId) {
        List<SysScore> current = scoreService.getScoreListByExamIdAndGradeId(currentExamId, gradeId);
        List<SysScore> previous = scoreService.getScoreListByExamIdAndGradeId(previousExamId, gradeId);
        Map<Long, Double> now = current.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, Double> old = previous.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, String> names = new HashMap<>(); studentService.listByIds(new ArrayList<>(now.keySet())).forEach(s -> names.put(s.getId(), s.getName()));
        List<Map<String, Object>> changes = new ArrayList<>();
        now.forEach((id, value) -> { Map<String, Object> row = new LinkedHashMap<>(); row.put("studentId", id); row.put("studentName", names.getOrDefault(id, "")); row.put("currentScore", value); row.put("previousScore", old.getOrDefault(id, 0D)); row.put("change", value - old.getOrDefault(id, 0D)); changes.add(row); });
        changes.sort((a, b) -> Double.compare((Double) b.get("change"), (Double) a.get("change")));
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        List<Map<String, Object>> gains = changes.stream().filter(x -> (Double) x.get("change") > 0).toList();
        List<Map<String, Object>> losses = changes.stream().filter(x -> (Double) x.get("change") < 0).sorted((a, b) -> Double.compare((Double) a.get("change"), (Double) b.get("change"))).toList();
        int band = Math.max(1, changes.size() / 5);
        for (int i = 0; i < 5; i++) {
            int from = Math.min(i * band, gains.size()), to = Math.min((i + 1) * band, gains.size());
            result.put("W" + (i + 1), gains.subList(from, to));
            from = Math.min(i * band, losses.size()); to = Math.min((i + 1) * band, losses.size());
            result.put("L" + (i + 1), losses.subList(from, to));
        }
        return Result.success(result);
    }

    @Operation(summary = "学生偏科排名差分析")
    @GetMapping("/studentBiasAnalysis")
    public Result<List<Map<String, Object>>> studentBiasAnalysis(Long gradeId, Long examId) {
        List<SysScore> scores = scoreService.getScoreListByExamIdAndGradeId(examId, gradeId);
        Map<Long, Double> totals = scores.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())));
        Map<Long, Integer> totalRanks = rankMap(totals);
        Map<Long, Map<Long, Double>> subjects = scores.stream().filter(s -> s.getScore() != null).collect(Collectors.groupingBy(SysScore::getCourseId, Collectors.toMap(SysScore::getStudentId, SysScore::getScore, (a, b) -> a)));
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        Map<Long, String> names = new HashMap<>(); studentService.listByIds(new ArrayList<>(totals.keySet())).forEach(s -> names.put(s.getId(), s.getName()));
        List<Map<String, Object>> result = new ArrayList<>();
        totals.forEach((studentId, total) -> {
            Map<String, Object> row = new LinkedHashMap<>(); row.put("studentId", studentId); row.put("studentName", names.getOrDefault(studentId, "")); row.put("totalScore", total); row.put("totalRank", totalRanks.get(studentId));
            List<Map<String, Object>> courseRanks = new ArrayList<>();
            subjects.forEach((courseId, values) -> { if (values.containsKey(studentId)) { Map<Long, Integer> rank = rankMap(values); Map<String, Object> item = new LinkedHashMap<>(); item.put("courseId", courseId); item.put("courseName", courses.get(courseId) == null ? "" : courses.get(courseId).getName()); item.put("score", values.get(studentId)); item.put("rank", rank.get(studentId)); item.put("rankDifference", rank.get(studentId) - totalRanks.get(studentId)); courseRanks.add(item); } });
            row.put("courseRanks", courseRanks); result.add(row);
        });
        return Result.success(result);
    }

    @Operation(summary = "学生多次考试综合成绩导出")
    @GetMapping("/studentHistoryToExcel")
    public void studentHistoryToExcel(Long gradeId, String examIds, String direction, HttpServletResponse response) throws IOException {
        List<Long> ids = Arrays.stream(examIds.split(",")).filter(s -> !s.isBlank()).map(Long::valueOf).toList();
        List<SysScore> scores = ids.stream().flatMap(id -> scoreService.getScoreListByExamIdAndGradeId(id, gradeId).stream()).toList();
        Map<Long, SysStudent> students = studentService.listByIds(scores.stream().map(SysScore::getStudentId).distinct().toList()).stream().collect(Collectors.toMap(SysStudent::getId, it -> it));
        Map<Long, SysExam> exams = examService.listByIds(ids).stream().collect(Collectors.toMap(SysExam::getId, it -> it));
        Map<Long, Map<Long, Double>> totals = scores.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.groupingBy(SysScore::getExamId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore()))));
        Map<Long, Map<Long, Map<Long, Double>>> subjectTotals = scores.stream().collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.groupingBy(SysScore::getExamId, Collectors.groupingBy(SysScore::getCourseId, Collectors.summingDouble(s -> s.getScore() == null ? 0D : s.getScore())))));
        Map<Long, SysCourse> courseMap = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        totals.forEach((studentId, values) -> { Map<String, Object> row = new LinkedHashMap<>(); SysStudent student = students.get(studentId); row.put("方向", direction == null ? "全科" : direction); row.put("学号", student == null ? "" : student.getCode()); row.put("姓名", student == null ? "" : student.getName()); row.put("学生ID", studentId); ids.forEach(id -> { String examName = exams.get(id) == null ? String.valueOf(id) : exams.get(id).getName(); row.put(examName + "-总分", values.getOrDefault(id, 0D)); Map<Long, Double> coursesForExam = subjectTotals.getOrDefault(studentId, Map.of()).getOrDefault(id, Map.of()); coursesForExam.forEach((courseId, score) -> row.put(examName + "-" + (courseMap.get(courseId) == null ? courseId : courseMap.get(courseId).getName()), score)); }); rows.add(row); });
        ExcelWriter writer = ExcelUtil.getWriter(true); writer.write(rows, true); response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"); String suffix = "物理".equals(direction) ? "物理方向" : "历史".equals(direction) ? "历史方向" : "全科"; response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("学生多次考试综合成绩-" + suffix + ".xlsx", "UTF-8")); writer.flush(response.getOutputStream(), true); writer.close();
    }

    private double average(List<SysScore> scores) {
        return scores == null ? 0D : scores.stream().map(SysScore::getScore).filter(java.util.Objects::nonNull).mapToDouble(Double::doubleValue).average().orElse(0D);
    }

    private double rate(List<SysScore> scores, int degree) {
        return scores == null || scores.isEmpty() ? 0D : scores.stream().filter(s -> Integer.valueOf(degree).equals(s.getDegree())).count() * 1D / scores.size();
    }

    private double passRate(List<SysScore> scores) {
        return scores == null || scores.isEmpty() ? 0D : scores.stream().filter(s -> s.getDegree() != null && s.getDegree() <= 4).count() * 1D / scores.size();
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
