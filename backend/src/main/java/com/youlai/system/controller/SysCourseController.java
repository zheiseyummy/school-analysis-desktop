package com.youlai.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.form.CourseForm;
import com.youlai.system.model.query.CoursePageQuery;
import com.youlai.system.model.vo.CoursePageVO;
import com.youlai.system.service.SysArrangeService;
import com.youlai.system.service.SysCourseService;
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
 * 课程控制器
 */
@Tag(name = "14.课程接口")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class SysCourseController {

    private final SysCourseService courseService;

    private final SysArrangeService arrangeService;


    @Operation(summary = "课程分页列表")
    @GetMapping("/page")
    public PageResult<CoursePageVO> getCoursePage(
            CoursePageQuery queryParams
    ) {
        IPage<CoursePageVO> result = courseService.getCoursePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增课程")
    @PostMapping
    public Result saveCourse(
            @RequestBody @Valid CourseForm courseForm
    ) {
        boolean result = courseService.saveCourse(courseForm);
        return Result.judge(result);
    }

    @Operation(summary = "课程表单数据")
    @GetMapping("/{courseId}/form")
    public Result<CourseForm> getCourseForm(
            @Parameter(description = "课程ID") @PathVariable Long courseId
    ) {
        CourseForm formData = courseService.getCourseForm(courseId);
        return Result.success(formData);
    }


    @Operation(summary = "修改课程")
    @PutMapping(value = "/{courseId}")
    public Result updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestBody @Validated CourseForm courseForm) {
        boolean result = courseService.updateCourse(courseId, courseForm);
        return Result.judge(result);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{ids}")
    public Result deleteCourses(
            @Parameter(description = "课程ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "课程删除数据为空");
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        boolean result = courseService.deleteCourses(idList);
        return Result.judge(result);
    }

    @Operation(summary = "课程下拉列表")
    @GetMapping("/options")
    public Result<List<Option>> listCourseOptions(@RequestParam(required = false) Long clazzId) {
        List<Option> list = courseService.listCourseOptions();
        if (clazzId != null) {
            List<Long> courseIdList = arrangeService.getCourseIdListByClazzId(clazzId);
            if (CollectionUtil.isNotEmpty(courseIdList)) {
                list.forEach(it -> {
                    if (courseIdList.contains(it.getValue())) {
                        it.setLabel(it.getLabel() + "(√)");
                    }
                });
            }
        }
        return Result.success(list);
    }

}
