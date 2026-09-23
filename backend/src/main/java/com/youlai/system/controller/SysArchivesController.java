package com.youlai.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.entity.*;
import com.youlai.system.model.form.ArchivesForm;
import com.youlai.system.model.query.ArchivesPageQuery;
import com.youlai.system.model.vo.ArchivesPageVO;
import com.youlai.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学情档案控制器
 */
@Tag(name = "27.学情档案接口")
@RestController
@RequestMapping("/api/v1/archivess")
@RequiredArgsConstructor
public class SysArchivesController {

    private final SysArchivesService archivesService;

    private final SysClazzService clazzService;

    private final SysGradeService gradeService;

    private final SysStudentService studentService;

    private final SysTeacherService teacherService;


    @Operation(summary = "学情档案分页列表")
    @GetMapping("/page")
    public PageResult<ArchivesPageVO> getArchivesPage(
            ArchivesPageQuery queryParams
    ) {
        IPage<ArchivesPageVO> result = archivesService.getArchivesPage(queryParams);
        result.getRecords().forEach(archivesPageVO -> {

            archivesPageVO.setCanEdit(true);

            //学生
            if (archivesPageVO.getStudentId() != null) {
                SysStudent student = studentService.getById(archivesPageVO.getStudentId());
                if (student != null) {
                    archivesPageVO.setStudentName(student.getName());
                }
            }

            //教师
            if (archivesPageVO.getTeacherId() != null) {
                SysTeacher teacher = teacherService.getById(archivesPageVO.getTeacherId());
                if (teacher != null) {
                    archivesPageVO.setTeacherName(teacher.getName());
                }
            }


            //年级
            if (archivesPageVO.getGradeId() != null) {
                SysGrade grade = gradeService.getById(archivesPageVO.getGradeId());
                if (grade != null) {
                    archivesPageVO.setGradeName(grade.getName());
                }
            }
            //班级
            if (archivesPageVO.getClazzId() != null) {
                SysClazz clazz = clazzService.getById(archivesPageVO.getClazzId());
                if (clazz != null) {
                    archivesPageVO.setClazzName(clazz.getName());
                }
            }

        });
        return PageResult.success(result);
    }

    @Operation(summary = "新增学情档案")
    @PostMapping
    public Result saveArchives(
            @RequestBody @Valid ArchivesForm archivesForm
    ) {

        extroInfoSetting(archivesForm);

        boolean result = archivesService.saveArchives(archivesForm);
        return Result.judge(result);
    }

    private void extroInfoSetting(@RequestBody @Valid ArchivesForm archivesForm) {
        SysClazz clazz = clazzService.getById(archivesForm.getClazzId());
        Assert.notNull(clazz, "班级不存在");
        SysGrade grade = gradeService.getById(clazz.getGradeId());
        Assert.notNull(grade, "年级不存在");
        archivesForm.setGradeId(grade.getId());
        archivesForm.setGradeName(grade.getName());
        archivesForm.setClazzName(clazz.getName());

    }

    @Operation(summary = "学情档案表单数据")
    @GetMapping("/{archivesId}/form")
    public Result<ArchivesForm> getArchivesForm(
            @Parameter(description = "学情档案ID") @PathVariable Long archivesId
    ) {
        ArchivesForm formData = archivesService.getArchivesForm(archivesId);

        //学生
        if (formData.getStudentId() != null) {
            SysStudent student = studentService.getById(formData.getStudentId());
            if (student != null) {
                formData.setStudentName(student.getName());
            }
        }

        //教师
        if (formData.getTeacherId() != null) {
            SysTeacher teacher = teacherService.getById(formData.getTeacherId());
            if (teacher != null) {
                formData.setTeacherName(teacher.getName());
            }
        }


        //年级
        if (formData.getGradeId() != null) {
            SysGrade grade = gradeService.getById(formData.getGradeId());
            if (grade != null) {
                formData.setGradeName(grade.getName());
            }
        }
        //班级
        if (formData.getClazzId() != null) {
            SysClazz clazz = clazzService.getById(formData.getClazzId());
            if (clazz != null) {
                formData.setClazzName(clazz.getName());
            }
        }


        return Result.success(formData);
    }


    @Operation(summary = "修改学情档案")
    @PutMapping(value = "/{archivesId}")
    public Result updateArchives(
            @Parameter(description = "学情档案ID") @PathVariable Long archivesId,
            @RequestBody @Validated ArchivesForm archivesForm) {
        extroInfoSetting(archivesForm);
        boolean result = archivesService.updateArchives(archivesId, archivesForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除学情档案")
    @DeleteMapping("/{ids}")
    public Result deleteArchivess(
            @Parameter(description = "学情档案ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "学情档案删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = archivesService.deleteArchivess(idList);
        return Result.judge(result);
    }

    @Operation(summary = "学情档案下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listArchivesOptions() {
        List<Option> list = archivesService.listArchivesOptions();
        return Result.success(list);
    }

}
