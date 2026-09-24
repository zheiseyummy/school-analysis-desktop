package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysExamCourse;
import com.youlai.system.model.form.ExamCourseConfigForm;
import com.youlai.system.model.vo.ExamCourseConfigVO;

import java.util.List;

public interface SysExamCourseService extends IService<SysExamCourse> {
    SysExamCourse getByExamIdAndCourseId(Long examId, Long courseId);
    boolean hasConfig(Long examId);
    List<ExamCourseConfigVO> getConfig(Long examId);
    boolean saveConfig(Long examId, List<ExamCourseConfigForm> forms);
}
