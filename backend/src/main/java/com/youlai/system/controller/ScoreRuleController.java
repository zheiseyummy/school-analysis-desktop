package com.youlai.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.entity.SysScoreRule;
import com.youlai.system.model.form.ScoreRuleForm;
import com.youlai.system.service.SysScoreRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 高中赋分规则配置接口。具体政策算法确认前只保存规则，不自动计算。 */
@RestController
@RequestMapping("/api/v1/score-rules")
@RequiredArgsConstructor
public class ScoreRuleController {
    private final SysScoreRuleService ruleService;

    @GetMapping
    public Result<List<SysScoreRule>> list() {
        return Result.success(ruleService.list(new LambdaQueryWrapper<SysScoreRule>().orderByDesc(SysScoreRule::getId)));
    }

    @PostMapping
    public Result<SysScoreRule> save(@RequestBody @Valid ScoreRuleForm form) {
        return Result.success(ruleService.saveRule(form));
    }

    @PutMapping("/{id}")
    public Result<SysScoreRule> update(@PathVariable Long id, @RequestBody @Valid ScoreRuleForm form) {
        return Result.success(ruleService.updateRule(id, form));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        if (ruleService.getById(id) == null) throw new IllegalArgumentException("赋分规则不存在");
        return Result.success(ruleService.removeById(id));
    }
}
