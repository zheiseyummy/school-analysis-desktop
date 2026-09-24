package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysStudentFollowupMapper;
import com.youlai.system.model.entity.SysStudentFollowup;
import com.youlai.system.model.form.StudentFollowupForm;
import com.youlai.system.service.SysStudentFollowupService;
import com.youlai.system.service.SysStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysStudentFollowupServiceImpl extends ServiceImpl<SysStudentFollowupMapper, SysStudentFollowup>
        implements SysStudentFollowupService {
    private final SysStudentService studentService;

    @Override
    public List<SysStudentFollowup> listByStudentId(Long studentId) {
        ensureStudent(studentId);
        return list(new LambdaQueryWrapper<SysStudentFollowup>()
                .eq(SysStudentFollowup::getStudentId, studentId)
                .orderByDesc(SysStudentFollowup::getFollowupDate)
                .orderByDesc(SysStudentFollowup::getId));
    }

    @Override
    public SysStudentFollowup saveFollowup(Long studentId, StudentFollowupForm form) {
        ensureStudent(studentId);
        SysStudentFollowup entity = new SysStudentFollowup();
        entity.setStudentId(studentId);
        copy(form, entity);
        entity.setDeleted(0);
        save(entity);
        return entity;
    }

    @Override
    public SysStudentFollowup updateFollowup(Long followupId, StudentFollowupForm form) {
        SysStudentFollowup entity = getById(followupId);
        if (entity == null || Integer.valueOf(1).equals(entity.getDeleted())) throw new IllegalArgumentException("跟进记录不存在");
        copy(form, entity);
        updateById(entity);
        return entity;
    }

    private void ensureStudent(Long studentId) {
        if (studentId == null || studentService.getById(studentId) == null) throw new IllegalArgumentException("学生不存在");
    }

    private void copy(StudentFollowupForm form, SysStudentFollowup entity) {
        entity.setLearningStatus(form.getLearningStatus());
        entity.setSpecialSituation(form.getSpecialSituation());
        entity.setFollowupContent(form.getFollowupContent());
        entity.setNextAction(form.getNextAction());
        entity.setFollowupDate(form.getFollowupDate());
    }
}
