package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysScoreRuleMapper;
import com.youlai.system.model.entity.SysScoreRule;
import com.youlai.system.model.form.ScoreRuleForm;
import com.youlai.system.service.SysScoreRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysScoreRuleServiceImpl extends ServiceImpl<SysScoreRuleMapper, SysScoreRule> implements SysScoreRuleService {
    @Override
    public SysScoreRule saveRule(ScoreRuleForm form) {
        SysScoreRule entity = new SysScoreRule(); copy(form, entity); entity.setDeleted(0); save(entity); return entity;
    }

    @Override
    public SysScoreRule updateRule(Long id, ScoreRuleForm form) {
        SysScoreRule entity = getById(id); if (entity == null) throw new IllegalArgumentException("赋分规则不存在"); copy(form, entity); updateById(entity); return entity;
    }

    private void copy(ScoreRuleForm form, SysScoreRule entity) {
        entity.setName(form.getName().trim()); entity.setMethod(form.getMethod() == null ? "PENDING" : form.getMethod());
        entity.setTargetFullScore(form.getTargetFullScore()); entity.setConfigJson(form.getConfigJson()); entity.setStatus(form.getStatus() == null ? 1 : form.getStatus()); entity.setRemark(form.getRemark());
    }
}
