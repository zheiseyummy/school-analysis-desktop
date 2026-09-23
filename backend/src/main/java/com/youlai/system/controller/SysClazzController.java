package com.youlai.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.bo.ClazzArrangeBO;
import com.youlai.system.model.bo.ClazzStudentBO;
import com.youlai.system.model.form.ClazzForm;
import com.youlai.system.model.query.ClazzPageQuery;
import com.youlai.system.model.vo.ClazzPageVO;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级控制器
 */
@Tag(name = "11.班级接口")
@RestController
@RequestMapping("/api/v1/clazzs")
@RequiredArgsConstructor
public class SysClazzController {

    private final SysClazzService clazzService;

    private final SysTeacherService teacherService;

    private final SysGradeService gradeService;

    private final SchoolService schoolService;

    private final SysClazzStudentService  clazzStudentService;

    private final SysArrangeService arrangeService;

    private final SysScoreService scoreService;

    private final SysExamBodyService examBodyService;

    @Operation(summary = "班级分页列表")
    @GetMapping("/page")
    public PageResult<ClazzPageVO> getClazzPage(
            ClazzPageQuery queryParams
    ) {
        IPage<ClazzPageVO> result = clazzService.getClazzPage(queryParams);
        Map<Long, String> teacherIdNameMap = teacherService.allTeacherIdNameMap();
        Map<Long, String> gradeNameMap = gradeService.allGradeIdNameMap();
        Map<Long, ClazzStudentBO> clazzStudentBOMap = clazzStudentService.getAllClazzStudentCountNameMap(DateUtil.thisYear());

        Map<Long, ClazzArrangeBO> clazzArrangeBOMap = arrangeService.getAllClazzArrangeMap();

        result.getRecords().forEach(it -> {

            String managerName = teacherIdNameMap.get(it.getManagerId());
            it.setManagerName(managerName);

            String gradeName = gradeNameMap.get(it.getGradeId());
            it.setGradeName(gradeName);

            ClazzStudentBO clazzStudentBO = clazzStudentBOMap.get(it.getId());
            if (clazzStudentBO != null) {
                it.setStudentCount(clazzStudentBO.getStudentCount());
                it.setStudentNameList(clazzStudentBO.getStudentNameList());
            }

            ClazzArrangeBO clazzArrangeBO = clazzArrangeBOMap.get(it.getId());
            if (clazzArrangeBO != null) {
                it.setArrangeCount(clazzArrangeBO.getArrangeCount());
                it.setArrangeNameList(clazzArrangeBO.getArrangeNameList());
            }
        });
        return PageResult.success(result);
    }

    @Operation(summary = "新增班级")
    @PostMapping
    public Result saveClazz(
            @RequestBody @Valid ClazzForm clazzForm
    ) {
        boolean result = clazzService.saveClazz(clazzForm);
        return Result.judge(result);
    }

    @Operation(summary = "班级表单数据")
    @GetMapping("/{clazzId}/form")
    public Result<ClazzForm> getClazzForm(
            @Parameter(description = "班级ID") @PathVariable Long clazzId
    ) {
        ClazzForm formData = clazzService.getClazzForm(clazzId);
        return Result.success(formData);
    }


    @Operation(summary = "修改班级")
    @PutMapping(value = "/{clazzId}")
    public Result updateClazz(
            @Parameter(description = "班级ID") @PathVariable Long clazzId,
            @RequestBody @Validated ClazzForm clazzForm) {
        boolean result = clazzService.updateClazz(clazzId, clazzForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{ids}")
    public Result deleteClazzs(
            @Parameter(description = "班级ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "班级删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = clazzService.deleteClazzs(idList);
        //删除班级的关联学生信息
        idList.forEach(clazzStudentService::removeByClazzId);
        //删除班级的关联成绩信息
        idList.forEach(scoreService::removeByClazzId);
        //删除班级的关联考试信息
        idList.forEach(examBodyService::removeByClazzId);
        return Result.judge(result);
    }

    @Operation(summary = "班级下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listClazzOptions() {
        List<Option> list = clazzService.listClazzOptions();
        return Result.success(list);
    }

    @Operation(summary = "携带年级的班级下拉列表")
    @GetMapping("/complex_options")
    public Result<List<Option<Long>>> listGradeClazzOptions() {
        List<Option<Long>> list = schoolService.listGradeClazzOptions();
        return Result.success(list);
    }

}
