package com.youlai.system.service;

import cn.hutool.json.JSONUtil;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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

    public ScoreImportPreviewVO preview(Long examId, MultipartFile file, String blankPolicy, String mappingJson) throws IOException {
        if (examId == null) throw new IllegalArgumentException("请选择考试");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择成绩 Excel 文件");
        String policy = normalizePolicy(blankPolicy);
        Map<String, Integer> customMapping = parseMapping(mappingJson);
        boolean examCourseConfigured = examCourseService.hasConfig(examId);
        Map<Long, ExamCourseConfigVO> examCourses = examCourseService.getConfig(examId).stream()
                .filter(ExamCourseConfigVO::getSelected)
                .collect(Collectors.toMap(ExamCourseConfigVO::getCourseId, value -> value));
        SysExam exam = Optional.ofNullable(examService.getById(examId))
                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));
        WorkbookData workbookData;
        try (InputStream input = file.getInputStream()) {
            workbookData = readWorkbook(input, customMapping);
        }
        List<ParsedRow> rows = workbookData.rows();

        ScoreImportPreviewVO preview = new ScoreImportPreviewVO();
        preview.setToken(UUID.randomUUID().toString());
        preview.setExamId(examId);
        preview.setFileName(file.getOriginalFilename());
        preview.setCreatedAt(Instant.now());
        preview.setBlankPolicy(policy);
        preview.setSheetName(workbookData.sheetName());
        preview.setHeaderRowNumber(workbookData.headerRowNumber());
        preview.setMappingComplete(workbookData.mappingComplete());
        preview.setHeaderMappings(workbookData.headerMappings());
        preview.setDetectedColumnIndexes(workbookData.columnIndexes());
        preview.setAvailableColumns(workbookData.availableColumns());
        preview.setMappingWarnings(workbookData.mappingWarnings());
        preview.setTotalRows(rows.size());

        List<ScoreImportChangeVO> changes = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> seenCells = new HashSet<>();
        int validRows = 0;
        int blankCells = 0;
        if (workbookData.mappingComplete()) {
            for (ParsedRow parsed : rows) {
                RowResult result = inspectRow(examId, exam, parsed.rowNumber(), parsed.row(), policy, changes, errors, seenCells, examCourses, examCourseConfigured);
                if (result.valid()) validRows++;
                blankCells += result.blankCells();
            }
        } else {
            errors.addAll(workbookData.mappingWarnings());
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
        if (!stored.preview().isMappingComplete()) {
            throw new IllegalArgumentException("成绩表表头未完成映射，请重新选择或整理表头后再导入");
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

    /**
     * 读取成绩工作簿并完成轻量表头识别。这里不依赖固定的 EasyExcel 注解表头，
     * 以便兼容多行表头、表头顺序变化和合并单元格覆盖的数据行。
     */
    private static WorkbookData readWorkbook(InputStream input, Map<String, Integer> customMapping) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = chooseSheet(workbook);
            DataFormatter formatter = new DataFormatter();
            int maxColumn = maxColumn(sheet);
            List<List<String>> matrix = new ArrayList<>();
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                List<String> row = new ArrayList<>();
                for (int columnIndex = 0; columnIndex < maxColumn; columnIndex++) {
                    row.add(mergedCellText(sheet, rowIndex, columnIndex, formatter));
                }
                matrix.add(row);
            }
            HeaderDetection detection = detectHeaders(matrix);
            if (!customMapping.isEmpty()) detection = detection.withCustomMapping(customMapping);
            if (!detection.mappingComplete()) {
                return new WorkbookData(List.of(), sheet.getSheetName(), detection.headerRowNumber() < 0 ? 0 : detection.headerRowNumber() + 1,
                        detection.columnIndexes(), detection.headerMappings(), detection.availableColumns(), detection.warnings(), false);
            }
            List<ParsedRow> rows = new ArrayList<>();
            for (int rowIndex = detection.headerRowNumber() + 1; rowIndex < matrix.size(); rowIndex++) {
                List<String> values = matrix.get(rowIndex);
                if (values.stream().allMatch(ScoreImportPreviewService::isBlank)) continue;
                rows.add(new ParsedRow(rowIndex + 1, toImportRow(values, detection.columnIndexes())));
            }
            return new WorkbookData(rows, sheet.getSheetName(), detection.headerRowNumber() + 1,
                    detection.columnIndexes(), detection.headerMappings(), detection.availableColumns(), detection.warnings(), true);
        }
    }

    private static Map<String, Integer> parseMapping(String mappingJson) {
        if (isBlank(mappingJson)) return Map.of();
        try {
            Map<String, Integer> result = new LinkedHashMap<>();
            JSONUtil.parseObj(mappingJson).forEach((key, value) -> {
                if (value != null && !isBlank(String.valueOf(value))) result.put(key, Integer.valueOf(String.valueOf(value)));
            });
            return result;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("成绩列映射格式无法识别，请重新选择列");
        }
    }

    private static Sheet chooseSheet(Workbook workbook) {
        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            Sheet sheet = workbook.getSheetAt(index);
            for (int rowIndex = 0; rowIndex <= Math.min(sheet.getLastRowNum(), 20); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null && row.getFirstCellNum() >= 0 && row.getLastCellNum() > row.getFirstCellNum()) {
                    return sheet;
                }
            }
        }
        if (workbook.getNumberOfSheets() == 0) throw new IllegalArgumentException("Excel 中没有可读取的工作表");
        return workbook.getSheetAt(0);
    }

    private static int maxColumn(Sheet sheet) {
        int max = 0;
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null && row.getLastCellNum() > max) max = row.getLastCellNum();
        }
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            max = Math.max(max, sheet.getMergedRegion(i).getLastColumn() + 1);
        }
        return Math.max(max, 1);
    }

    private static String mergedCellText(Sheet sheet, int rowIndex, int columnIndex, DataFormatter formatter) {
        Cell cell = sheet.getRow(rowIndex) == null ? null : sheet.getRow(rowIndex).getCell(columnIndex);
        if (cell != null && !isBlank(formatter.formatCellValue(cell))) return formatter.formatCellValue(cell).trim();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            org.apache.poi.ss.util.CellRangeAddress region = sheet.getMergedRegion(i);
            if (!region.isInRange(rowIndex, columnIndex)) continue;
            Row firstRow = sheet.getRow(region.getFirstRow());
            Cell firstCell = firstRow == null ? null : firstRow.getCell(region.getFirstColumn());
            return firstCell == null ? "" : formatter.formatCellValue(firstCell).trim();
        }
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }

    private static HeaderDetection detectHeaders(List<List<String>> matrix) {
        int scanRows = Math.min(matrix.size(), 30);
        Map<String, Integer> indexes = new LinkedHashMap<>();
        Map<String, String> mappings = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        int headerRow = -1;
        for (int rowIndex = 0; rowIndex < scanRows; rowIndex++) {
            List<String> row = matrix.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                String raw = row.get(columnIndex);
                String key = canonicalHeader(raw);
                if (key == null) continue;
                if (indexes.containsKey(key)) {
                    if (!Objects.equals(indexes.get(key), columnIndex)) {
                        warnings.add("检测到重复表头“" + raw + "”，已保留第 " + (indexes.get(key) + 1) + " 列");
                    }
                    continue;
                }
                indexes.put(key, columnIndex);
                mappings.put(displayName(key), isBlank(raw) ? "第 " + (columnIndex + 1) + " 列" : raw);
                headerRow = Math.max(headerRow, rowIndex);
            }
        }
        List<String> required = List.of("gradeName", "clazzName", "studentCode", "studentName");
        for (String key : required) if (!indexes.containsKey(key)) warnings.add("未识别必需列：“" + displayName(key) + "”");
        boolean hasSubject = SUBJECT_KEYS.stream().anyMatch(indexes::containsKey);
        if (!hasSubject) warnings.add("未识别到任何科目成绩列，请检查科目表头或先使用系统模板");
        Map<String, String> availableColumns = new LinkedHashMap<>();
        int maxColumns = matrix.stream().mapToInt(List::size).max().orElse(0);
        for (int columnIndex = 0; columnIndex < maxColumns; columnIndex++) {
            String label = "";
            int labelEndRow = headerRow >= 0 ? headerRow : Math.max(0, scanRows - 1);
            for (int rowIndex = 0; rowIndex <= labelEndRow && rowIndex < matrix.size(); rowIndex++) {
                String candidate = matrix.get(rowIndex).get(columnIndex);
                if (!isBlank(candidate)) label = candidate;
            }
            availableColumns.put(String.valueOf(columnIndex), isBlank(label) ? "第 " + (columnIndex + 1) + " 列" : label);
        }
        return new HeaderDetection(indexes, mappings, availableColumns, warnings, headerRow, warnings.stream().noneMatch(it -> it.startsWith("未识别必需列")) && hasSubject);
    }

    private static ScoreImportVO toImportRow(List<String> values, Map<String, Integer> indexes) {
        ScoreImportVO row = new ScoreImportVO();
        row.setGradeName(valueAt(values, indexes.get("gradeName")));
        row.setClazzName(valueAt(values, indexes.get("clazzName")));
        row.setStudentCode(valueAt(values, indexes.get("studentCode")));
        row.setStudentName(valueAt(values, indexes.get("studentName")));
        row.setChineseScore(valueAt(values, indexes.get("chineseScore")));
        row.setMathScore(valueAt(values, indexes.get("mathScore")));
        row.setEnglishScore(valueAt(values, indexes.get("englishScore")));
        row.setPhysicsScore(valueAt(values, indexes.get("physicsScore")));
        row.setChemistryScore(valueAt(values, indexes.get("chemistryScore")));
        row.setOrganismScore(valueAt(values, indexes.get("organismScore")));
        row.setGeographyScore(valueAt(values, indexes.get("geographyScore")));
        row.setHistoryScore(valueAt(values, indexes.get("historyScore")));
        row.setPoliticsScore(valueAt(values, indexes.get("politicsScore")));
        return row;
    }

    private static String valueAt(List<String> values, Integer index) {
        return index == null || index < 0 || index >= values.size() ? "" : values.get(index);
    }

    private static String canonicalHeader(String raw) {
        String value = normalizeHeader(raw);
        if (value.isEmpty()) return null;
        Map<String, List<String>> aliases = HEADER_ALIASES;
        for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
            for (String alias : entry.getValue()) {
                String normalizedAlias = normalizeHeader(alias);
                if (value.equals(normalizedAlias)) return entry.getKey();
                String stripped = stripScoreSuffix(value);
                if (stripped.equals(normalizedAlias)) return entry.getKey();
            }
        }
        return null;
    }

    private static String normalizeHeader(String value) {
        return value == null ? "" : value.trim().replaceAll("[^\\p{IsHan}A-Za-z]", "").toLowerCase(Locale.ROOT);
    }

    private static String stripScoreSuffix(String value) {
        String result = value;
        for (String suffix : List.of("成绩", "分数", "得分", "原始分", "卷面分")) {
            if (result.endsWith(suffix)) result = result.substring(0, result.length() - suffix.length());
        }
        return result;
    }

    private static String displayName(String key) {
        return switch (key) {
            case "gradeName" -> "年级"; case "clazzName" -> "班级"; case "studentCode" -> "学号"; case "studentName" -> "姓名";
            case "chineseScore" -> "语文"; case "mathScore" -> "数学"; case "englishScore" -> "英语"; case "physicsScore" -> "物理";
            case "chemistryScore" -> "化学"; case "organismScore" -> "生物"; case "geographyScore" -> "地理"; case "historyScore" -> "历史";
            case "politicsScore" -> "政治/道德与法治"; default -> key;
        };
    }

    private static final List<String> SUBJECT_KEYS = List.of("chineseScore", "mathScore", "englishScore", "physicsScore", "chemistryScore", "organismScore", "geographyScore", "historyScore", "politicsScore");
    private static final Map<String, List<String>> HEADER_ALIASES = new LinkedHashMap<>();
    static {
        HEADER_ALIASES.put("gradeName", List.of("年级", "年级名称", "年段"));
        HEADER_ALIASES.put("clazzName", List.of("班级", "班级名称", "行政班", "教学班"));
        HEADER_ALIASES.put("studentCode", List.of("学号", "学生学号", "考号", "考生号", "学生编号", "学籍号"));
        HEADER_ALIASES.put("studentName", List.of("姓名", "学生姓名", "考生姓名", "学生名称"));
        HEADER_ALIASES.put("chineseScore", List.of("语文")); HEADER_ALIASES.put("mathScore", List.of("数学"));
        HEADER_ALIASES.put("englishScore", List.of("英语")); HEADER_ALIASES.put("physicsScore", List.of("物理"));
        HEADER_ALIASES.put("chemistryScore", List.of("化学")); HEADER_ALIASES.put("organismScore", List.of("生物"));
        HEADER_ALIASES.put("geographyScore", List.of("地理")); HEADER_ALIASES.put("historyScore", List.of("历史"));
        HEADER_ALIASES.put("politicsScore", List.of("政治", "道德与法治", "道法"));
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
    private record WorkbookData(List<ParsedRow> rows, String sheetName, int headerRowNumber,
                                Map<String, Integer> columnIndexes, Map<String, String> headerMappings,
                                Map<String, String> availableColumns, List<String> mappingWarnings,
                                boolean mappingComplete) { }
    private record HeaderDetection(Map<String, Integer> columnIndexes, Map<String, String> headerMappings,
                                   Map<String, String> availableColumns, List<String> warnings,
                                   int headerRowNumber, boolean mappingComplete) {
        private HeaderDetection withCustomMapping(Map<String, Integer> customMapping) {
            Map<String, Integer> indexes = new LinkedHashMap<>(customMapping);
            Map<String, String> mappings = new LinkedHashMap<>();
            List<String> nextWarnings = new ArrayList<>();
            Map<Integer, String> usedColumns = new HashMap<>();
            for (Map.Entry<String, Integer> entry : indexes.entrySet()) {
                if (!HEADER_ALIASES.containsKey(entry.getKey())) {
                    nextWarnings.add("不支持的映射字段：" + entry.getKey());
                    continue;
                }
                String label = availableColumns.get(String.valueOf(entry.getValue()));
                if (label == null) nextWarnings.add("映射列不存在：" + displayName(entry.getKey()));
                String previous = usedColumns.put(entry.getValue(), entry.getKey());
                if (previous != null && !previous.equals(entry.getKey())) {
                    nextWarnings.add("第 " + (entry.getValue() + 1) + " 列同时映射了“" + displayName(previous) + "”和“" + displayName(entry.getKey()) + "”");
                }
                mappings.put(displayName(entry.getKey()), label == null ? "第 " + (entry.getValue() + 1) + " 列" : label);
            }
            for (String key : List.of("gradeName", "clazzName", "studentCode", "studentName")) {
                if (!indexes.containsKey(key)) nextWarnings.add("未映射必需列：“" + displayName(key) + "”");
            }
            boolean hasSubject = SUBJECT_KEYS.stream().anyMatch(indexes::containsKey);
            if (!hasSubject) nextWarnings.add("未映射任何科目成绩列");
            return new HeaderDetection(indexes, mappings, availableColumns, nextWarnings,
                    headerRowNumber, nextWarnings.isEmpty());
        }
    }
    private record RowResult(boolean valid, int blankCells) { }
    private record PendingPreview(ScoreImportPreviewVO preview, Instant createdAt, List<ScoreImportChangeVO> changes) { }
    private record CellValue(Double score, String status, boolean blank, String error) {
        private static CellValue blankValue() { return new CellValue(null, null, true, null); }
        private boolean invalid() { return !blank && status == null && error != null; }
    }
}
