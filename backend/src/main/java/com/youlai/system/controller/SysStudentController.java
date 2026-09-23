package com.youlai.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.DateUtils;
import com.youlai.system.common.util.ExcelUtils;
import com.youlai.system.converter.StudentConverter;
import com.youlai.system.model.bo.ClazzYearBO;
import com.youlai.system.model.bo.StudentClazzBO;
import com.youlai.system.model.entity.*;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.query.StudentPageQuery;
import com.youlai.system.model.vo.*;
import com.youlai.system.plugin.easyexcel.StudentImportListener;
import com.youlai.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生控制器
 */
@Tag(name = "12.学生接口")
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class SysStudentController {

    private final SysStudentService studentService;


    private final SysClazzStudentService clazzStudentService;

    private final SchoolService schoolService;


    private final StudentConverter studentConverter;

    private final SysClazzService clazzService;

    private final SysGradeService gradeService;

    private final SysScoreService scoreService;




    @Operation(summary = "学生分页列表")
    @GetMapping("/page")
    public PageResult<StudentPageVO> getStudentPage(
            StudentPageQuery queryParams
    ) {

        if (queryParams.getClazzId() != null) {
            List<Long> studentIdList = clazzStudentService.getStudentIdListBy(queryParams.getClazzId(), DateUtil.thisYear());
            queryParams.setStudentIdList(studentIdList);
        }

        IPage<StudentPageVO> result = studentService.getStudentPage(queryParams);
        List<Long> studentIdList = new ArrayList<>();
        result.getRecords().forEach(it -> {
            studentIdList.add(it.getId());
            if (it.getBirthDay() != null) {
                it.setAge(DateUtils.getChineseAge(it.getBirthDay()));
            }
        });

        if (CollectionUtil.isNotEmpty(studentIdList)) {
            Map<Long, StudentClazzBO> studentClazzBOMap = clazzStudentService.getStudentClazzCountNameMap(studentIdList);
            result.getRecords().forEach(it -> {
                StudentClazzBO studentClazzBO = studentClazzBOMap.get(it.getId());
                if (studentClazzBO != null) {
                    it.setClazzCount(studentClazzBO.getClazzCount());
                    it.setClazzNameList(studentClazzBO.getClazzNameList());
                }

            });
        }

        return PageResult.success(result);
    }

    @Operation(summary = "新增学生")
    @PostMapping
    @Transactional
    public Result saveStudent(
            @RequestBody @Valid StudentForm studentForm
    ) {
        boolean result = studentService.saveStudent(studentForm);
        return saveStudentExtraInfo(studentForm, result);
    }

    @Operation(summary = "学生表单数据")
    @GetMapping("/{studentId}/form")
    public Result<StudentForm> getStudentForm(
            @Parameter(description = "学生ID") @PathVariable Long studentId
    ) {
        StudentForm formData = studentService.getStudentForm(studentId);
        List<ClazzYearBO> clazzYearBOList = clazzStudentService.getByStudentId(studentId);
        Assert.notNull(formData, "学生不存在");
        formData.setClazzList(clazzYearBOList == null ? List.of() : clazzYearBOList);


        return Result.success(formData);
    }


    @Operation(summary = "修改学生")
    @PutMapping(value = "/{studentId}")
    @Transactional
    public Result updateStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestBody @Validated StudentForm studentForm) {
        boolean result = studentService.updateStudent(studentId, studentForm);
        return saveStudentExtraInfo(studentForm, result);
    }

    @NotNull
    private Result saveStudentExtraInfo(@Validated @RequestBody StudentForm studentForm, boolean result) {
        if (!result || studentForm.getClazzList() == null) {
            return Result.judge(result);
        }
        studentForm.getClazzList().forEach(item -> {
            Assert.notNull(item, "班级归属不能为空");
            Assert.notNull(item.getClazzId(), "请选择班级");
            Assert.notNull(item.getYear(), "请填写班级归属年度");
        });
        clazzStudentService.deleteByStudentId(studentForm.getId());
        studentForm.getClazzList().forEach(item ->
                clazzStudentService.saveOrUpdateClazzStudent(studentForm.getId(), item.getClazzId(), item.getYear()));
        return Result.judge(result);
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{ids}")
    @Transactional
    public Result deleteStudents(
            @Parameter(description = "学生ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "学生删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = studentService.deleteStudents(idList);
        //删除学生的班级关联信息
        idList.forEach(clazzStudentService::removeByStudentId);
        //删除学生的成绩关联信息
        idList.forEach(scoreService::removeByStudentId);
        return Result.judge(result);
    }

    @Operation(summary = "学生下拉列表")
    @GetMapping("/{clazzId}/options")
    public Result<List<Option<Long>>> listStudentOptions(@Parameter(description = "学生ID") @PathVariable Long clazzId,
                                                         @RequestParam(value = "year", required = false) Integer year) {
        List<Option<Long>> list = schoolService.listStudentOptions(clazzId, year);
        return Result.success(list);
    }


    @Operation(summary = "学生导入模板下载")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String fileName = "学生导入模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));


        ServletOutputStream outputStream = response.getOutputStream();
        EasyExcel.write(outputStream, StudentImportVO.class).sheet("学生导入模板").doWrite(List.of());
    }

    @Operation(summary = "导入学生")
    @PostMapping("/_import")
    public Result importStudents(MultipartFile file) throws IOException {
        StudentImportListener listener = new StudentImportListener();
        String msg = ExcelUtils.importExcel(file.getInputStream(), StudentImportVO.class, listener);
        return Result.success(msg);
    }

    @Operation(summary = "导出学生")
    @GetMapping("/_export")
    public void exportStudents(StudentPageQuery queryParams, HttpServletResponse response) throws IOException {
        String fileName = "学生列表.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        if (queryParams.getClazzId() != null) {
            List<Long> studentIdList = clazzStudentService.getStudentIdListBy(queryParams.getClazzId(), DateUtil.thisYear());
            queryParams.setStudentIdList(studentIdList);
        }
        List<SysStudent> studentList = studentService.getStudentExport(queryParams);
        List<StudentExportVO> studentExportVOList = new ArrayList<>();
        studentList.forEach(sysStudent -> {
            StudentExportVO studentExportVO = studentConverter.entity2Export(sysStudent);
            SysClazzStudent clazzStudent = clazzStudentService.getLastOneBy(sysStudent.getId());
            if (clazzStudent != null) {
                studentExportVO.setClazzYear(clazzStudent.getYear());
                SysClazz clazz = clazzService.getById(clazzStudent.getClazzId());
                if (clazz != null) {
                    studentExportVO.setClazzName(clazz.getName());
                    SysGrade grade = gradeService.getById(clazz.getGradeId());
                    if (grade != null) {
                        studentExportVO.setGradeName(grade.getName());
                    }
                }
            }

            studentExportVOList.add(studentExportVO);
        });
        EasyExcel.write(response.getOutputStream(), StudentExportVO.class).sheet("学生列表")
                .doWrite(studentExportVOList);
    }

}
