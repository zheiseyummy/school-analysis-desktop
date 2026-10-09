package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Independent score-file analysis. It deliberately has no dependency on sys_student/sys_clazz. */
@RestController
@RequestMapping("/api/v1/local-score-analysis")
@RequiredArgsConstructor
public class LocalScoreAnalysisController {
    private static final long PREVIEW_TTL_MILLIS = 30 * 60 * 1000L;
    private static final Map<String, String> SUBJECTS = Map.ofEntries(
            Map.entry("语文", "语文"), Map.entry("数学", "数学"), Map.entry("英语", "英语"),
            Map.entry("外语", "英语"), Map.entry("物理", "物理"), Map.entry("化学", "化学"),
            Map.entry("生物", "生物"), Map.entry("生物学", "生物"), Map.entry("地理", "地理"),
            Map.entry("历史", "历史"), Map.entry("政治", "道德与法治"),
            Map.entry("道法", "道德与法治"), Map.entry("政治道法", "道德与法治"),
            Map.entry("道法政治", "道德与法治"), Map.entry("政治与法治", "道德与法治"),
            Map.entry("道德与法治", "道德与法治"),
            Map.entry("思想政治", "思想政治"), Map.entry("体育", "体育")
    );

    private final JdbcTemplate jdbc;
    private final Map<String, PendingImport> previews = new ConcurrentHashMap<>();

    @GetMapping("/datasets")
    public Result<List<Map<String, Object>>> datasets() {
        return Result.success(jdbc.queryForList("""
                SELECT d.id, d.name, d.stage, d.grade_name AS gradeName, d.class_name AS className,
                  d.created_at AS createdAt,
                  (SELECT COUNT(*) FROM local_score_exam e WHERE e.dataset_id=d.id) AS examCount,
                  (SELECT COUNT(*) FROM local_score_student s WHERE s.dataset_id=d.id) AS studentCount
                FROM local_score_dataset d ORDER BY d.id DESC
                """));
    }

    /** Parse and validate first. No database table is changed until the user confirms. */
    @PostMapping("/preview")
    public Result<Map<String, Object>> preview(@RequestParam String datasetName,
                                                @RequestParam(required = false) String stage,
                                                @RequestParam String gradeName,
                                                @RequestParam String className,
                                                @RequestParam String examName,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        requireText(datasetName, "请输入分析数据集名称");
        requireText(gradeName, "请选择或填写年级");
        requireText(className, "请选择或填写班级");
        requireText(examName, "请输入考试名称");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择成绩 Excel 文件");

        List<Map<String, Object>> sameName = jdbc.queryForList("SELECT stage,grade_name,class_name FROM local_score_dataset WHERE name=?", datasetName.trim());
        if (!sameName.isEmpty()) {
            Map<String, Object> existing = sameName.get(0);
            if (!Objects.equals(clean(stage), clean(existing.get("stage")))
                    || !gradeName.trim().equals(clean(existing.get("grade_name")))
                    || !className.trim().equals(clean(existing.get("class_name")))) {
                throw new IllegalArgumentException("同名分析数据集已存在，但年级、班级或学段不同");
            }
        }

        ParsedWorkbook parsed;
        try (InputStream input = file.getInputStream()) {
            parsed = parse(input);
        }
        List<String> errors = validateRows(parsed.rows());
        if (parsed.rows().isEmpty()) errors.add("没有识别到有效学生行");
        String token = UUID.randomUUID().toString();
        PendingImport pending = new PendingImport(token, Instant.now().toEpochMilli(), datasetName.trim(), clean(stage),
                gradeName.trim(), className.trim(), examName.trim(), file.getOriginalFilename(), parsed, errors);
        previews.put(token, pending);
        purgeExpired();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("datasetName", pending.datasetName());
        result.put("examName", pending.examName());
        result.put("fileName", pending.fileName());
        result.put("sheetName", parsed.sheetName());
        result.put("headerRow", parsed.headerRow());
        result.put("rowCount", parsed.rows().size());
        result.put("studentCodePresent", parsed.studentCodePresent());
        result.put("subjects", parsed.subjects());
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        result.put("confirmable", errors.isEmpty());
        return Result.success(result);
    }

    @Transactional
    @PostMapping("/confirm")
    public Result<Map<String, Object>> confirm(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        PendingImport pending = previews.remove(token);
        if (pending == null || pending.createdAt() + PREVIEW_TTL_MILLIS < System.currentTimeMillis()) {
            throw new IllegalArgumentException("导入预览已失效，请重新选择文件");
        }
        if (!pending.errors().isEmpty()) throw new IllegalArgumentException("导入预览有错误，请先修正后重新上传");

        Long datasetId = findId("SELECT id FROM local_score_dataset WHERE name=?", pending.datasetName());
        if (datasetId == null) {
            datasetId = insert("INSERT INTO local_score_dataset(name,stage,grade_name,class_name,created_at) VALUES(?,?,?,?,?)",
                    pending.datasetName(), pending.stage(), pending.gradeName(), pending.className(), Instant.now().toString());
        }
        Long examId = findId("SELECT id FROM local_score_exam WHERE dataset_id=? AND name=?", datasetId, pending.examName());
        if (examId == null) {
            examId = insert("INSERT INTO local_score_exam(dataset_id,name,source_file,imported_at) VALUES(?,?,?,?)",
                    datasetId, pending.examName(), pending.fileName(), Instant.now().toString());
        } else {
            jdbc.update("UPDATE local_score_exam SET source_file=?,imported_at=? WHERE id=?", pending.fileName(), Instant.now().toString(), examId);
            jdbc.update("DELETE FROM local_score_value WHERE exam_id=?", examId);
        }

        int valueCount = 0;
        Set<Long> touchedStudents = new LinkedHashSet<>();
        for (ParsedRow row : pending.parsed().rows()) {
            long studentId = resolveStudent(datasetId, row.code(), row.name());
            touchedStudents.add(studentId);
            for (Map.Entry<String, ParsedValue> value : row.values().entrySet()) {
                ParsedValue parsedValue = value.getValue();
                jdbc.update("INSERT INTO local_score_value(exam_id,student_id,subject,score,status) VALUES(?,?,?,?,?)",
                        examId, studentId, value.getKey(), parsedValue.score(), parsedValue.status());
                valueCount++;
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("datasetId", datasetId);
        response.put("examId", examId);
        response.put("students", touchedStudents.size());
        response.put("subjects", pending.parsed().subjects().size());
        response.put("values", valueCount);
        response.put("reimported", pending.parsed().rows().size());
        return Result.success(response);
    }

    @GetMapping("/datasets/{datasetId}/analysis")
    public Result<Map<String, Object>> analysis(@PathVariable long datasetId) {
        assertDataset(datasetId);
        List<Map<String, Object>> exams = jdbc.queryForList("SELECT id,name,source_file AS sourceFile,imported_at AS importedAt FROM local_score_exam WHERE dataset_id=? ORDER BY id", datasetId);
        for (Map<String, Object> exam : exams) {
            long examId = ((Number) exam.get("id")).longValue();
            exam.put("studentCount", jdbc.queryForObject("SELECT COUNT(DISTINCT student_id) FROM local_score_value WHERE exam_id=?", Integer.class, examId));
            exam.put("subjectStats", jdbc.queryForList("""
                    SELECT subject, COUNT(CASE WHEN status='NORMAL' THEN 1 END) AS scoredCount,
                      AVG(CASE WHEN status='NORMAL' THEN score END) AS average,
                      MAX(CASE WHEN status='NORMAL' THEN score END) AS highest,
                      MIN(CASE WHEN status='NORMAL' THEN score END) AS lowest,
                      SUM(CASE WHEN status='ABSENT' THEN 1 ELSE 0 END) AS absentCount,
                      SUM(CASE WHEN status='UNSELECTED' THEN 1 ELSE 0 END) AS unselectedCount,
                      SUM(CASE WHEN status='MISSING' THEN 1 ELSE 0 END) AS missingCount
                    FROM local_score_value WHERE exam_id=? GROUP BY subject ORDER BY subject
                    """, examId));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dataset", jdbc.queryForMap("SELECT id,name,stage,grade_name AS gradeName,class_name AS className FROM local_score_dataset WHERE id=?", datasetId));
        result.put("exams", exams);
        return Result.success(result);
    }

    @GetMapping("/datasets/{datasetId}/exams/{examId}/students")
    public Result<Map<String, Object>> examStudents(@PathVariable long datasetId, @PathVariable long examId) {
        assertDataset(datasetId);
        Long belongs = findId("SELECT id FROM local_score_exam WHERE id=? AND dataset_id=?", examId, datasetId);
        if (belongs == null) throw new IllegalArgumentException("考试不属于所选分析数据集");
        List<Map<String, Object>> students = jdbc.queryForList("""
                SELECT DISTINCT s.id AS studentId,s.student_code AS code,s.name
                FROM local_score_student s JOIN local_score_value v ON v.student_id=s.id
                WHERE s.dataset_id=? AND v.exam_id=? ORDER BY s.student_code,s.name
                """, datasetId, examId);
        List<Map<String, Object>> values = jdbc.queryForList("SELECT student_id AS studentId,subject,score,status FROM local_score_value WHERE exam_id=? ORDER BY student_id,subject", examId);
        Map<Long, Map<String, Object>> byStudent = new HashMap<>();
        for (Map<String, Object> student : students) {
            Map<String, Object> scores = new LinkedHashMap<>();
            student.put("scores", scores);
            byStudent.put(((Number) student.get("studentId")).longValue(), student);
        }
        for (Map<String, Object> value : values) {
            Map<String, Object> student = byStudent.get(((Number) value.get("studentId")).longValue());
            if (student != null) {
                @SuppressWarnings("unchecked") Map<String, Object> scores = (Map<String, Object>) student.get("scores");
                scores.put(clean(value.get("subject")), Map.of("score", value.get("score") == null ? "" : value.get("score"), "status", value.get("status")));
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("students", students);
        result.put("subjects", jdbc.queryForList("SELECT DISTINCT subject FROM local_score_value WHERE exam_id=? ORDER BY subject", examId).stream().map(row -> row.get("subject")).toList());
        return Result.success(result);
    }

    private long resolveStudent(long datasetId, String code, String name) {
        Long id = isBlank(code) ? null : findId("SELECT id FROM local_score_student WHERE dataset_id=? AND student_code=?", datasetId, code);
        if (id == null) {
            List<Map<String, Object>> nameMatches = jdbc.queryForList("SELECT id,student_code FROM local_score_student WHERE dataset_id=? AND name=?", datasetId, name);
            if (nameMatches.size() > 1) throw new IllegalArgumentException("分析数据集中存在同名学生“" + name + "”，不能安全合并");
            if (nameMatches.size() == 1) {
                id = ((Number) nameMatches.get(0).get("id")).longValue();
                String storedCode = clean(nameMatches.get(0).get("student_code"));
                if (!isBlank(code) && !isBlank(storedCode) && !code.equals(storedCode)) {
                    throw new IllegalArgumentException("同名学生的学号不一致，不能自动合并：" + name);
                }
                if (isBlank(storedCode) && !isBlank(code)) jdbc.update("UPDATE local_score_student SET student_code=? WHERE id=?", code, id);
                return id;
            }
        }
        if (id != null) return id;
        return insert("INSERT INTO local_score_student(dataset_id,student_code,name,created_at) VALUES(?,?,?,?)",
                datasetId, isBlank(code) ? null : code, name, Instant.now().toString());
    }

    private static ParsedWorkbook parse(InputStream input) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(input)) {
            if (workbook.getNumberOfSheets() == 0) throw new IllegalArgumentException("Excel 中没有工作表");
            DataFormatter formatter = new DataFormatter();
            Sheet selected = null;
            Header header = null;
            for (int s = 0; s < workbook.getNumberOfSheets() && selected == null; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                for (int row = 0; row <= Math.min(sheet.getLastRowNum(), 30); row++) {
                    Header candidate = detectHeader(sheet, row, formatter);
                    if (candidate != null) { selected = sheet; header = candidate; break; }
                }
            }
            if (selected == null || header == null) throw new IllegalArgumentException("未识别到含姓名和科目成绩的表头");
            List<ParsedRow> rows = new ArrayList<>();
            for (int r = header.row() + 1; r <= selected.getLastRowNum(); r++) {
                Row source = selected.getRow(r);
                if (source == null) continue;
                String name = textAt(selected, source, header.nameColumn(), formatter);
                String code = header.codeColumn() == null ? "" : textAt(selected, source, header.codeColumn(), formatter);
                if (name.isBlank() && code.isBlank()) continue;
                if (name.isBlank()) throw new IllegalArgumentException("第" + (r + 1) + "行缺少姓名");
                Map<String, ParsedValue> values = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> subject : header.subjectColumns().entrySet()) {
                    String raw = textAt(selected, source, subject.getValue(), formatter);
                    values.put(subject.getKey(), parseValue(raw, r + 1, subject.getKey()));
                }
                rows.add(new ParsedRow(r + 1, code, name, values));
            }
            return new ParsedWorkbook(selected.getSheetName(), header.row() + 1, header.codeColumn() != null,
                    List.copyOf(header.subjectColumns().keySet()), rows);
        }
    }

    private static Header detectHeader(Sheet sheet, int rowIndex, DataFormatter formatter) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return null;
        Integer code = null, name = null;
        Map<String, Integer> subjects = new LinkedHashMap<>();
        int max = Math.max(0, row.getLastCellNum());
        for (int col = 0; col < max; col++) {
            String value = normalizeHeader(textAt(sheet, row, col, formatter));
            if (value.isBlank()) continue;
            if (name == null && List.of("姓名", "学生姓名", "考生姓名").contains(value)) name = col;
            if (code == null && List.of("学号", "学生学号", "考号", "考生号", "学生编号", "学籍号").contains(value)) code = col;
            String subject = SUBJECTS.get(value);
            if (subject != null) subjects.putIfAbsent(subject, col);
        }
        return name != null && !subjects.isEmpty() ? new Header(rowIndex, code, name, subjects) : null;
    }

    private static ParsedValue parseValue(String raw, int row, String subject) {
        if (raw.isBlank()) return new ParsedValue(null, "MISSING");
        String normalized = raw.trim().replace("，", "").replace(",", "");
        if (normalized.equalsIgnoreCase("缺考")) return new ParsedValue(null, "ABSENT");
        if (List.of("未选", "未选科", "未选择").contains(normalized)) return new ParsedValue(null, "UNSELECTED");
        try {
            double score = Double.parseDouble(normalized);
            if (!Double.isFinite(score) || score < 0) throw new NumberFormatException();
            return new ParsedValue(score, "NORMAL");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("第" + row + "行“" + subject + "”不是有效分数或已知状态");
        }
    }

    private static List<String> validateRows(List<ParsedRow> rows) {
        List<String> errors = new ArrayList<>();
        Set<String> codes = new HashSet<>();
        Set<String> namesWithoutCode = new HashSet<>();
        for (ParsedRow row : rows) {
            if (!isBlank(row.code()) && !codes.add(row.code())) errors.add("第" + row.rowNumber() + "行学号重复");
            if (isBlank(row.code()) && !namesWithoutCode.add(row.name())) errors.add("第" + row.rowNumber() + "行无学号且姓名重复，无法安全对应学生");
        }
        return errors;
    }

    private static String textAt(Sheet sheet, Row row, int column, DataFormatter formatter) {
        Cell cell = row.getCell(column);
        if (cell != null) {
            String raw = formatter.formatCellValue(cell).trim();
            if (!raw.isBlank()) return raw;
        }
        for (int index = 0; index < sheet.getNumMergedRegions(); index++) {
            CellRangeAddress merged = sheet.getMergedRegion(index);
            if (!merged.isInRange(row.getRowNum(), column)) continue;
            Row firstRow = sheet.getRow(merged.getFirstRow());
            Cell firstCell = firstRow == null ? null : firstRow.getCell(merged.getFirstColumn());
            return firstCell == null ? "" : formatter.formatCellValue(firstCell).trim();
        }
        return "";
    }

    private static String normalizeHeader(String value) { return value == null ? "" : value.replaceAll("[\\s　()（）:：/_-]", "").trim(); }
    private static String clean(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static boolean isBlank(String value) { return value == null || value.isBlank(); }
    private static void requireText(String value, String message) { if (isBlank(value)) throw new IllegalArgumentException(message); }

    private void assertDataset(long id) {
        if (findId("SELECT id FROM local_score_dataset WHERE id=?", id) == null) throw new IllegalArgumentException("分析数据集不存在");
    }

    private Long findId(String sql, Object... args) {
        List<Long> rows = jdbc.query(sql, (rs, rowNum) -> rs.getLong(1), args);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private long insert(String sql, Object... args) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int index = 0; index < args.length; index++) statement.setObject(index + 1, args[index]);
            return statement;
        }, key);
        Number id = key.getKey();
        if (id == null) throw new IllegalStateException("本地数据写入失败：没有生成记录编号");
        return id.longValue();
    }

    private void purgeExpired() {
        long now = System.currentTimeMillis();
        previews.entrySet().removeIf(entry -> entry.getValue().createdAt() + PREVIEW_TTL_MILLIS < now);
    }

    private record Header(int row, Integer codeColumn, int nameColumn, Map<String, Integer> subjectColumns) { }
    private record ParsedValue(Double score, String status) { }
    private record ParsedRow(int rowNumber, String code, String name, Map<String, ParsedValue> values) { }
    private record ParsedWorkbook(String sheetName, int headerRow, boolean studentCodePresent, List<String> subjects, List<ParsedRow> rows) { }
    private record PendingImport(String token, long createdAt, String datasetName, String stage, String gradeName,
                                 String className, String examName, String fileName, ParsedWorkbook parsed, List<String> errors) { }
}
