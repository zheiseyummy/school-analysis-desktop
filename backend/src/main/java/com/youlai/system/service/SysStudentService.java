package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.bo.StudentSexCountBO;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.query.StudentPageQuery;
import com.youlai.system.model.vo.StudentPageVO;

import java.util.List;
import java.util.Map;

public interface SysStudentService extends IService<SysStudent> {
    Page<StudentPageVO> getStudentPage(StudentPageQuery queryParams);

    List<SysStudent> getStudentExport(StudentPageQuery queryParams);

    boolean saveStudent(StudentForm studentForm);

    boolean updateStudent(Long studentId, StudentForm studentForm);

    StudentForm getStudentForm(Long studentId);

    boolean deleteStudents(String idsStr);

    boolean deleteStudents(List<Long> idList);

    List<Option> listStudentOptions();

    Map<Long, String> allStudentIdNameMap();

    List<StudentSexCountBO> getAllStudentSexCount();

    SysStudent getByCode(String code);
}
