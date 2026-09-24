package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysStudentFollowup;
import com.youlai.system.model.form.StudentFollowupForm;

import java.util.List;

public interface SysStudentFollowupService extends IService<SysStudentFollowup> {
    List<SysStudentFollowup> listByStudentId(Long studentId);
    SysStudentFollowup saveFollowup(Long studentId, StudentFollowupForm form);
    SysStudentFollowup updateFollowup(Long followupId, StudentFollowupForm form);
}
