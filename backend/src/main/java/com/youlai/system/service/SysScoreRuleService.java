package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysScoreRule;
import com.youlai.system.model.form.ScoreRuleForm;

public interface SysScoreRuleService extends IService<SysScoreRule> {
    SysScoreRule saveRule(ScoreRuleForm form);
    SysScoreRule updateRule(Long id, ScoreRuleForm form);
}
