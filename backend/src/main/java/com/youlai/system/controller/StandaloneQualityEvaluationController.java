package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.QualityScoring;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

/** Workbook-only comprehensive-quality evaluation; all identifiers are local to this module. */
@RestController
@RequestMapping("/api/v1/quality-independent")
@RequiredArgsConstructor
public class StandaloneQualityEvaluationController {
    private static final String FINAL_SEMESTER = "junior_3_2";
    private final JdbcTemplate jdbc;

    @GetMapping("/datasets")
    public Result<List<Map<String, Object>>> datasets() {
        return Result.success(jdbc.queryForList("""
                SELECT d.id,d.name,d.source_file AS sourceFile,d.created_at AS createdAt,
                  (SELECT COUNT(*) FROM local_quality_student s WHERE s.dataset_id=d.id) AS studentCount,
                  (SELECT COUNT(*) FROM local_quality_final_scope f WHERE f.dataset_id=d.id) AS baselineStudentCount,
                  COALESCE(d.is_locked,0) AS isLocked
                FROM local_quality_dataset d ORDER BY d.id DESC
                """));
    }

    /** Upload a workbook directly. Workbook student names/IDs become the module's own roster. */
    @Transactional
    @PostMapping("/import")
    public Result<Map<String, Object>> importWorkbook(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return Result.failed("请选择综合素质评价工作簿");
        String sourceFile = Objects.requireNonNullElse(file.getOriginalFilename(), "综合素质评价.xlsx");
        String datasetName = sourceFile.replaceFirst("(?i)\\.(xlsx|xls)$", "");
        ParsedQuality parsed;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            parsed = parseWorkbook(workbook);
        }
        if (parsed.rows().isEmpty()) return Result.failed("工作簿中没有识别到包含学生和评价维度的数据行");
        if (parsed.studentsWithoutIdHaveDuplicateNames()) return Result.failed("存在无学号且姓名重复的学生，无法安全对应，请补充学号或先修正工作簿");

        Long datasetId = findId("SELECT id FROM local_quality_dataset WHERE name=?", datasetName);
        if (datasetId == null) {
            datasetId = insert("INSERT INTO local_quality_dataset(name,source_file,created_at) VALUES(?,?,?)",
                    datasetName, sourceFile, LocalDateTime.now().toString());
        } else {
            jdbc.update("UPDATE local_quality_dataset SET source_file=?,is_locked=0,locked_at=NULL,generated_at=NULL,workflow_version=0 WHERE id=?", sourceFile, datasetId);
            jdbc.update("DELETE FROM local_quality_final_result WHERE dataset_id=?", datasetId);
            jdbc.update("DELETE FROM local_quality_score_confirmation WHERE dataset_id=?", datasetId);
        }

        int matchedRows = 0;
        Set<Long> baselineIds = new LinkedHashSet<>();
        Set<String> importedSemesters = new LinkedHashSet<>();
        for (QualityRow row : parsed.rows()) {
            Long studentId = resolveStudent(datasetId, row.code(), row.name());
            if (studentId == null) continue;
            matchedRows++;
            importedSemesters.add(row.semester());
            for (String dimension : QualityScoring.DIMENSIONS) {
                saveRecord(datasetId, studentId, row.semester(), dimension, row.levels().getOrDefault(dimension, "N/A"));
            }
            ensureRoster(datasetId, studentId, row.semester());
            if (FINAL_SEMESTER.equals(row.semester())) baselineIds.add(studentId);
        }
        List<String> issues = new ArrayList<>(parsed.issues());
        if (!importedSemesters.contains(FINAL_SEMESTER)) {
            issues.add("未找到最后一个学期工作表，最终人数范围未更新");
        } else {
            jdbc.update("DELETE FROM local_quality_final_scope WHERE dataset_id=?", datasetId);
            jdbc.update("DELETE FROM local_quality_missing_review WHERE dataset_id=? AND student_id NOT IN (SELECT student_id FROM local_quality_final_scope WHERE dataset_id=?)", datasetId, datasetId);
            for (Long studentId : baselineIds) {
                jdbc.update("INSERT INTO local_quality_final_scope(dataset_id,student_id,source_sheet) VALUES(?,?,?)", datasetId, studentId, FINAL_SEMESTER);
                ensureMissingReviews(datasetId, studentId);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("datasetId", datasetId);
        result.put("datasetName", datasetName);
        result.put("totalRows", parsed.rows().size());
        result.put("matchedRows", matchedRows);
        result.put("baselineStudentCount", baselineIds.size());
        result.put("semesterSheets", parsed.semesters());
        result.put("issues", issues);
        return Result.success(result);
    }

    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        return Result.success(Map.of("dimensions", QualityScoring.DIMENSIONS, "semesters", QualityScoring.SEMESTERS,
                "semesterMaximums", QualityScoring.semesterMaximums(), "entryLevels", List.of("A", "B", "C", "N/A"), "finalLevels", List.of("A", "B", "C")));
    }

    @GetMapping("/datasets/{datasetId}/students")
    public Result<List<Map<String, Object>>> students(@PathVariable long datasetId) {
        requireDataset(datasetId);
        return Result.success(jdbc.queryForList("""
                SELECT s.id AS studentId,s.source_code AS code,s.name,
                  (SELECT COUNT(DISTINCT r.semester) FROM local_quality_roster_entry r WHERE r.dataset_id=s.dataset_id AND r.student_id=s.id) AS completedSemesters,
                  CASE WHEN EXISTS(SELECT 1 FROM local_quality_final_scope f WHERE f.dataset_id=s.dataset_id AND f.student_id=s.id) THEN 1 ELSE 0 END AS inFinalScope,
                  (SELECT COUNT(*) FROM local_quality_missing_review m WHERE m.dataset_id=s.dataset_id AND m.student_id=s.id AND m.status='PENDING') AS pendingMissingCount
                FROM local_quality_student s WHERE s.dataset_id=? ORDER BY s.source_code,s.name
                """, datasetId));
    }

    @GetMapping("/datasets/{datasetId}/students/{studentId}/records")
    public Result<List<Map<String, Object>>> studentRecords(@PathVariable long datasetId, @PathVariable long studentId) {
        requireStudent(datasetId, studentId);
        return Result.success(jdbc.queryForList("SELECT semester,dimension,level_or_score AS level FROM local_quality_record WHERE dataset_id=? AND student_id=? ORDER BY semester,dimension", datasetId, studentId));
    }

    @PostMapping("/datasets/{datasetId}/students/{studentId}/semester/{semester}")
    @Transactional
    public Result<Void> saveSemester(@PathVariable long datasetId, @PathVariable long studentId,
                                    @PathVariable String semester, @RequestBody Map<String, String> ratings) {
        requireDataset(datasetId);
        requireStudent(datasetId, studentId);
        if (!Arrays.asList(QualityScoring.SEMESTERS).contains(semester)) return Result.failed("评价学期无效");
        if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND is_locked=1", datasetId) > 0) return Result.failed("最终评定已锁定，请先解锁再修改学期评价");
        Map<String, String> validated = new LinkedHashMap<>();
        for (String dimension : QualityScoring.DIMENSIONS) {
            String level = Objects.requireNonNullElse(ratings.get(dimension), "N/A").trim().toUpperCase();
            if (!List.of("A", "B", "C", "N/A").contains(level)) return Result.failed("评价等级无效");
            validated.put(dimension, level);
        }
        Set<String> changedDimensions = new HashSet<>();
        for (String dimension : QualityScoring.DIMENSIONS) {
            String level = validated.get(dimension);
            List<String> previous = jdbc.query("SELECT level_or_score FROM local_quality_record WHERE dataset_id=? AND student_id=? AND semester=? AND dimension=?", (rs, row) -> rs.getString(1), datasetId, studentId, semester, dimension);
            if (previous.isEmpty() || !level.equals(previous.get(0))) changedDimensions.add(dimension);
            saveRecord(datasetId, studentId, semester, dimension, level);
        }
        ensureRoster(datasetId, studentId, semester);
        ensureMissingReviews(datasetId, studentId);
        if (!changedDimensions.isEmpty()) {
            for (String dimension : changedDimensions) jdbc.update("DELETE FROM local_quality_score_confirmation WHERE dataset_id=? AND student_id=? AND dimension=?", datasetId, studentId, dimension);
            jdbc.update("DELETE FROM local_quality_final_result WHERE dataset_id=?", datasetId);
            jdbc.update("UPDATE local_quality_dataset SET generated_at=NULL,workflow_version=0 WHERE id=?", datasetId);
        }
        return Result.success();
    }

    @GetMapping("/datasets/{datasetId}/students/{studentId}/summary")
    public Result<Map<String, Object>> studentSummary(@PathVariable long datasetId, @PathVariable long studentId) {
        requireStudent(datasetId, studentId);
        List<Map<String, Object>> records = jdbc.queryForList("SELECT semester,dimension,level_or_score AS level FROM local_quality_record WHERE dataset_id=? AND student_id=?", datasetId, studentId);
        Map<String, Double> totals = new LinkedHashMap<>();
        for (String dimension : QualityScoring.DIMENSIONS) {
            totals.put(dimension, records.stream().filter(row -> dimension.equals(row.get("dimension")))
                    .mapToDouble(row -> QualityScoring.score(String.valueOf(row.get("semester")), String.valueOf(row.get("level")))).sum());
        }
        return Result.success(Map.of("records", records, "dimensionTotals", totals));
    }

    @GetMapping("/datasets/{datasetId}/final")
    public Result<Map<String, Object>> finalResults(@PathVariable long datasetId) {
        requireDataset(datasetId);
        Map<String, Object> state = jdbc.queryForMap("SELECT COALESCE(is_locked,0) AS isLocked,COALESCE(workflow_version,0) AS workflowVersion,generated_at AS generatedAt,COALESCE(a_ratio,0.60) AS aRatio,COALESCE(b_ratio,0.35) AS bRatio,COALESCE(c_ratio,0.05) AS cRatio,locked_at AS lockedAt FROM local_quality_dataset WHERE id=?", datasetId);
        boolean currentWorkflow = number(state.get("workflowVersion")) == 1;
        List<Map<String, Object>> rows = currentWorkflow ? jdbc.queryForList("""
                SELECT r.student_id AS studentId,s.source_code AS code,s.name,r.dimension,
                  r.calculated_score AS calculatedScore,r.cumulative_score AS cumulativeScore,
                  r.class_rank AS classRank,r.automatic_level AS automaticLevel,r.final_level AS finalLevel,
                  r.available_terms AS availableTerms,r.contains_na AS containsNa,r.score_confirmed AS scoreConfirmed
                FROM local_quality_final_result r JOIN local_quality_student s ON s.id=r.student_id
                WHERE r.dataset_id=? ORDER BY s.source_code,s.name,r.dimension
                """, datasetId) : List.of();
        int baseline = count("SELECT COUNT(*) FROM local_quality_final_scope WHERE dataset_id=?", datasetId);
        int pending = count("SELECT COUNT(*) FROM local_quality_missing_review WHERE dataset_id=? AND status='PENDING'", datasetId);
        int pendingStudents = count("SELECT COUNT(DISTINCT student_id) FROM local_quality_missing_review WHERE dataset_id=? AND status='PENDING'", datasetId);
        int scored = currentWorkflow ? count("SELECT COUNT(DISTINCT student_id) FROM local_quality_final_result WHERE dataset_id=? AND automatic_level<>'N/A'", datasetId) : 0;
        int pendingScores = currentWorkflow ? count("SELECT COUNT(*) FROM local_quality_final_result WHERE dataset_id=? AND contains_na=1 AND score_confirmed=0", datasetId) : 0;
        int pendingScoreStudents = currentWorkflow ? count("SELECT COUNT(DISTINCT student_id) FROM local_quality_final_result WHERE dataset_id=? AND contains_na=1 AND score_confirmed=0", datasetId) : 0;
        return Result.success(Map.of("state", state, "rows", rows, "baselineStudentCount", baseline,
                "pendingReviewCount", pending, "pendingStudentCount", pendingStudents, "scoredStudentCount", scored,
                "pendingScoreCount", pendingScores, "pendingScoreStudentCount", pendingScoreStudents));
    }

    @GetMapping("/datasets/{datasetId}/final/missing-reviews")
    public Result<List<Map<String, Object>>> missingReviews(@PathVariable long datasetId) {
        requireDataset(datasetId);
        return Result.success(jdbc.queryForList("""
                SELECT m.student_id AS studentId,s.source_code AS code,s.name,m.semester,m.status,
                  m.missing_dimensions AS missingDimensions,m.remark,m.updated_at AS updatedAt
                FROM local_quality_missing_review m JOIN local_quality_student s ON s.id=m.student_id
                WHERE m.dataset_id=? ORDER BY CASE m.status WHEN 'PENDING' THEN 0 ELSE 1 END,s.source_code,m.semester
                """, datasetId));
    }

    @PutMapping("/datasets/{datasetId}/final/missing-reviews/{studentId}/{semester}")
    public Result<Void> updateMissingReview(@PathVariable long datasetId, @PathVariable long studentId,
                                            @PathVariable String semester, @RequestBody Map<String, Object> body) {
        requireStudent(datasetId, studentId);
        if (!Arrays.asList(QualityScoring.SEMESTERS).contains(semester)) return Result.failed("评价学期无效");
        String status = text(body.get("status")).toUpperCase();
        if (!List.of("PENDING", "CONFIRMED_MISSING", "CONFIRMED_TRANSFER_IN").contains(status)) return Result.failed("缺失确认状态无效");
        String missing = missingDimensions(datasetId, studentId, semester);
        if (missing.isBlank()) return Result.failed("该学生该学期已存在完整评价，无需缺失确认");
        Long id = findId("SELECT id FROM local_quality_missing_review WHERE dataset_id=? AND student_id=? AND semester=?", datasetId, studentId, semester);
        if (id == null) jdbc.update("INSERT INTO local_quality_missing_review(dataset_id,student_id,semester,status,missing_dimensions,remark,updated_at) VALUES(?,?,?,?,?,?,?)", datasetId, studentId, semester, status, missing, text(body.get("remark")), LocalDateTime.now().toString());
        else jdbc.update("UPDATE local_quality_missing_review SET status=?,missing_dimensions=?,remark=?,updated_at=? WHERE id=?", status, missing, text(body.get("remark")), LocalDateTime.now().toString(), id);
        return Result.success();
    }

    @Transactional
    @PostMapping("/datasets/{datasetId}/final/generate")
    public Result<Map<String, Object>> generateFinal(@PathVariable long datasetId) {
        requireDataset(datasetId);
        Map<String, Object> config = jdbc.queryForMap("SELECT is_locked AS isLocked,a_ratio AS aRatio,b_ratio AS bRatio,c_ratio AS cRatio FROM local_quality_dataset WHERE id=?", datasetId);
        if (number(config.get("isLocked")) == 1) return Result.failed("最终评定已锁定，请先解锁后重新计算");
        List<Map<String, Object>> scoped = jdbc.queryForList("SELECT s.id,s.source_code AS code,s.name FROM local_quality_final_scope f JOIN local_quality_student s ON s.id=f.student_id WHERE f.dataset_id=? ORDER BY s.source_code,s.name", datasetId);
        if (scoped.isEmpty()) return Result.failed("请先导入包含最后一个学期工作表的评价数据");
        Map<String, List<QualityResultValue>> byDimension = new LinkedHashMap<>();
        Set<Long> pendingStudentIds = new HashSet<>();
        int pendingScores = 0;
        // 第一阶段只计算已录成绩；缺失项必须由使用者确认最终累计分。
        for (String dimension : QualityScoring.DIMENSIONS) {
            List<QualityResultValue> values = new ArrayList<>();
            for (Map<String, Object> student : scoped) {
                long studentId = ((Number) student.get("id")).longValue();
                QualityResultValue value = calculateFinalValue(datasetId, studentId, text(student.get("code")), text(student.get("name")), dimension);
                values.add(value);
                if (value.containsNa() && !value.scoreConfirmed()) {
                    pendingScores++;
                    pendingStudentIds.add(studentId);
                }
            }
            byDimension.put(dimension, values);
        }
        boolean readyForGrades = pendingScores == 0;
        int[] gradeCounts = allocateGradeCounts(scoped.size(), number(config.get("aRatio")), number(config.get("bRatio")), number(config.get("cRatio")));
        jdbc.update("DELETE FROM local_quality_final_result WHERE dataset_id=?", datasetId);
        int rowsCreated = 0;
        // 五个维度各自按最终分值排名、各自套用 A/B/C 名额，不使用五维总分排名。
        for (String dimension : QualityScoring.DIMENSIONS) {
            List<QualityResultValue> values = byDimension.get(dimension);
            if (readyForGrades) {
                values.sort(Comparator.comparingDouble(QualityResultValue::score).reversed()
                        .thenComparing(QualityResultValue::code).thenComparing(QualityResultValue::name)
                        .thenComparingLong(QualityResultValue::studentId));
            }
            for (int index = 0; index < values.size(); index++) {
                QualityResultValue value = values.get(index);
                int rank = readyForGrades ? index + 1 : 0;
                String level = readyForGrades ? levelAtRank(index, gradeCounts) : "N/A";
                saveFinal(datasetId, value, dimension, rank, level);
                rowsCreated++;
            }
        }
        jdbc.update("UPDATE local_quality_dataset SET generated_at=?,workflow_version=1 WHERE id=?", LocalDateTime.now().toString(), datasetId);
        return Result.success(Map.of("count", rowsCreated, "studentCount", scoped.size(), "scoredStudentCount", readyForGrades ? scoped.size() : 0,
                "pendingScoreCount", pendingScores, "pendingScoreStudentCount", pendingStudentIds.size(),
                "pendingReviewCount", count("SELECT COUNT(*) FROM local_quality_missing_review WHERE dataset_id=? AND status='PENDING'", datasetId),
                "pendingStudentCount", count("SELECT COUNT(DISTINCT student_id) FROM local_quality_missing_review WHERE dataset_id=? AND status='PENDING'", datasetId)));
    }

    @PutMapping("/datasets/{datasetId}/final/confirmed-score")
    @Transactional
    public Result<Map<String, Object>> confirmFinalScore(@PathVariable long datasetId, @RequestBody Map<String, Object> body) {
        requireDataset(datasetId);
        if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND is_locked=1", datasetId) > 0) return Result.failed("最终评定已锁定，请先解锁");
        long studentId;
        double confirmedScore;
        try {
            studentId = Long.parseLong(text(body.get("studentId")));
            confirmedScore = Double.parseDouble(text(body.get("score")));
        } catch (NumberFormatException exception) {
            return Result.failed("请选择学生并填写有效的最终累计分");
        }
        String dimension = text(body.get("dimension"));
        if (!Arrays.asList(QualityScoring.DIMENSIONS).contains(dimension)) return Result.failed("评价维度无效");
        if (count("SELECT COUNT(*) FROM local_quality_final_scope WHERE dataset_id=? AND student_id=?", datasetId, studentId) == 0) return Result.failed("该学生不在最终评定范围内");
        QualityResultValue calculated = calculateFinalValue(datasetId, studentId, "", "", dimension);
        if (!calculated.containsNa()) return Result.failed("该维度没有缺失成绩，无需人工确认分值");
        if (!Double.isFinite(confirmedScore) || confirmedScore < calculated.calculatedScore() - 0.000001 || confirmedScore > 10.000001)
            return Result.failed("确认分值必须在已计算分值和 10 分之间");
        confirmedScore = Math.round(confirmedScore * 100.0) / 100.0;
        Long id = findId("SELECT id FROM local_quality_score_confirmation WHERE dataset_id=? AND student_id=? AND dimension=?", datasetId, studentId, dimension);
        if (id == null) jdbc.update("INSERT INTO local_quality_score_confirmation(dataset_id,student_id,dimension,confirmed_score,updated_at) VALUES(?,?,?,?,?)", datasetId, studentId, dimension, confirmedScore, LocalDateTime.now().toString());
        else jdbc.update("UPDATE local_quality_score_confirmation SET confirmed_score=?,updated_at=? WHERE id=?", confirmedScore, LocalDateTime.now().toString(), id);
        return generateFinal(datasetId);
    }

    @PostMapping("/datasets/{datasetId}/final/lock")
    public Result<Void> lockFinal(@PathVariable long datasetId, @RequestParam(defaultValue = "true") boolean locked) {
        requireDataset(datasetId);
        if (locked) {
            if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND workflow_version=1", datasetId) == 0) return Result.failed("请先按新规则重新计算最终分值和等级");
            int expected = count("SELECT COUNT(*) FROM local_quality_final_scope WHERE dataset_id=?", datasetId) * QualityScoring.DIMENSIONS.length;
            int ready = count("SELECT COUNT(*) FROM local_quality_final_result WHERE dataset_id=? AND automatic_level<>'N/A' AND final_level IN ('A','B','C')", datasetId);
            if (expected == 0 || ready != expected) return Result.failed("请先计算分值并确认全部 N/A 学生的最终分值，再生成 A/B/C 等级");
        }
        jdbc.update("UPDATE local_quality_dataset SET is_locked=?,locked_at=? WHERE id=?", locked ? 1 : 0, locked ? LocalDateTime.now().toString() : null, datasetId);
        return Result.success();
    }

    @PutMapping("/datasets/{datasetId}/final/ratios")
    public Result<Void> updateRatios(@PathVariable long datasetId, @RequestBody Map<String, Object> body) {
        requireDataset(datasetId);
        double a = ratio(body.get("aRatio"), .60), b = ratio(body.get("bRatio"), .35), c = ratio(body.get("cRatio"), .05);
        if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(c) || a < 0 || b < 0 || c < 0 || Math.abs(a + b + c - 1) > .0001) return Result.failed("A/B/C 比例必须为非负数且合计 100%");
        if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND is_locked=1", datasetId) > 0) return Result.failed("最终评定已锁定，请先解锁后调整比例");
        jdbc.update("UPDATE local_quality_dataset SET a_ratio=?,b_ratio=?,c_ratio=?,workflow_version=0 WHERE id=?", a, b, c, datasetId);
        return Result.success();
    }

    @PutMapping("/datasets/{datasetId}/final/level")
    public Result<Void> updateFinalLevel(@PathVariable long datasetId, @RequestBody Map<String, Object> body) {
        requireDataset(datasetId);
        String level = text(body.get("level")).toUpperCase();
        if (!List.of("A", "B", "C").contains(level)) return Result.failed("最终等级只能是 A、B 或 C");
        if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND is_locked=1", datasetId) > 0) return Result.failed("最终评定已锁定，请先解锁");
        int updated = jdbc.update("UPDATE local_quality_final_result SET final_level=?,is_manually_adjusted=1 WHERE dataset_id=? AND student_id=? AND dimension=? AND automatic_level<>'N/A'",
                level, datasetId, body.get("studentId"), body.get("dimension"));
        return updated == 0 ? Result.failed("未找到可调整的最终评定") : Result.success();
    }

    @GetMapping("/datasets/{datasetId}/export")
    public void exportWorkbook(@PathVariable long datasetId, HttpServletResponse response) throws IOException {
        requireDataset(datasetId);
        List<Map<String, Object>> roster = students(datasetId).getData();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("综合素质评价-独立数据集.xlsx", "UTF-8"));
        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            writeRosterSheet(workbook, roster);
            writeScoreSheet(workbook, datasetId, roster);
            for (String semester : QualityScoring.SEMESTERS) writeSemesterSheet(workbook, datasetId, roster, semester);
            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/datasets/{datasetId}/final/export")
    public void exportFinal(@PathVariable long datasetId, HttpServletResponse response) throws IOException {
        requireDataset(datasetId);
        Map<String, Object> state = jdbc.queryForMap("SELECT is_locked AS isLocked,a_ratio AS aRatio,b_ratio AS bRatio,c_ratio AS cRatio,locked_at AS lockedAt FROM local_quality_dataset WHERE id=?", datasetId);
        if (number(state.get("isLocked")) != 1) throw new IllegalStateException("请先由老师确认并锁定最终评定，再导出正式结果");
        if (count("SELECT COUNT(*) FROM local_quality_dataset WHERE id=? AND workflow_version=1", datasetId) == 0) throw new IllegalStateException("请先按新规则重新计算最终结果，再导出正式表");
        int expected = count("SELECT COUNT(*) FROM local_quality_final_scope WHERE dataset_id=?", datasetId) * QualityScoring.DIMENSIONS.length;
        if (expected == 0 || count("SELECT COUNT(*) FROM local_quality_final_result WHERE dataset_id=? AND final_level IN ('A','B','C')", datasetId) != expected)
            throw new IllegalStateException("最终分值或等级尚未完整确认，不能导出正式结果");
        List<Map<String, Object>> scoped = jdbc.queryForList("SELECT s.id AS studentId,s.name,s.source_code AS code FROM local_quality_final_scope f JOIN local_quality_student s ON s.id=f.student_id WHERE f.dataset_id=? ORDER BY s.source_code,s.name", datasetId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("综合素质评价-正式最终结果.xlsx", "UTF-8"));
        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("最终结果");
            Row header = sheet.createRow(0); header.createCell(0).setCellValue("姓名");
            int column = 1;
            for (String dimension : QualityScoring.DIMENSIONS) header.createCell(column++).setCellValue(dimension + "分值");
            for (String dimension : QualityScoring.DIMENSIONS) header.createCell(column++).setCellValue(dimension + "等级");
            int rowIndex = 1;
            for (Map<String, Object> student : scoped) {
                Row row = sheet.createRow(rowIndex++); row.createCell(0).setCellValue(text(student.get("name"))); column = 1;
                Map<String, Map<String, Object>> byDimension = new HashMap<>();
                jdbc.queryForList("SELECT dimension,cumulative_score AS score,final_level AS level,contains_na AS containsNa FROM local_quality_final_result WHERE dataset_id=? AND student_id=?", datasetId, student.get("studentId"))
                        .forEach(item -> byDimension.put(text(item.get("dimension")), item));
                for (String dimension : QualityScoring.DIMENSIONS) {
                    Map<String, Object> item = byDimension.get(dimension);
                    if (item == null || "N/A".equals(text(item.get("level")))) row.createCell(column++).setCellValue("N/A");
                    else row.createCell(column++).setCellValue(number(item.get("score")));
                }
                for (String dimension : QualityScoring.DIMENSIONS) {
                    Map<String, Object> item = byDimension.get(dimension);
                    row.createCell(column++).setCellValue(item == null ? "N/A" : text(item.get("level")));
                }
            }
            autoSize(sheet, 1 + QualityScoring.DIMENSIONS.length * 2);
            workbook.write(response.getOutputStream());
        }
    }

    private ParsedQuality parseWorkbook(Workbook workbook) {
        DataFormatter formatter = new DataFormatter();
        List<QualityRow> rows = new ArrayList<>();
        List<String> issues = new ArrayList<>();
        Map<String, String> semesters = new LinkedHashMap<>();
        Map<String, Integer> noCodeNameCounts = new HashMap<>();
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            String semester = semesterOf(sheet.getSheetName());
            if (semester == null) continue;
            Header header = findHeader(sheet, formatter);
            if (header == null) { issues.add("工作表“" + sheet.getSheetName() + "”缺少姓名或评价维度表头"); continue; }
            semesters.put(sheet.getSheetName(), semester);
            for (int rowIndex = header.row() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String name = cell(sheet, row, header.nameColumn(), formatter);
                String code = header.codeColumn() == null ? "" : cell(sheet, row, header.codeColumn(), formatter);
                if (name.isBlank() && code.isBlank()) continue;
                if (name.isBlank()) { issues.add("工作表“" + sheet.getSheetName() + "”第" + (rowIndex + 1) + "行缺少姓名"); continue; }
                if (code.isBlank()) noCodeNameCounts.merge(semester + "\u0000" + name, 1, Integer::sum);
                Map<String, String> levels = new LinkedHashMap<>();
                for (String dimension : QualityScoring.DIMENSIONS) {
                    Integer column = header.dimensions().get(dimension);
                    levels.put(dimension, column == null ? "N/A" : normalizeLevel(cell(sheet, row, column, formatter)));
                }
                rows.add(new QualityRow(semester, rowIndex + 1, code, name, levels));
            }
        }
        boolean duplicateName = noCodeNameCounts.values().stream().anyMatch(count -> count > 1);
        return new ParsedQuality(rows, issues, semesters, duplicateName);
    }

    private Header findHeader(Sheet sheet, DataFormatter formatter) {
        for (int rowIndex = 0; rowIndex <= Math.min(10, sheet.getLastRowNum()); rowIndex++) {
            Row row = sheet.getRow(rowIndex); if (row == null) continue;
            Integer name = null, code = null; Map<String, Integer> dimensions = new HashMap<>();
            for (Cell cell : row) {
                String value = normalizeHeader(formatter.formatCellValue(cell));
                if (value.contains("姓名")) name = cell.getColumnIndex();
                if (value.contains("学号") || value.contains("考号") || value.equalsIgnoreCase("code")) code = cell.getColumnIndex();
                for (String dimension : QualityScoring.DIMENSIONS) {
                    if (value.equals(normalizeHeader(dimension)) || value.equals(normalizeHeader(dimension.replace("与", "")))) dimensions.put(dimension, cell.getColumnIndex());
                }
            }
            if (name != null && !dimensions.isEmpty()) return new Header(rowIndex, code, name, dimensions);
        }
        return null;
    }

    private static String semesterOf(String raw) {
        String normalized = raw == null ? "" : raw.toLowerCase().replace(" ", "");
        for (String semester : QualityScoring.SEMESTERS) if (normalized.contains(semester)) return semester;
        String[][] aliases = {{"初一上", "junior_1_1"}, {"七上", "junior_1_1"}, {"初一下", "junior_1_2"}, {"七下", "junior_1_2"}, {"初二上", "junior_2_1"}, {"八上", "junior_2_1"}, {"初二下", "junior_2_2"}, {"八下", "junior_2_2"}, {"初三上", "junior_3_1"}, {"九上", "junior_3_1"}, {"初三下", "junior_3_2"}, {"九下", "junior_3_2"}};
        for (String[] alias : aliases) if (normalized.contains(alias[0])) return alias[1];
        return null;
    }

    private Long resolveStudent(long datasetId, String code, String name) {
        Long id = code.isBlank() ? null : findId("SELECT id FROM local_quality_student WHERE dataset_id=? AND source_code=?", datasetId, code);
        if (id != null) return id;
        List<Map<String, Object>> sameName = jdbc.queryForList("SELECT id,source_code FROM local_quality_student WHERE dataset_id=? AND name=?", datasetId, name);
        if (sameName.size() > 1) throw new IllegalArgumentException("同一数据集中存在同名学生，无法安全导入");
        if (sameName.size() == 1) {
            id = ((Number) sameName.get(0).get("id")).longValue();
            String existingCode = text(sameName.get(0).get("source_code"));
            if (!code.isBlank() && !existingCode.isBlank() && !code.equals(existingCode)) throw new IllegalArgumentException("同名学生的学号不一致，无法自动对应");
            if (!code.isBlank() && existingCode.isBlank()) jdbc.update("UPDATE local_quality_student SET source_code=? WHERE id=?", code, id);
            return id;
        }
        return insert("INSERT INTO local_quality_student(dataset_id,source_code,name) VALUES(?,?,?)", datasetId, code.isBlank() ? null : code, name);
    }

    private void saveRecord(long datasetId, long studentId, String semester, String dimension, String level) {
        Long id = findId("SELECT id FROM local_quality_record WHERE dataset_id=? AND student_id=? AND semester=? AND dimension=?", datasetId, studentId, semester, dimension);
        if (id == null) jdbc.update("INSERT INTO local_quality_record(dataset_id,student_id,semester,dimension,level_or_score) VALUES(?,?,?,?,?)", datasetId, studentId, semester, dimension, level);
        else jdbc.update("UPDATE local_quality_record SET level_or_score=? WHERE id=?", level, id);
    }

    private void ensureRoster(long datasetId, long studentId, String semester) {
        if (findId("SELECT id FROM local_quality_roster_entry WHERE dataset_id=? AND student_id=? AND semester=?", datasetId, studentId, semester) == null)
            jdbc.update("INSERT INTO local_quality_roster_entry(dataset_id,student_id,semester) VALUES(?,?,?)", datasetId, studentId, semester);
    }

    private void ensureMissingReviews(long datasetId, long studentId) {
        for (String semester : QualityScoring.SEMESTERS) {
            String missing = missingDimensions(datasetId, studentId, semester);
            Long id = findId("SELECT id FROM local_quality_missing_review WHERE dataset_id=? AND student_id=? AND semester=?", datasetId, studentId, semester);
            if (missing.isBlank()) {
                if (id != null) jdbc.update("DELETE FROM local_quality_missing_review WHERE id=?", id);
            } else if (id == null) {
                jdbc.update("INSERT INTO local_quality_missing_review(dataset_id,student_id,semester,status,missing_dimensions,updated_at) VALUES(?,?,?,'PENDING',?,?)", datasetId, studentId, semester, missing, LocalDateTime.now().toString());
            } else {
                jdbc.update("UPDATE local_quality_missing_review SET missing_dimensions=?,updated_at=? WHERE id=?", missing, LocalDateTime.now().toString(), id);
            }
        }
    }

    private String missingDimensions(long datasetId, long studentId, String semester) {
        Map<String, String> levels = new HashMap<>();
        jdbc.queryForList("SELECT dimension,level_or_score FROM local_quality_record WHERE dataset_id=? AND student_id=? AND semester=?", datasetId, studentId, semester)
                .forEach(row -> levels.put(text(row.get("dimension")), text(row.get("level_or_score")).toUpperCase()));
        return Arrays.stream(QualityScoring.DIMENSIONS).filter(dimension -> !List.of("A", "B", "C").contains(levels.getOrDefault(dimension, "N/A"))).collect(java.util.stream.Collectors.joining("、"));
    }

    private QualityResultValue calculateFinalValue(long datasetId, long studentId, String code, String name, String dimension) {
        List<Map<String, Object>> records = jdbc.queryForList("SELECT semester,level_or_score FROM local_quality_record WHERE dataset_id=? AND student_id=? AND dimension=?", datasetId, studentId, dimension);
        Map<String, String> levels = new HashMap<>();
        records.forEach(row -> levels.put(text(row.get("semester")), text(row.get("level_or_score")).toUpperCase()));
        double calculatedScore = 0;
        int available = 0;
        for (String semester : QualityScoring.SEMESTERS) {
            String level = levels.getOrDefault(semester, "N/A");
            if (List.of("A", "B", "C").contains(level)) {
                available++;
                calculatedScore += QualityScoring.score(semester, level);
            }
        }
        calculatedScore = Math.round(calculatedScore * 100.0) / 100.0;
        boolean containsNa = available < QualityScoring.SEMESTERS.length;
        List<Double> confirmed = containsNa ? jdbc.query("SELECT confirmed_score FROM local_quality_score_confirmation WHERE dataset_id=? AND student_id=? AND dimension=?", (rs, row) -> rs.getDouble(1), datasetId, studentId, dimension) : List.of();
        boolean scoreConfirmed = !confirmed.isEmpty();
        double finalScore = scoreConfirmed ? confirmed.get(0) : calculatedScore;
        return new QualityResultValue(studentId, code, name, calculatedScore, finalScore, available, containsNa, scoreConfirmed);
    }

    private void saveFinal(long datasetId, QualityResultValue value, String dimension, int rank, String level) {
        jdbc.update("INSERT INTO local_quality_final_result(dataset_id,student_id,dimension,calculated_score,cumulative_score,class_rank,automatic_level,final_level,is_manually_adjusted,available_terms,contains_na,score_confirmed) VALUES(?,?,?,?,?,?,?,?,0,?,?,?)",
                datasetId, value.studentId(), dimension, value.calculatedScore(), value.score(), rank, level, level, value.availableTerms(), value.containsNa() ? 1 : 0, value.scoreConfirmed() ? 1 : 0);
    }

    /** Largest-remainder allocation keeps A/B/C counts as close as possible to configured ratios. */
    static int[] allocateGradeCounts(int size, double aRatio, double bRatio, double cRatio) {
        double[] exact = {size * aRatio, size * bRatio, size * cRatio};
        int[] counts = {(int) Math.floor(exact[0]), (int) Math.floor(exact[1]), (int) Math.floor(exact[2])};
        Integer[] order = {0, 1, 2};
        Arrays.sort(order, Comparator.comparingDouble((Integer index) -> exact[index] - counts[index]).reversed().thenComparingInt(index -> index));
        int remaining = size - counts[0] - counts[1] - counts[2];
        for (int i = 0; i < remaining; i++) counts[order[i]]++;
        return counts;
    }

    private static String levelAtRank(int zeroBasedRank, int[] counts) {
        if (zeroBasedRank < counts[0]) return "A";
        if (zeroBasedRank < counts[0] + counts[1]) return "B";
        return "C";
    }

    private void writeRosterSheet(Workbook workbook, List<Map<String, Object>> students) {
        Sheet sheet = workbook.createSheet("学生名单"); Row header = sheet.createRow(0); header.createCell(0).setCellValue("学号"); header.createCell(1).setCellValue("姓名"); header.createCell(2).setCellValue("已录入学期数");
        int rowIndex = 1; for (Map<String, Object> student : students) { Row row = sheet.createRow(rowIndex++); row.createCell(0).setCellValue(text(student.get("code"))); row.createCell(1).setCellValue(text(student.get("name"))); row.createCell(2).setCellValue(number(student.get("completedSemesters"))); }
        autoSize(sheet, 3);
    }

    private void writeScoreSheet(Workbook workbook, long datasetId, List<Map<String, Object>> students) {
        Sheet sheet = workbook.createSheet("五维得分"); Row header = sheet.createRow(0); header.createCell(0).setCellValue("姓名");
        for (int i = 0; i < QualityScoring.DIMENSIONS.length; i++) header.createCell(i + 1).setCellValue(QualityScoring.DIMENSIONS[i]);
        int rowIndex = 1;
        for (Map<String, Object> student : students) {
            Row row = sheet.createRow(rowIndex++); row.createCell(0).setCellValue(text(student.get("name")));
            for (int i = 0; i < QualityScoring.DIMENSIONS.length; i++) row.createCell(i + 1).setCellValue(totalScore(datasetId, student.get("studentId"), QualityScoring.DIMENSIONS[i]));
        }
        autoSize(sheet, 1 + QualityScoring.DIMENSIONS.length);
    }

    private void writeSemesterSheet(Workbook workbook, long datasetId, List<Map<String, Object>> students, String semester) {
        Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(semester)); Row header = sheet.createRow(0); header.createCell(0).setCellValue("姓名");
        for (int i = 0; i < QualityScoring.DIMENSIONS.length; i++) header.createCell(i + 1).setCellValue(QualityScoring.DIMENSIONS[i]);
        int rowIndex = 1;
        for (Map<String, Object> student : students) {
            Row row = sheet.createRow(rowIndex++); row.createCell(0).setCellValue(text(student.get("name")));
            for (int i = 0; i < QualityScoring.DIMENSIONS.length; i++) {
                List<String> values = jdbc.query("SELECT level_or_score FROM local_quality_record WHERE dataset_id=? AND student_id=? AND semester=? AND dimension=?", (rs, n) -> rs.getString(1), datasetId, student.get("studentId"), semester, QualityScoring.DIMENSIONS[i]);
                row.createCell(i + 1).setCellValue(values.isEmpty() ? "N/A" : values.get(0));
            }
        }
        autoSize(sheet, 1 + QualityScoring.DIMENSIONS.length);
    }

    private double totalScore(long datasetId, Object studentId, String dimension) {
        return jdbc.queryForList("SELECT semester,level_or_score FROM local_quality_record WHERE dataset_id=? AND student_id=? AND dimension=?", datasetId, studentId)
                .stream().mapToDouble(row -> QualityScoring.score(text(row.get("semester")), text(row.get("level_or_score")))).sum();
    }

    private void requireDataset(long id) { if (findId("SELECT id FROM local_quality_dataset WHERE id=?", id) == null) throw new IllegalArgumentException("综合素质数据集不存在，请先上传工作簿"); }
    private void requireStudent(long datasetId, long studentId) { if (findId("SELECT id FROM local_quality_student WHERE dataset_id=? AND id=?", datasetId, studentId) == null) throw new IllegalArgumentException("当前数据集内找不到该学生"); }
    private int count(String sql, Object... args) { Integer count = jdbc.queryForObject(sql, Integer.class, args); return count == null ? 0 : count; }
    private Long findId(String sql, Object... args) { List<Long> ids = jdbc.query(sql, (rs, row) -> rs.getLong(1), args); return ids.isEmpty() ? null : ids.get(0); }
    private long insert(String sql, Object... args) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> { PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); for (int i = 0; i < args.length; i++) statement.setObject(i + 1, args[i]); return statement; }, key);
        Number id = key.getKey(); if (id == null) throw new IllegalStateException("本地评价数据保存失败"); return id.longValue();
    }
    private static String cell(Sheet sheet, Row row, int column, DataFormatter formatter) {
        Cell current = row.getCell(column); if (current != null) { String value = formatter.formatCellValue(current).trim(); if (!value.isBlank()) return value; }
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) { var region = sheet.getMergedRegion(i); if (region.isInRange(row.getRowNum(), column)) { Row first = sheet.getRow(region.getFirstRow()); Cell cell = first == null ? null : first.getCell(region.getFirstColumn()); return cell == null ? "" : formatter.formatCellValue(cell).trim(); } }
        return "";
    }
    private static String normalizeHeader(String value) { return value == null ? "" : value.replaceAll("[\\s　()（）:：/_-]", "").trim(); }
    private static String normalizeLevel(String value) { String level = value == null ? "" : value.trim().toUpperCase(); if (level.startsWith("A")) return "A"; if (level.startsWith("B")) return "B"; if (level.startsWith("C")) return "C"; return "N/A"; }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static double number(Object value) { return value instanceof Number n ? n.doubleValue() : 0D; }
    private static double ratio(Object value, double fallback) { try { return value == null ? fallback : Double.parseDouble(String.valueOf(value)); } catch (RuntimeException ignored) { return fallback; } }
    private static void autoSize(Sheet sheet, int count) { for (int i = 0; i < count; i++) sheet.autoSizeColumn(i); }

    private record Header(int row, Integer codeColumn, int nameColumn, Map<String, Integer> dimensions) { }
    private record QualityRow(String semester, int rowNumber, String code, String name, Map<String, String> levels) { }
    private record ParsedQuality(List<QualityRow> rows, List<String> issues, Map<String, String> semesters, boolean studentsWithoutIdHaveDuplicateNames) { }
    private record QualityResultValue(long studentId, String code, String name, double calculatedScore, double score,
                                      int availableTerms, boolean containsNa, boolean scoreConfirmed) { }
}
