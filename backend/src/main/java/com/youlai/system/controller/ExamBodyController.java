package com.youlai.system.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.constant.SystemConstants;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.ExcelUtils;
import com.youlai.system.model.bo.ClazzCourseBO;
import com.youlai.system.model.bo.CourseScoreCountBO;
import com.youlai.system.model.form.ScoreEntryForm;
import com.youlai.system.model.query.ExamBodyPageQuery;
import com.youlai.system.model.query.ScoreEntryQuery;
import com.youlai.system.model.vo.ExamBodyPageVO;
import com.youlai.system.model.vo.ScoreEntryVO;
import com.youlai.system.model.vo.ScoreImportVO;
import com.youlai.system.plugin.easyexcel.ScoreImportListener;
import com.youlai.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 考试主体控制器
 */
@Tag(name = "17.考试主体接口")
@RestController
@RequestMapping("/api/v1/exam_body_s")
@RequiredArgsConstructor
public class ExamBodyController {

    private final SysExamBodyService examBodyService;

    private final SysDictService dictService;

    private final SysArrangeService arrangeService;

    private final SysClazzStudentService clazzStudentService;

    private final BusinessService businessService;

    private final SysScoreService scoreService;

    @Operation(summary = "考试主体分页列表")
    @GetMapping("/page")
    public PageResult<ExamBodyPageVO> getArrangePage(
            ExamBodyPageQuery queryParams
    ) {
        Page<ExamBodyPageVO> result = examBodyService.getExamBodyPage(queryParams);

        Map<String, String> typeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_EXAM_TYPE);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);

        result.getRecords().forEach(it -> {
            String type = typeMap.get(it.getExamType());
            it.setExamTypeStr(type);

            String semesterStr = semesterMap.get(it.getSemester().toString());
            it.setSemesterStr(semesterStr);

            List<ClazzCourseBO> courseList = arrangeService.getClazzCourseBOList(it.getClazzId());

            //考试ID，班级ID，统计课程有多少学生成绩已录入
            Map<Long, CourseScoreCountBO> courseScoreCountBOMap = scoreService.getCourseScoreCountMap(it.getExamId(), it.getClazzId());
            courseList.forEach(clazzCourseBO -> {
                CourseScoreCountBO courseScoreCountBO = courseScoreCountBOMap.get(clazzCourseBO.getCourseId());
                if (courseScoreCountBO != null) {
                    clazzCourseBO.setScoreCount(courseScoreCountBO.getScoreCount());
                } else {
                    clazzCourseBO.setScoreCount(0L);
                }
            });

            it.setCourseList(courseList);

            Long studentCount = clazzStudentService.studentCountBy(it.getClazzId(),it.getYear());
            it.setStudentCount(studentCount);

        });
        return PageResult.success(result);
    }

    @Operation(summary = "成绩录入列表")
    @GetMapping("/scoreEntryList")
    public Result<List<ScoreEntryVO>> getScoreEntryList(ScoreEntryQuery queryParams) {
        List<ScoreEntryVO> scoreEntryVOList = businessService.getScoreEntryList(queryParams);
        return Result.success(scoreEntryVOList);
    }

    @Operation(summary = "保存成绩")
    @PostMapping("/saveScore")
    public Result saveScore(
            @RequestBody @Valid ScoreEntryForm scoreEntryForm
    ) {
        businessService.saveScore(scoreEntryForm);
        return Result.judge(true);
    }

    @Operation(summary = "考试班级所有课程成绩汇总数据")
    @GetMapping("/{examBodyId}/getExamBodyScorePreview")
    public Result<Map<String,Object>> getExamBodyScorePreview(@Parameter(description = "考试主体ID") @PathVariable Long examBodyId) {
        Map<String, Object> resultMap = businessService.clazzExamAllCourseScoreSummaryData(examBodyId, null);
        return Result.success(resultMap);
    }


    @Operation(summary = "成绩导入模板下载")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String fileName = "成绩导入模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        String fileClassPath = "excel-templates" + File.separator + fileName;
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileClassPath);

        ServletOutputStream outputStream = response.getOutputStream();
        ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).build();

        excelWriter.finish();
    }

    @Operation(summary = "导入成绩")
    @PostMapping("/_import")
    public Result importScores(@Parameter(description = "考试ID") Long examId, MultipartFile file) throws IOException {
        ScoreImportListener listener = new ScoreImportListener(examId);
        String msg = ExcelUtils.importExcel(file.getInputStream(), ScoreImportVO.class, listener);
        return Result.success(msg);
    }

}
