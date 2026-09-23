package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.bo.TeacherSexCountBO;
import com.youlai.system.model.entity.SysTeacher;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.query.TeacherPageQuery;
import com.youlai.system.model.vo.TeacherExportVO;
import com.youlai.system.model.vo.TeacherPageVO;

import java.util.List;
import java.util.Map;

public interface SysTeacherService extends IService<SysTeacher> {
    Page<TeacherPageVO> getTeacherPage(TeacherPageQuery queryParams);

    List<TeacherExportVO> getTeacherExport(TeacherPageQuery queryParams);

    boolean saveTeacher(TeacherForm teacherForm);

    boolean updateTeacher(Long teacherId, TeacherForm teacherForm);

    TeacherForm getTeacherForm(Long teacherId);

    boolean deleteTeachers(String idsStr);

    boolean deleteTeachers(List<Long> idList);

    List<Option> listTeacherOptions();

    Map<Long, String> allTeacherIdNameMap();

    List<TeacherSexCountBO> getAllTeacherSexCount();

    SysTeacher getByCode(String code);
}
