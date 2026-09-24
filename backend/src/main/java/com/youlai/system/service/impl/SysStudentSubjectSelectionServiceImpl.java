package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.SysStudentSubjectSelectionMapper;
import com.youlai.system.mapper.SysStudentSubjectSelectionVersionMapper;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.entity.SysStudentSubjectSelection;
import com.youlai.system.model.entity.SysStudentSubjectSelectionVersion;
import com.youlai.system.model.form.StudentSubjectSelectionBatchForm;
import com.youlai.system.model.form.StudentSubjectSelectionRecordForm;
import com.youlai.system.model.form.StudentSubjectSelectionVersionForm;
import com.youlai.system.model.vo.StudentSubjectSelectionRecordVO;
import com.youlai.system.service.SysCourseService;
import com.youlai.system.service.SysGradeService;
import com.youlai.system.service.SysStudentService;
import com.youlai.system.service.SysStudentSubjectSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysStudentSubjectSelectionServiceImpl
        extends ServiceImpl<SysStudentSubjectSelectionMapper, SysStudentSubjectSelection>
        implements SysStudentSubjectSelectionService {
    private final SysStudentSubjectSelectionVersionMapper versionMapper;
    private final SysGradeService gradeService;
    private final SysStudentService studentService;
    private final SysCourseService courseService;

    @Override
    public List<SysStudentSubjectSelectionVersion> listVersions(Long gradeId) {
        return versionMapper.selectList(new LambdaQueryWrapper<SysStudentSubjectSelectionVersion>()
                .eq(gradeId != null, SysStudentSubjectSelectionVersion::getGradeId, gradeId)
                .orderByDesc(SysStudentSubjectSelectionVersion::getEffectiveDate)
                .orderByDesc(SysStudentSubjectSelectionVersion::getId));
    }

    @Override
    public SysStudentSubjectSelectionVersion getVersion(Long versionId) {
        return versionMapper.selectById(versionId);
    }

    @Override
    public SysStudentSubjectSelectionVersion saveVersion(StudentSubjectSelectionVersionForm form) {
        SysGrade grade = gradeService.getById(form.getGradeId());
        if (grade == null || !"高中".equals(grade.getStage())) throw new IllegalArgumentException("选科版本只能关联高中年级");
        SysStudentSubjectSelectionVersion entity = new SysStudentSubjectSelectionVersion();
        entity.setGradeId(form.getGradeId()); entity.setName(form.getName().trim());
        entity.setEffectiveDate(form.getEffectiveDate()); entity.setStatus(form.getStatus() == null ? 1 : form.getStatus());
        entity.setNote(form.getNote()); entity.setDeleted(0); versionMapper.insert(entity);
        return entity;
    }

    @Override
    public List<StudentSubjectSelectionRecordVO> listRecords(Long versionId) {
        SysStudentSubjectSelectionVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new IllegalArgumentException("选科版本不存在");
        List<SysStudentSubjectSelection> records = list(new LambdaQueryWrapper<SysStudentSubjectSelection>()
                .eq(SysStudentSubjectSelection::getVersionId, versionId).orderByAsc(SysStudentSubjectSelection::getId));
        Set<Long> studentIds = records.stream().map(SysStudentSubjectSelection::getStudentId).collect(Collectors.toSet());
        Set<Long> courseIds = new HashSet<>();
        records.forEach(record -> { if (record.getTrackCourseId() != null) courseIds.add(record.getTrackCourseId()); courseIds.addAll(parseIds(record.getElectiveCourseIds())); });
        Map<Long, SysStudent> students = studentService.listByIds(studentIds).stream().collect(Collectors.toMap(SysStudent::getId, it -> it));
        Map<Long, SysCourse> courses = courseService.listByIds(courseIds).stream().collect(Collectors.toMap(SysCourse::getId, it -> it));
        return records.stream().map(record -> {
            StudentSubjectSelectionRecordVO vo = new StudentSubjectSelectionRecordVO();
            vo.setId(record.getId()); vo.setStudentId(record.getStudentId());
            SysStudent student = students.get(record.getStudentId());
            vo.setStudentCode(student == null ? "" : student.getCode()); vo.setStudentName(student == null ? "" : student.getName());
            vo.setTrackCourseId(record.getTrackCourseId());
            SysCourse track = courses.get(record.getTrackCourseId()); vo.setTrackCourseName(track == null ? "" : track.getName());
            List<Long> electiveIds = parseIds(record.getElectiveCourseIds()); vo.setElectiveCourseIds(electiveIds);
            vo.setElectiveCourseNames(electiveIds.stream().map(courses::get).filter(Objects::nonNull).map(SysCourse::getName).toList());
            vo.setCombinationCode(record.getCombinationCode()); vo.setSource(record.getSource()); vo.setNote(record.getNote());
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public void saveRecords(Long versionId, StudentSubjectSelectionBatchForm form) {
        SysStudentSubjectSelectionVersion version = versionMapper.selectById(versionId);
        if (version == null) throw new IllegalArgumentException("选科版本不存在");
        List<StudentSubjectSelectionRecordForm> forms = form.getRecords();
        Set<Long> studentIds = forms.stream().map(StudentSubjectSelectionRecordForm::getStudentId).collect(Collectors.toSet());
        if (studentIds.size() != forms.size()) throw new IllegalArgumentException("同一版本中学生不能重复");
        if (studentService.count(new LambdaQueryWrapper<SysStudent>().in(SysStudent::getId, studentIds)) != studentIds.size()) throw new IllegalArgumentException("存在不存在的学生");
        Set<Long> courseIds = new HashSet<>();
        forms.forEach(item -> { if (item.getTrackCourseId() != null) courseIds.add(item.getTrackCourseId()); if (item.getElectiveCourseIds() != null) courseIds.addAll(item.getElectiveCourseIds()); });
        if (!courseIds.isEmpty() && courseService.count(new LambdaQueryWrapper<SysCourse>().in(SysCourse::getId, courseIds)) != courseIds.size()) throw new IllegalArgumentException("存在不存在的学科");
        remove(new LambdaQueryWrapper<SysStudentSubjectSelection>().eq(SysStudentSubjectSelection::getVersionId, versionId));
        for (StudentSubjectSelectionRecordForm item : forms) {
            if (item.getCombinationCode() == null || item.getCombinationCode().isBlank()) throw new IllegalArgumentException("选科组合不能为空");
            if (item.getElectiveCourseIds() != null && item.getElectiveCourseIds().size() > 2) throw new IllegalArgumentException("3+1+2 的选择性科目最多 2 门");
            SysStudentSubjectSelection entity = new SysStudentSubjectSelection();
            entity.setVersionId(versionId); entity.setStudentId(item.getStudentId()); entity.setTrackCourseId(item.getTrackCourseId());
            entity.setElectiveCourseIds(joinIds(item.getElectiveCourseIds())); entity.setCombinationCode(item.getCombinationCode().trim());
            entity.setSource("MANUAL"); entity.setNote(item.getNote()); entity.setDeleted(0); save(entity);
        }
    }

    private String joinIds(List<Long> ids) { return ids == null ? "" : ids.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(",")); }
    private List<Long> parseIds(String value) { if (value == null || value.isBlank()) return List.of(); return Arrays.stream(value.split(",")).map(String::trim).filter(it -> !it.isBlank()).map(Long::valueOf).toList(); }
}
