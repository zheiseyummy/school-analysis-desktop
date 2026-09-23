package com.youlai.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.DateUtils;
import com.youlai.system.common.util.ExcelUtils;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.query.TeacherPageQuery;
import com.youlai.system.model.vo.*;
import com.youlai.system.plugin.easyexcel.TeacherImportListener;
import com.youlai.system.service.SysTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 职工控制器
 */
@Tag(name = "10.职工接口")
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class SysTeacherController {

    private final SysTeacherService teacherService;



    @Operation(summary = "职工分页列表")
    @GetMapping("/page")
    public PageResult<TeacherPageVO> getTeacherPage(
            TeacherPageQuery queryParams
    ) {
        IPage<TeacherPageVO> result = teacherService.getTeacherPage(queryParams);
        result.getRecords().forEach(it -> {

            if (it.getBirthDay() != null) {
                it.setAge(DateUtils.getChineseAge(it.getBirthDay(), true, false, false));
            }
        });
        return PageResult.success(result);
    }

    @Operation(summary = "新增职工")
    @PostMapping
    public Result saveTeacher(
            @RequestBody @Valid TeacherForm teacherForm
    ) {
        boolean result = teacherService.saveTeacher(teacherForm);
        return Result.judge(result);
    }

    @Operation(summary = "职工表单数据")
    @GetMapping("/{teacherId}/form")
    public Result<TeacherForm> getTeacherForm(
            @Parameter(description = "职工ID") @PathVariable Long teacherId
    ) {
        TeacherForm formData = teacherService.getTeacherForm(teacherId);
        return Result.success(formData);
    }


    @Operation(summary = "修改职工")
    @PutMapping(value = "/{teacherId}")
    public Result updateTeacher(
            @Parameter(description = "职工ID") @PathVariable Long teacherId,
            @RequestBody @Validated TeacherForm teacherForm) {
        boolean result = teacherService.updateTeacher(teacherId, teacherForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除职工")
    @DeleteMapping("/{ids}")
    public Result deleteTeachers(
            @Parameter(description = "职工ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "职工删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = teacherService.deleteTeachers(idList);
        return Result.judge(result);
    }

    @Operation(summary = "职工下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listTeacherOptions() {
        return Result.success(teacherService.listTeacherOptions());
    }

    @Operation(summary = "教师导入模板下载")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String fileName = "教师导入模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));


        ServletOutputStream outputStream = response.getOutputStream();
        EasyExcel.write(outputStream, TeacherImportVO.class).sheet("教师导入模板").doWrite(List.of());
    }

    @Operation(summary = "导入教师")
    @PostMapping("/_import")
    public Result importTeachers(MultipartFile file) throws IOException {
        TeacherImportListener listener = new TeacherImportListener();
        String msg = ExcelUtils.importExcel(file.getInputStream(), TeacherImportVO.class, listener);
        return Result.success(msg);
    }

    @Operation(summary = "导出教师")
    @GetMapping("/_export")
    public void exportTeachers(TeacherPageQuery queryParams, HttpServletResponse response) throws IOException {
        String fileName = "教师列表.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        List<TeacherExportVO> exportTeacherList = teacherService.getTeacherExport(queryParams);
        EasyExcel.write(response.getOutputStream(), TeacherExportVO.class).sheet("教师列表")
                .doWrite(exportTeacherList);
    }

}
