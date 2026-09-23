package com.youlai.system.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.bo.GradeClazzBO;
import com.youlai.system.model.form.GradeForm;
import com.youlai.system.model.query.GradePageQuery;
import com.youlai.system.model.vo.GradePageVO;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysGradeService;
import com.youlai.system.service.SysTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 年级控制器
 */
@Tag(name = "8.年级接口")
@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class SysGradeController {

    private final SysGradeService gradeService;

    private final SysTeacherService teacherService;

    private final SysClazzService clazzService;

    @Operation(summary = "年级分页列表")
    @GetMapping("/page")
    public PageResult<GradePageVO> getGradePage(
            GradePageQuery queryParams
    ) {
        IPage<GradePageVO> result = gradeService.getGradePage(queryParams);
        Map<Long, String> teacherIdNameMap = teacherService.allTeacherIdNameMap();
        Map<Long, GradeClazzBO> gradeClazzBOMap = clazzService.getAllGradeClazzCountNameMap();
        result.getRecords().forEach(it -> {
            String positionName = teacherIdNameMap.get(it.getManagerId());
            it.setManagerName(positionName);

            GradeClazzBO gradeClazzBO = gradeClazzBOMap.get(it.getId());
            if (gradeClazzBO != null) {
                it.setClazzCount(gradeClazzBO.getClazzCount());
                it.setClazzNameList(gradeClazzBO.getClazzNameList());
            }
        });
        return PageResult.success(result);
    }

    @Operation(summary = "新增年级")
    @PostMapping
    public Result saveGrade(
            @RequestBody @Valid GradeForm gradeForm
    ) {
        boolean result = gradeService.saveGrade(gradeForm);
        return Result.judge(result);
    }

    @Operation(summary = "年级表单数据")
    @GetMapping("/{gradeId}/form")
    public Result<GradeForm> getGradeForm(
            @Parameter(description = "年级ID") @PathVariable Long gradeId
    ) {
        GradeForm formData = gradeService.getGradeForm(gradeId);
        return Result.success(formData);
    }


    @Operation(summary = "修改年级")
    @PutMapping(value = "/{gradeId}")
    public Result updateGrade(
            @Parameter(description = "年级ID") @PathVariable Long gradeId,
            @RequestBody @Validated GradeForm gradeForm) {
        boolean result = gradeService.updateGrade(gradeId, gradeForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除年级")
    @DeleteMapping("/{ids}")
    public Result deleteGrades(
            @Parameter(description = "年级ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "年级删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        Long clazzCount = clazzService.countByGradeIdList(idList);
        Assert.isTrue(clazzCount == 0, "只能删除没有没有班级关联的年级信息！");
        boolean result = gradeService.deleteGrades(idList);
        return Result.judge(result);
    }

    @Operation(summary = "年级下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listGradeOptions() {
        List<Option> list = gradeService.listGradeOptions();
        return Result.success(list);
    }

}
