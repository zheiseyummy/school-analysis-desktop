package com.youlai.system.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.youlai.system.common.constant.ScoreStatus;
import com.youlai.system.model.entity.*;
import com.youlai.system.model.form.ScoreEntryForm;
import com.youlai.system.model.vo.ScoreEntryVO;
import com.youlai.system.model.vo.ExamCourseConfigVO;
import com.youlai.system.model.vo.ScoreImportChangeVO;
import com.youlai.system.model.vo.ScoreImportPreviewVO;
import com.youlai.system.model.vo.ScoreImportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 成绩导入的两阶段服务：解析和校验阶段只生成内存预览，确认阶段才写成绩。
 * 单机单用户场景下使用短期内存令牌，避免将上传文件或学生数据写入临时公共目录。
 */
@Service
@RequiredArgsConstructor
public class ScoreImportPreviewService {
    public static final String KEEP_BLANKS = "KEEP";
    public static final String CLEAR_BLANKS = "CLEAR";
    private static final Duration PREVIEW_TTL = Duration.ofMinutes(30);

    private final SysGradeService gradeService;
    private final SysClazzService clazzService;
    private final SysStudentService studentService;
    private final SysExamService examService;
    private final SysClazzStudentService clazzStudentService;
    private final SysExamBodyService examBodyService;
    private final SysArrangeService arrangeService;
    private final SysCourseService courseService;
    private final SysScoreService scoreService;
    private final SysScoreImportChangeLogService scoreImportChangeLogService;
    private final SysExamCourseService examCourseService;
    private final BusinessService businessService;

    private final Map<String, PendingPreview> pending = new ConcurrentHashMap<>();

    public ScoreImportPreviewVO preview(Long examId, MultipartFile file, String blankPolicy) throws IOException {
        if (examId == null) throw new IllegalArgumentException("请选择考试");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择成绩 Excel 文件");
        String policy = normalizePolicy(blankPolicy);
        boolean examCourseConfigured = examCourseService.hasConfig(examId);
        Map<Long, ExamCourseConfigVO> examCourses = examCourseService.getConfig(examId).stream()
                .filter(ExamCourseConfigVO::getSelected)
                .collect(Collectors.toMap(ExamCourseConfigVO::getCourseId, value -> value));
        SysExam exam = Optional.ofNullable(examService.getById(examId))
                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));
        List<ParsedRow> rows = new ArrayList<>();
        try (InputStream input = file.getInputStream()) {
            EasyExcel.read(input, ScoreImportVO.class, new AnalysisEventListener<ScoreImportVO>() {
                private int rowNumber = 1;
                @Override public void invoke(ScoreImportVO row, AnalysisContext context) {
                    rows.add(new ParsedRow(++rowNumber, row));
                }
                @Override public void doAfterAllAnalysed(AnalysisContext context) { }
            }).sheet().doRead();
        }

        ScoreImportPreviewVO preview = new ScoreImportPreviewVO();
        preview.setToken(UUID.randomUUID().toString());
        preview.setExamId(examId);
        preview.setFileName(file.getOriginalFilename());
        preview.setCreatedAt(Instant.now());
        preview.setBlankPolicy(policy);
        preview.setTotalRows(rows.size());

        List<ScoreImportChangeVO> changes = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> seenCells = new HashSet<>();
        int validRows = 0;
        int blankCells = 0;
        for (ParsedRow parsed : rows) {
            RowResult result = inspectRow(examId, exam, parsed.rowNumber(), parsed.row(), policy, changes, errors, seenCells, examCourses, examCourseConfigured);
            if (result.valid()) validRows++;
            blankCells += result.blankCells();
        }
        preview.setValidRows(validRows);
        preview.setErrorRows(rows.size() - validRows);
        preview.setChangeCount(changes.size());
        preview.setBlankCellCount(blankCells);
        preview.setChanges(changes);
        preview.setErrors(errors);
        pending.put(preview.getToken(), new PendingPreview(preview, Instant.now(), changes));
        purgeExpired();
        return preview;
    }

    @Transactional
    public Map<String, Object> confirm(String token, boolean allowErrors) {
        PendingPreview stored = pending.remove(token);
        if (stored == null || stored.createdAt().plus(PREVIEW_TTL).isBefore(Instant.now())) {
            throw new IllegalArgumentException("导入预览已失效，请重新上传");
        }
        if (!allowErrors && stored.preview().getErrorRows() > 0) {
            throw new IllegalArgumentException("预览存在错误行，请修正文件或确认仅提交有效行");
        }
        String batchId = stored.changes().isEmpty() ? null : UUID.randomUUID().toString();
        int applied = 0;
        for (ScoreImportChangeVO change : stored.changes()) {
            SysScore before = scoreService.getScoreByExamIdAndStudentIdAndCourseId(
                    stored.preview().getExamId(), change.getStudentId(), change.getCourseId());
            if ("CLEAR".equals(change.getAction())) {
                if (before != null) scoreService.removeById(before.getId());
                applied++;
            } else {
                ScoreEntryForm form = new ScoreEntryForm();
                form.setExamBodyId(change.getExamBodyId());
                form.setCourseId(change.getCourseId());
                ScoreEntryVO score = new ScoreEntryVO();
                score.setStudentId(change.getStudentId());
                score.setScore(change.getNewScore());
                score.setStatus(change.getNewStatus());
                form.setScoreList(List.of(score));
                businessService.saveScore(form);
                applied++;
            }
            SysScore after = scoreService.getScoreByExamIdAndStudentIdAndCourseId(
                    stored.preview().getExamId(), change.getStudentId(), change.getCourseId());
            if (batchId != null) {
                scoreImportChangeLogService.saveChange(batchId, stored.preview().getExamId(), stored.preview().getFileName(),
                        change.getRowNumber(), change.getStudentCode(), change.getStudentName(), change.getCourseName(),
                        change.getStudentId(), change.getCourseId(), change.getAction(), before, after);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("appliedChanges", applied);
        result.put("skippedErrorRows", stored.preview().getErrorRows());
        result.put("token", token);
        result.put("batchId", batchId);
        return result;
    }

    private RowResult inspectRow(Long examId, SysExam exam, int rowNumber, ScoreImportVO row,
                                 String policy, List<ScoreImportChangeVO> changes, List<String> errors,
                                 Set<String> seenCells, Map<Long, ExamCourseConfigVO> examCourses, boolean examCourseConfigured) {
        List<String> rowErrors = new ArrayList<>();
        if (row == null) rowErrors.add("空行");
        if (row != null && isBlank(row.getGradeName())) rowErrors.add("年级为空");
        if (row != null && isBlank(row.getClazzName())) rowErrors.add("班级为空");
        if (row != null && isBlank(row.getStudentCode())) rowErrors.add("学号为空");
        if (row != null && isBlank(row.getStudentName())) rowErrors.add("姓名为空");
        if (!rowErrors.isEmpty()) {
            errors.add("第" + rowNumber + "行：" + String.join("、", rowErrors));
            return new RowResult(false, 0);
        }
        SysGrade grade = gradeService.getByGradeName(row.getGradeName());
        SysClazz clazz = grade == null ? null : clazzService.getByClazzName(grade.getId(), row.getClazzName());
        SysStudent student = studentService.getByCode(row.getStudentCode());
        if (grade == null) rowErrors.add("年级不存在");
        if (clazz == null) rowErrors.add("班级不存在");
        if (student == null) rowErrors.add("学号不存在");
        if (student != null && !Objects.equals(student.getName(), row.getStudentName())) rowErrors.add("学号与姓名不一致");
        if (clazz != null && student != null && !clazzStudentService.existStudentInClazz(clazz.getId(), student.getId(), exam.getYear())) rowErrors.add("学生不属于该班级年度归属");
        SysExamBody examBody = clazz == null ? null : examBodyService.getIdByExamIdAndClazzId(examId, clazz.getId());
        if (examBody == null && clazz != null) rowErrors.add("考试未配置该班级");
        if (!rowErrors.isEmpty()) {
            errors.add("第" + rowNumber + "行：" + String.join("、", rowErrors));
            return new RowResult(false, 0);
        }
        List<Long> courseIds = arrangeService.getCourseIdListByClazzId(clazz.getId());
        List<SysCourse> courses = courseService.listByIds(courseIds).stream()
                .filter(course -> !examCourseConfigured || examCourses.containsKey(course.getId())).toList();
        int blanks = 0;
        boolean valid = true;
        for (SysCourse course : courses) {
            CellValue cell = scoreFor(course.getName(), row);
            SysScore old = scoreService.getScoreByExamIdAndStudentIdAndCourseId(examId, student.getId(), course.getId());
            if (cell.invalid()) {
                errors.add("第" + rowNumber + "行：" + course.getName() + cell.error());
                valid = false;
                continue;
            }
            if (cell.blank()) {
                blanks++;
                if (KEEP_BLANKS.equals(policy)) continue;
                if (old != null) {
                    changes.add(change(rowNumber, row, student, examBody, course, "CLEAR", old.getScore(), null,
                            effectiveStatus(old), null, "空白按确认规则清除原成绩"));
                }
                continue;
            }
            Double score = cell.score();
            String newStatus = cell.status();
            String cellKey = examId + ":" + student.getId() + ":" + course.getId();
            if (!seenCells.add(cellKey)) {
                errors.add("第" + rowNumber + "行：学生" + row.getStudentCode() + "的" + course.getName() + "重复出现");
                valid = false;
                continue;
            }
            Double fullScore = examCourses.containsKey(course.getId()) && examCourses.get(course.getId()).getFullScore() != null
                    ? examCourses.get(course.getId()).getFullScore() : course.getFullScore() == null ? null : course.getFullScore().doubleValue();
            if (ScoreStatus.NORMAL.equals(newStatus) && (score < 0 || (fullScore != null && score > fullScore))) {
                errors.add("第" + rowNumber + "行：" + course.getName() + "成绩超出 0-" + fullScore + "范围");
                valid = false;
                continue;
            }
            String action = old == null ? "ADD" : "UPDATE";
            String oldStatus = old == null ? null : effectiveStatus(old);
            if (old == null || !Objects.equals(old.getScore(), score) || !Objects.equals(oldStatus, newStatus)) {
                changes.add(change(rowNumber, row, student, examBody, course, action,
                        old == null ? null : old.getScore(), score, oldStatus, newStatus, null));
            }
        }
        return new RowResult(valid, blanks);
    }

    private ScoreImportChangeVO change(int rowNumber, ScoreImportVO row, SysStudent student,
                                       SysExamBody examBody, SysCourse course, String action,
                                       Double oldScore, Double newScore, String oldStatus, String newStatus, String message) {
        ScoreImportChangeVO change = new ScoreImportChangeVO();
        change.setRowNumber(rowNumber); change.setStudentCode(row.getStudentCode()); change.setStudentName(row.getStudentName());
        change.setStudentId(student.getId()); change.setExamBodyId(examBody.getId()); change.setCourseId(course.getId());
        change.setCourseName(course.getName()); change.setAction(action); change.setOldScore(oldScore); change.setNewScore(newScore);
        change.setOldStatus(oldStatus); change.setNewStatus(newStatus); change.setMessage(message);
        return change;
    }

    private static CellValue scoreFor(String courseName, ScoreImportVO row) {
        String raw = switch (courseName) {
            case "语文" -> row.getChineseScore(); case "数学" -> row.getMathScore(); case "英语" -> row.getEnglishScore();
            case "物理" -> row.getPhysicsScore(); case "化学" -> row.getChemistryScore(); case "生物" -> row.getOrganismScore();
            case "地理" -> row.getGeographyScore(); case "历史" -> row.getHistoryScore(); case "政治", "道德与法治" -> row.getPoliticsScore();
            default -> null;
        };
        if (raw == null || raw.isBlank()) return CellValue.blankValue();
        String value = raw.trim().replace(" ", "");
        if (value.equals("缺考") || value.equals("缺席") || value.equals("未参加")) return new CellValue(null, ScoreStatus.ABSENT, false, null);
        if (value.equals("未选科") || value.equals("未选")) return new CellValue(null, ScoreStatus.NOT_SELECTED, false, null);
        try { return new CellValue(Double.valueOf(value), ScoreStatus.NORMAL, false, null); }
        catch (NumberFormatException ignored) { return new CellValue(null, null, false, "成绩无法识别（支持数字、缺考、未选科）"); }
    }

    private static String effectiveStatus(SysScore score) {
        if (score.getStatus() != null && !score.getStatus().isBlank()) return score.getStatus();
        return score.getScore() == null ? null : ScoreStatus.NORMAL;
    }

    private static String normalizePolicy(String policy) {
        if (policy == null || policy.isBlank()) return KEEP_BLANKS;
        if (!KEEP_BLANKS.equals(policy) && !CLEAR_BLANKS.equals(policy)) throw new IllegalArgumentException("空白成绩处理规则不支持");
        return policy;
    }

    private static boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private void purgeExpired() { pending.entrySet().removeIf(entry -> entry.getValue().createdAt().plus(PREVIEW_TTL).isBefore(Instant.now())); }
    private record ParsedRow(int rowNumber, ScoreImportVO row) { }
    private record RowResult(boolean valid, int blankCells) { }
    private record PendingPreview(ScoreImportPreviewVO preview, Instant createdAt, List<ScoreImportChangeVO> changes) { }
    private record CellValue(Double score, String status, boolean blank, String error) {
        private static CellValue blankValue() { return new CellValue(null, null, true, null); }
        private boolean invalid() { return !blank && status == null && error != null; }
    }
}
