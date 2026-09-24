package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.model.entity.SysStudentFollowup;
import com.youlai.system.model.form.StudentFollowupForm;
import com.youlai.system.service.SysStudentFollowupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 学生学习状态和跟进记录接口。 */
@RestController
@RequestMapping("/api/v1/student-followups")
@RequiredArgsConstructor
public class StudentFollowupController {
    private final SysStudentFollowupService followupService;

    @GetMapping("/student/{studentId}")
    public Result<List<SysStudentFollowup>> list(@PathVariable Long studentId) {
        return Result.success(followupService.listByStudentId(studentId));
    }

    @PostMapping("/student/{studentId}")
    public Result<SysStudentFollowup> save(@PathVariable Long studentId, @RequestBody @Valid StudentFollowupForm form) {
        return Result.success(followupService.saveFollowup(studentId, form));
    }

    @PutMapping("/{followupId}")
    public Result<SysStudentFollowup> update(@PathVariable Long followupId, @RequestBody @Valid StudentFollowupForm form) {
        return Result.success(followupService.updateFollowup(followupId, form));
    }

    @DeleteMapping("/{followupId}")
    public Result<Boolean> delete(@PathVariable Long followupId) {
        SysStudentFollowup entity = followupService.getById(followupId);
        if (entity == null) throw new IllegalArgumentException("跟进记录不存在");
        return Result.success(followupService.removeById(followupId));
    }
}
