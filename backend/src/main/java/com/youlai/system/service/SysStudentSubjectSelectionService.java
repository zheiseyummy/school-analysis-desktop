package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysStudentSubjectSelection;
import com.youlai.system.model.entity.SysStudentSubjectSelectionVersion;
import com.youlai.system.model.form.StudentSubjectSelectionBatchForm;
import com.youlai.system.model.form.StudentSubjectSelectionVersionForm;
import com.youlai.system.model.vo.StudentSubjectSelectionRecordVO;

import java.util.List;

public interface SysStudentSubjectSelectionService extends IService<SysStudentSubjectSelection> {
    List<SysStudentSubjectSelectionVersion> listVersions(Long gradeId);
    SysStudentSubjectSelectionVersion getVersion(Long versionId);
    SysStudentSubjectSelectionVersion saveVersion(StudentSubjectSelectionVersionForm form);
    List<StudentSubjectSelectionRecordVO> listRecords(Long versionId);
    void saveRecords(Long versionId, StudentSubjectSelectionBatchForm form);
}
