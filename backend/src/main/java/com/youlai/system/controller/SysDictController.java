package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.bo.DictTypeDictBO;
import com.youlai.system.model.form.DictForm;
import com.youlai.system.model.form.DictTypeForm;
import com.youlai.system.model.query.DictPageQuery;
import com.youlai.system.model.query.DictTypePageQuery;
import com.youlai.system.model.vo.DictPageVO;
import com.youlai.system.model.vo.DictTypePageVO;
import com.youlai.system.common.model.Option;
import com.youlai.system.service.SysDictService;
import com.youlai.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "06.字典接口")
@RestController
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    private final SysDictTypeService dictTypeService;

    @Operation(summary = "字典分页列表")
    @GetMapping("/page")
    public PageResult<DictPageVO> getDictPage(
            DictPageQuery queryParams
    ) {
        Page<DictPageVO> result = dictService.getDictPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典数据表单数据")
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description ="字典ID") @PathVariable Long id
    ) {
        DictForm formData = dictService.getDictForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增字典")
    @PostMapping
    public Result saveDict(
            @RequestBody DictForm DictForm
    ) {
        boolean result = dictService.saveDict(DictForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典")
    @PutMapping("/{id}")
    public Result updateDict(
            @PathVariable Long id,
            @RequestBody DictForm DictForm
    ) {
        boolean status = dictService.updateDict(id, DictForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{ids}")
    public Result deleteDict(
            @Parameter(description ="字典ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        boolean result = dictService.deleteDict(ids);
        return Result.judge(result);
    }


    @Operation(summary = "字典下拉列表")
    @GetMapping("/{typeCode}/options")
    public Result<List<Option>> listDictOptions(
            @Parameter(description ="字典类型编码") @PathVariable String typeCode
    ) {
        List<Option> list = dictService.listDictOptions(typeCode);
        return Result.success(list);
    }


    /*----------------------------------------------------*/
    @Operation(summary = "字典类型分页列表")
    @GetMapping("/types/page")
    public PageResult<DictTypePageVO> getDictTypePage(
            DictTypePageQuery queryParams
    ) {
        Page<DictTypePageVO> result = dictTypeService.getDictTypePage(queryParams);

        Map<String, DictTypeDictBO> dictTypeDictBOMap = dictService.getAllDictTypeDictMap();
        result.getRecords().forEach(it -> {
            DictTypeDictBO dictTypeDictBO = dictTypeDictBOMap.get(it.getCode());
            if (dictTypeDictBO != null) {
                it.setDictCount(dictTypeDictBO.getDictCount());
                it.setDictNameList(dictTypeDictBO.getDictNameList());
            }
        });

        return PageResult.success(result);
    }

    @Operation(summary = "字典类型表单数据")
    @GetMapping("/types/{id}/form")
    public Result<DictTypeForm> getDictTypeForm(
            @Parameter(description ="字典ID") @PathVariable Long id
    ) {
        DictTypeForm dictTypeForm = dictTypeService.getDictTypeForm(id);
        return Result.success(dictTypeForm);
    }

    @Operation(summary = "新增字典类型")
    @PostMapping("/types")
    public Result saveDictType(@RequestBody DictTypeForm dictTypeForm) {
        boolean result = dictTypeService.saveDictType(dictTypeForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/types/{id}")
    public Result updateDictType(@PathVariable Long id, @RequestBody DictTypeForm dictTypeForm) {
        boolean status = dictTypeService.updateDictType(id, dictTypeForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/types/{ids}")
    public Result deleteDictTypes(
            @Parameter(description ="字典类型ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = dictTypeService.deleteDictTypes(ids);


        return Result.judge(result);
    }

}
