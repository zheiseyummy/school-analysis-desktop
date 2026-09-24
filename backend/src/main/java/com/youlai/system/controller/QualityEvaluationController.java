package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.QualityScoring;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/quality")
@RequiredArgsConstructor
public class QualityEvaluationController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        return Result.success(Map.of("dimensions", QualityScoring.DIMENSIONS, "semesters", QualityScoring.SEMESTERS, "semesterMaximums", QualityScoring.semesterMaximums(), "entryLevels", List.of("A", "B", "C", "N/A"), "finalLevels", List.of("A", "B", "C")));
    }

    /** 初中综合素质评价只显示初中年级，避免和高中班级混用。 */
    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> juniorClasses() {
        return Result.success(jdbcTemplate.queryForList("SELECT c.id, c.name, c.grade_id AS gradeId, g.name AS gradeName " +
                "FROM sys_clazz c JOIN sys_grade g ON g.id=c.grade_id " +
                "WHERE c.deleted=0 AND g.deleted=0 AND (g.stage='初中' OR g.name LIKE '%初%') ORDER BY g.sort, c.sort, c.name"));
    }

    /** 按班级返回学生名单及已录入学期数，页面不再要求用户手填学生 ID。 */
    @GetMapping("/class/{clazzId}")
    public Result<List<Map<String, Object>>> classStudents(@PathVariable Long clazzId) {
        String sql = "SELECT s.id AS studentId, s.code, s.name, " +
                "(SELECT COUNT(DISTINCT semester) FROM quality_roster_entry qre WHERE qre.student_id=s.id AND qre.deleted=0) AS completedSemesters " +
                "FROM sys_student s JOIN sys_clazz_student cs ON cs.student_id=s.id " +
                "WHERE cs.clazz_id=? AND s.deleted=0 ORDER BY s.code, s.name";
        return Result.success(jdbcTemplate.queryForList(sql, clazzId));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Map<String, Object>>> student(@PathVariable Long studentId) {
        return Result.success(jdbcTemplate.queryForList("SELECT semester, dimension, level_or_score AS level, comment FROM quality_record WHERE student_id=? AND deleted=0 ORDER BY semester, dimension", studentId));
    }

    @Transactional
    @PostMapping("/student/{studentId}/semester/{semester}")
    public Result<Void> saveSemester(@PathVariable Long studentId, @PathVariable String semester, @RequestBody Map<String, String> ratings) {
        if (!Arrays.asList(QualityScoring.SEMESTERS).contains(semester)) return Result.failed("评价学期无效");
        for (String dimension : QualityScoring.DIMENSIONS) {
            String level = ratings.getOrDefault(dimension, "N/A").toUpperCase();
            if (!List.of("A", "B", "C", "N/A").contains(level)) return Result.failed("评价等级无效");
            jdbcTemplate.update("INSERT INTO quality_record(student_id, semester, dimension, level_or_score, deleted) VALUES(?,?,?,?,0) ON CONFLICT(student_id, semester, dimension) DO UPDATE SET level_or_score=excluded.level_or_score, deleted=0", studentId, semester, dimension, level);
        }
        jdbcTemplate.update("INSERT INTO quality_roster_entry(student_id, semester, deleted) VALUES(?,?,0) ON CONFLICT(student_id, semester) DO UPDATE SET deleted=0", studentId, semester);
        return Result.success();
    }

    @GetMapping("/student/{studentId}/summary")
    public Result<Map<String, Object>> summary(@PathVariable Long studentId) {
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT semester, dimension, level_or_score AS level FROM quality_record WHERE student_id=? AND deleted=0", studentId);
        Map<String, Double> totals = new LinkedHashMap<>();
        for (String dimension : QualityScoring.DIMENSIONS) totals.put(dimension, records.stream().filter(r -> dimension.equals(r.get("dimension"))).mapToDouble(r -> QualityScoring.score((String) r.get("semester"), (String) r.get("level"))).sum());
        return Result.success(Map.of("records", records, "dimensionTotals", totals));
    }

    /** 导入综合素质评价工作簿。六个学期工作表可用“初一上/初一上学期/junior_1_1”等名称。 */
    @Transactional
    @PostMapping("/import")
    public Result<Map<String, Object>> importWorkbook(@RequestParam Long clazzId, @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return Result.failed("请选择综合素质评价工作簿");
        List<Map<String, Object>> students = jdbcTemplate.queryForList("SELECT s.id, s.code, s.name FROM sys_student s JOIN sys_clazz_student cs ON cs.student_id=s.id WHERE cs.clazz_id=? AND s.deleted=0 ORDER BY s.code", clazzId);
        Map<String, Map<String, Object>> byCode = new HashMap<>();
        Map<String, List<Map<String, Object>>> byName = new HashMap<>();
        for (Map<String, Object> s : students) {
            String code = text(s.get("code"));
            if (!code.isBlank()) byCode.put(code, s);
            byName.computeIfAbsent(text(s.get("name")), k -> new ArrayList<>()).add(s);
        }
        Map<String, String> sheetSemester = new LinkedHashMap<>();
        int totalRows = 0, matchedRows = 0;
        List<String> issues = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            DataFormatter formatter = new DataFormatter();
            for (int si = 0; si < workbook.getNumberOfSheets(); si++) {
                Sheet sheet = workbook.getSheetAt(si);
                String semester = semesterOf(sheet.getSheetName());
                boolean rosterOnly = semester == null && (sheet.getSheetName().contains("名单") || sheet.getSheetName().toLowerCase().contains("roster"));
                if (semester == null && !rosterOnly) continue;
                if (semester != null) sheetSemester.put(sheet.getSheetName(), semester);
                Map<String, Integer> columns = findHeader(sheet, formatter);
                if (!columns.containsKey("name")) { issues.add("工作表“" + sheet.getSheetName() + "”缺少姓名列"); continue; }
                for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r); if (row == null || r <= headerRow(sheet, formatter)) continue;
                    String name = cell(row, columns.get("name"), formatter);
                    String code = columns.containsKey("code") ? cell(row, columns.get("code"), formatter) : "";
                    if (name.isBlank() && code.isBlank()) continue;
                    totalRows++;
                    Map<String, Object> student = code.isBlank() ? null : byCode.get(code);
                    if (student == null) {
                        List<Map<String, Object>> sameName = byName.getOrDefault(name, List.of());
                        if (sameName.size() == 1) student = sameName.get(0);
                        else if (sameName.size() > 1) { issues.add("工作表“" + sheet.getSheetName() + "”第" + (r + 1) + "行姓名重复，请补充学号"); continue; }
                    }
                    if (student == null) { issues.add("工作表“" + sheet.getSheetName() + "”第" + (r + 1) + "行未匹配学生：" + (code.isBlank() ? name : code)); continue; }
                    matchedRows++;
                    if (rosterOnly) {
                        for (String targetSemester : QualityScoring.SEMESTERS) jdbcTemplate.update("INSERT INTO quality_roster_entry(student_id, semester, deleted) VALUES(?,?,0) ON CONFLICT(student_id, semester) DO UPDATE SET deleted=0", student.get("id"), targetSemester);
                        continue;
                    }
                    for (String dimension : QualityScoring.DIMENSIONS) {
                        Integer col = dimensionColumn(columns, dimension);
                        String level = col == null ? "N/A" : normalizeLevel(cell(row, col, formatter));
                        jdbcTemplate.update("INSERT INTO quality_record(student_id, semester, dimension, level_or_score, deleted) VALUES(?,?,?,?,0) ON CONFLICT(student_id, semester, dimension) DO UPDATE SET level_or_score=excluded.level_or_score, deleted=0", student.get("id"), semester, dimension, level);
                    }
                    jdbcTemplate.update("INSERT INTO quality_roster_entry(student_id, semester, deleted) VALUES(?,?,0) ON CONFLICT(student_id, semester) DO UPDATE SET deleted=0", student.get("id"), semester);
                }
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRows", totalRows); result.put("matchedRows", matchedRows); result.put("unmatchedRows", totalRows - matchedRows);
        result.put("semesterSheets", sheetSemester); result.put("issues", issues);
        return Result.success(result);
    }

    /** 导出当前班级的名单、五维累计分和六学期明细，便于继续编辑后重新导入。 */
    @GetMapping("/export")
    public void exportWorkbook(@RequestParam Long clazzId, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> students = classStudents(clazzId).getData();
        String fileName = URLEncoder.encode("初中综合素质评价-班级名单.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            writeRosterSheet(workbook, students);
            writeScoreSheet(workbook, students);
            for (String semester : QualityScoring.SEMESTERS) writeSemesterSheet(workbook, students, semester);
            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/final/{clazzId}")
    public Result<Map<String, Object>> finalResults(@PathVariable Long clazzId) {
        List<Map<String, Object>> stateRows = jdbcTemplate.queryForList("SELECT COALESCE(is_locked,0) AS isLocked, generated_at AS generatedAt FROM quality_finalization WHERE clazz_id=?", clazzId);
        Map<String, Object> state = stateRows.isEmpty() ? Map.of("isLocked", 0) : stateRows.get(0);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT r.student_id AS studentId, s.code, s.name, r.dimension, r.cumulative_score AS cumulativeScore, r.class_rank AS classRank, r.automatic_level AS automaticLevel, r.final_level AS finalLevel, r.available_terms AS availableTerms, r.contains_na AS containsNa FROM quality_final_result r JOIN sys_student s ON s.id=r.student_id WHERE r.clazz_id=? ORDER BY s.code, r.dimension", clazzId);
        return Result.success(Map.of("state", state, "rows", rows));
    }

    @Transactional
    @PostMapping("/final/{clazzId}/generate")
    public Result<Map<String, Object>> generateFinal(@PathVariable Long clazzId) {
        List<Map<String,Object>> lockRows = jdbcTemplate.queryForList("SELECT COALESCE(is_locked,0) AS isLocked FROM quality_finalization WHERE clazz_id=?", clazzId);
        Integer locked = lockRows.isEmpty() ? 0 : ((Number) lockRows.get(0).get("isLocked")).intValue();
        if (locked != null && locked == 1) return Result.failed("最终评定已锁定，请先解锁后重新计算");
        List<Map<String, Object>> students = jdbcTemplate.queryForList("SELECT s.id, s.code, s.name FROM sys_student s JOIN sys_clazz_student cs ON cs.student_id=s.id WHERE cs.clazz_id=? AND s.deleted=0 ORDER BY s.code", clazzId);
        jdbcTemplate.update("DELETE FROM quality_final_result WHERE clazz_id=?", clazzId);
        int count = 0;
        for (String dimension : QualityScoring.DIMENSIONS) {
            List<Map<String, Object>> values = new ArrayList<>();
            for (Map<String, Object> student : students) {
                List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT semester, level_or_score FROM quality_record WHERE student_id=? AND dimension=? AND deleted=0", student.get("id"), dimension);
                double total = 0D; int available = 0; boolean containsNa = false;
                for (String semester : QualityScoring.SEMESTERS) {
                    Optional<Map<String,Object>> record = records.stream().filter(r -> semester.equals(r.get("semester"))).findFirst();
                    String level = record.map(r -> text(r.get("level_or_score"))).orElse("N/A");
                    if ("N/A".equals(level)) containsNa = true; else { available++; total += QualityScoring.score(semester, level); }
                }
                Map<String,Object> value = new LinkedHashMap<>(); value.put("student", student); value.put("score", total); value.put("available", available); value.put("containsNa", containsNa); values.add(value);
            }
            values.sort((a,b) -> Double.compare((Double)b.get("score"), (Double)a.get("score")));
            int index=0; Double previous=null; int rank=0;
            for (Map<String,Object> value: values) {
                index++; double score=(Double)value.get("score"); if(previous==null || Double.compare(previous,score)!=0) rank=index; previous=score;
                String automatic = automaticLevel(rank, values.size()); Map<String,Object> student=(Map<String,Object>)value.get("student");
                jdbcTemplate.update("INSERT INTO quality_final_result(clazz_id,student_id,dimension,cumulative_score,class_rank,automatic_level,final_level,is_manually_adjusted,available_terms,contains_na) VALUES(?,?,?,?,?,?,?,0,?,?)", clazzId, student.get("id"), dimension, score, rank, automatic, automatic, value.get("available"), Boolean.TRUE.equals(value.get("containsNa")) ? 1 : 0); count++;
            }
        }
        jdbcTemplate.update("INSERT INTO quality_finalization(clazz_id,is_locked,generated_at) VALUES(?,0,?) ON CONFLICT(clazz_id) DO UPDATE SET generated_at=excluded.generated_at", clazzId, LocalDateTime.now().toString());
        return Result.success(Map.of("count", count, "studentCount", students.size()));
    }

    @PostMapping("/final/{clazzId}/lock")
    public Result<Void> lockFinal(@PathVariable Long clazzId, @RequestParam(defaultValue = "true") boolean locked) {
        jdbcTemplate.update("INSERT INTO quality_finalization(clazz_id,is_locked,locked_at) VALUES(?,?,?) ON CONFLICT(clazz_id) DO UPDATE SET is_locked=excluded.is_locked, locked_at=excluded.locked_at", clazzId, locked ? 1 : 0, locked ? LocalDateTime.now().toString() : null);
        return Result.success();
    }

    @PutMapping("/final/{clazzId}/level")
    public Result<Void> updateFinalLevel(@PathVariable Long clazzId, @RequestBody Map<String, Object> body) {
        String level = text(body.get("level")).toUpperCase();
        if (!List.of("A", "B", "C").contains(level)) return Result.failed("最终等级只能是 A、B 或 C");
        List<Map<String,Object>> lockRows = jdbcTemplate.queryForList("SELECT COALESCE(is_locked,0) AS isLocked FROM quality_finalization WHERE clazz_id=?", clazzId);
        if (!lockRows.isEmpty() && ((Number) lockRows.get(0).get("isLocked")).intValue() == 1) return Result.failed("最终评定已锁定，请先解锁");
        jdbcTemplate.update("UPDATE quality_final_result SET final_level=?, is_manually_adjusted=1 WHERE clazz_id=? AND student_id=? AND dimension=?", level, clazzId, body.get("studentId"), body.get("dimension"));
        return Result.success();
    }

    private static String automaticLevel(int rank, int size) { if (size == 0) return "B"; int a = (int)Math.ceil(size * .60); int c = (int)Math.floor(size * .05); if (rank <= a) return "A"; if (rank > size - c) return "C"; return "B"; }

    private void writeRosterSheet(Workbook wb, List<Map<String, Object>> students) {
        Sheet sheet = wb.createSheet("学生名单"); Row h = sheet.createRow(0); h.createCell(0).setCellValue("学号"); h.createCell(1).setCellValue("姓名"); h.createCell(2).setCellValue("已录入学期数");
        int r = 1; for (Map<String, Object> s : students) { Row row = sheet.createRow(r++); row.createCell(0).setCellValue(text(s.get("code"))); row.createCell(1).setCellValue(text(s.get("name"))); row.createCell(2).setCellValue(number(s.get("completedSemesters"))); }
        autoSize(sheet, 3);
    }

    private void writeScoreSheet(Workbook wb, List<Map<String, Object>> students) {
        Sheet sheet = wb.createSheet("五维得分"); Row h = sheet.createRow(0); h.createCell(0).setCellValue("学号"); h.createCell(1).setCellValue("姓名"); for (int i=0;i<QualityScoring.DIMENSIONS.length;i++) h.createCell(i+2).setCellValue(QualityScoring.DIMENSIONS[i]);
        int r=1; for (Map<String,Object> s:students) { Row row=sheet.createRow(r++); row.createCell(0).setCellValue(text(s.get("code"))); row.createCell(1).setCellValue(text(s.get("name"))); for(int i=0;i<QualityScoring.DIMENSIONS.length;i++){ row.createCell(i+2).setCellValue(totalScore(s.get("studentId"), QualityScoring.DIMENSIONS[i])); } }
        autoSize(sheet, 2 + QualityScoring.DIMENSIONS.length);
    }

    private void writeSemesterSheet(Workbook wb, List<Map<String,Object>> students, String semester) {
        Sheet sheet=wb.createSheet(WorkbookUtil.createSafeSheetName(semester)); Row h=sheet.createRow(0); h.createCell(0).setCellValue("学号"); h.createCell(1).setCellValue("姓名"); for(int i=0;i<QualityScoring.DIMENSIONS.length;i++) h.createCell(i+2).setCellValue(QualityScoring.DIMENSIONS[i]);
        int r=1; for(Map<String,Object> s:students){ Row row=sheet.createRow(r++); row.createCell(0).setCellValue(text(s.get("code"))); row.createCell(1).setCellValue(text(s.get("name"))); for(int i=0;i<QualityScoring.DIMENSIONS.length;i++){ String level=jdbcTemplate.queryForObject("SELECT level_or_score FROM quality_record WHERE student_id=? AND semester=? AND dimension=? AND deleted=0", String.class,s.get("studentId"),semester,QualityScoring.DIMENSIONS[i]); row.createCell(i+2).setCellValue(level == null ? "N/A" : level); } }
        autoSize(sheet, 2 + QualityScoring.DIMENSIONS.length);
    }

    private static void autoSize(Sheet sheet, int count) { for (int i=0;i<count;i++) sheet.autoSizeColumn(i); }
    private double totalScore(Object studentId, String dimension) { List<Map<String,Object>> records=jdbcTemplate.queryForList("SELECT semester,level_or_score FROM quality_record WHERE student_id=? AND dimension=? AND deleted=0",studentId,dimension); return records.stream().mapToDouble(r -> QualityScoring.score(text(r.get("semester")), text(r.get("level_or_score")))).sum(); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static double number(Object value) { return value instanceof Number n ? n.doubleValue() : 0D; }
    private static String cell(Row row, Integer index, DataFormatter formatter) { return index == null || row.getCell(index) == null ? "" : formatter.formatCellValue(row.getCell(index)).trim(); }
    private static String normalizeLevel(String value) { String v=value == null ? "" : value.trim().toUpperCase(); if (v.startsWith("A")) return "A"; if (v.startsWith("B")) return "B"; if (v.startsWith("C")) return "C"; return "N/A"; }
    private static Integer dimensionColumn(Map<String,Integer> columns, String dimension) { Integer c=columns.get(dimension); if(c!=null)return c; return columns.get(dimension.replace("与","")); }
    private static Map<String,Integer> findHeader(Sheet sheet, DataFormatter formatter) { Map<String,Integer> map=new HashMap<>(); for(int r=0;r<=Math.min(10,sheet.getLastRowNum());r++){ Row row=sheet.getRow(r); if(row==null)continue; for(Cell c:row){ String v=formatter.formatCellValue(c).trim(); if(v.contains("姓名"))map.put("name",c.getColumnIndex()); if(v.contains("学号")||v.contains("考号")||v.equalsIgnoreCase("code"))map.put("code",c.getColumnIndex()); for(String d:QualityScoring.DIMENSIONS) if(v.equals(d)||v.replace("与","").equals(d.replace("与","")))map.put(d,c.getColumnIndex()); } if(map.containsKey("name"))break; } return map; }
    private static int headerRow(Sheet sheet, DataFormatter formatter) { for(int r=0;r<=Math.min(10,sheet.getLastRowNum());r++){ Row row=sheet.getRow(r); if(row!=null) for(Cell c:row) if(formatter.formatCellValue(c).contains("姓名")) return r; } return 0; }
    private static String semesterOf(String raw) { String n=raw == null ? "" : raw.toLowerCase().replace(" ",""); for(String s:QualityScoring.SEMESTERS) if(n.contains(s))return s; String[][] aliases={{"初一上","junior_1_1"},{"初一上学期","junior_1_1"},{"七上","junior_1_1"},{"初一下","junior_1_2"},{"初一下学期","junior_1_2"},{"七下","junior_1_2"},{"初二上","junior_2_1"},{"八上","junior_2_1"},{"初二下","junior_2_2"},{"八下","junior_2_2"},{"初三上","junior_3_1"},{"九上","junior_3_1"},{"初三下","junior_3_2"},{"九下","junior_3_2"}}; for(String[] a:aliases)if(n.contains(a[0]))return a[1]; return null; }
}
