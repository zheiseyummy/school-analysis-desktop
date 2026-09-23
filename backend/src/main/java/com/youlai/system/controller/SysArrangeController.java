package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.form.ArrangeForm;
import com.youlai.system.model.query.ArrangePageQuery;
import com.youlai.system.model.vo.ArrangePageVO;
import com.youlai.system.service.SysArrangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "15.教学安排接口")
@RestController
@RequestMapping("/api/v1/arrange")
@RequiredArgsConstructor
public class SysArrangeController {

    private final SysArrangeService arrangeService;

    @Operation(summary = "教学安排分页列表")
    @GetMapping("/page")
    public PageResult<ArrangePageVO> getArrangePage(
            ArrangePageQuery queryParams
    ) {
        Page<ArrangePageVO> result = arrangeService.getArrangePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "教学安排数据表单数据")
    @GetMapping("/{id}/form")
    public Result<ArrangeForm> getArrangeForm(
            @Parameter(description = "教学安排ID") @PathVariable Long id
    ) {
        ArrangeForm formData = arrangeService.getArrangeForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增教学安排")
    @PostMapping
    public Result saveArrange(
            @RequestBody ArrangeForm ArrangeForm
    ) {
        boolean result = arrangeService.saveArrange(ArrangeForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改教学安排")
    @PutMapping("/{id}")
    public Result updateArrange(
            @PathVariable Long id,
            @RequestBody ArrangeForm ArrangeForm
    ) {
        boolean status = arrangeService.updateArrange(id, ArrangeForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除教学安排")
    @DeleteMapping("/{ids}")
    public Result deleteArrange(
            @Parameter(description = "教学安排ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        boolean result = arrangeService.deleteArrange(ids);
        return Result.judge(result);
    }


    @Operation(summary = "教学安排下拉列表")
    @GetMapping("/{clazzId}/options")
    public Result<List<Option>> listArrangeOptions(
            @Parameter(description = "班级ID") @PathVariable Long clazzId
    ) {
        List<Option> list = arrangeService.listArrangeOptions(clazzId);
        return Result.success(list);
    }
}
