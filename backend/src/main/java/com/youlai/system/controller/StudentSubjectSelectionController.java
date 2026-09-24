package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.common.model.Option;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysClazzStudent;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.entity.SysStudentSubjectSelectionVersion;
import com.youlai.system.model.form.StudentSubjectSelectionBatchForm;
import com.youlai.system.model.form.StudentSubjectSelectionVersionForm;
import com.youlai.system.model.vo.StudentSubjectSelectionRecordVO;
import com.youlai.system.service.SysStudentSubjectSelectionService;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysClazzStudentService;
import com.youlai.system.service.SysStudentService;
import com.youlai.system.service.SysGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/** 高中 3+1+2 选科版本和学生组合接口。 */
@RestController
@RequestMapping("/api/v1/student-subject-selections")
@RequiredArgsConstructor
public class StudentSubjectSelectionController {
    private final SysStudentSubjectSelectionService selectionService;
    private final SysGradeService gradeService;
    private final SysClazzService clazzService;
    private final SysClazzStudentService clazzStudentService;
    private final SysStudentService studentService;

    @GetMapping("/versions")
    public Result<List<SysStudentSubjectSelectionVersion>> listVersions(@RequestParam(required = false) Long gradeId) {
        return Result.success(selectionService.listVersions(gradeId));
    }

    @PostMapping("/versions")
    public Result<SysStudentSubjectSelectionVersion> saveVersion(@RequestBody @Valid StudentSubjectSelectionVersionForm form) {
        return Result.success(selectionService.saveVersion(form));
    }

    @GetMapping("/versions/{versionId}/records")
    public Result<List<StudentSubjectSelectionRecordVO>> listRecords(@PathVariable Long versionId) {
        return Result.success(selectionService.listRecords(versionId));
    }

    @GetMapping("/versions/{versionId}/student-options")
    public Result<List<Option<Long>>> studentOptions(@PathVariable Long versionId) {
        SysStudentSubjectSelectionVersion version = selectionService.getVersion(versionId);
        if (version == null) throw new IllegalArgumentException("选科版本不存在");
        if (gradeService.getById(version.getGradeId()) == null) throw new IllegalArgumentException("年级不存在");
        List<Long> clazzIds = clazzService.list(new LambdaQueryWrapper<SysClazz>().eq(SysClazz::getGradeId, version.getGradeId())).stream().map(SysClazz::getId).toList();
        if (clazzIds.isEmpty()) return Result.success(List.of());
        LinkedHashSet<Long> ids = new LinkedHashSet<>(clazzStudentService.list(new LambdaQueryWrapper<SysClazzStudent>().in(SysClazzStudent::getClazzId, clazzIds)).stream().map(SysClazzStudent::getStudentId).toList());
        List<SysStudent> students = studentService.listByIds(new ArrayList<>(ids));
        return Result.success(students.stream().sorted((a, b) -> String.valueOf(a.getCode()).compareTo(String.valueOf(b.getCode()))).map(student -> new Option<>(student.getId(), student.getCode() + " - " + student.getName())).toList());
    }

    @PutMapping("/versions/{versionId}/records")
    public Result<Boolean> saveRecords(@PathVariable Long versionId, @RequestBody @Valid StudentSubjectSelectionBatchForm form) {
        selectionService.saveRecords(versionId, form);
        return Result.success(true);
    }
}
