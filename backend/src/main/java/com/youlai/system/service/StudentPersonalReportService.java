package com.youlai.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.youlai.system.common.constant.ScoreStatus;
import com.youlai.system.model.bo.StudentScoreRankingBO;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.entity.SysStudent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.DataBarFormatting;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生个人成绩报告数据和模板导出服务。
 * 报告数据先从学生、考试、班级、年级、学科、成绩表统一组装，再分别写入 Excel/PDF，
 * 避免四个报告页各自查询造成学生错配。
 */
@Service
@RequiredArgsConstructor
public class StudentPersonalReportService {
    private static final String TEMPLATE_PATH = "templates/student-personal-report.xlsx";
    private static final DateTimeFormatter EXAM_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SysStudentService studentService;
    private final SysScoreService scoreService;
    private final SysCourseService courseService;
    private final SysExamService examService;
    private final SysExamCourseService examCourseService;
    private final SysClazzService clazzService;
    private final SysGradeService gradeService;
    private final SysClazzStudentService clazzStudentService;

    public byte[] exportExcel(Long studentId, Long examId) throws IOException {
        return exportExcel(build(studentId, examId));
    }

    private byte[] exportExcel(ReportData data) throws IOException {
        try (InputStream input = new ClassPathResource(TEMPLATE_PATH).getInputStream(); Workbook workbook = new XSSFWorkbook(input); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            expandTemplate(workbook, data.getSubjects().size());
            fillSource(workbook, data);
            fillNarratives(workbook, data);
            materializeReportFormulas(workbook);
            normalizeReportCells(workbook, data);
            removeSheet(workbook, "数据源_示例");
            removeSheet(workbook, "字段映射与导出说明");
            renameSheet(workbook, "模板A_综合分析", "综合分析");
            renameSheet(workbook, "模板B_趋势变化", "趋势变化");
            renameSheet(workbook, "模板C_学科诊断", "学科诊断");
            renameSheet(workbook, "模板D_家长沟通", "家长沟通");
            configurePrint(workbook);
            workbook.write(output);
            return output.toByteArray();
        }
    }

    public byte[] exportPdf(Long studentId, Long examId) throws IOException {
        return exportPdf(build(studentId, examId));
    }

    private byte[] exportPdf(ReportData data) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 24, 24, 24, 24);
            PdfWriter.getInstance(document, output);
            document.open();
            BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font normal = new Font(baseFont, 9);
            Font small = new Font(baseFont, 8);
            Font title = new Font(baseFont, 16, Font.BOLD);
            addSummaryPage(document, data, title, normal, small);
            document.newPage();
            addTrendPage(document, data, title, normal, small);
            document.newPage();
            addSubjectPage(document, data, title, normal, small);
            document.newPage();
            addParentPage(document, data, title, normal, small);
            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            if (e instanceof IOException io) throw io;
            throw new IOException("生成学生个人 PDF 失败", e);
        }
    }

    public String fileBaseName(Long studentId, Long examId) {
        return fileBaseName(build(studentId, examId));
    }

    /**
     * 同一名学生的一次批量报告只组装一次数据，避免 Excel、PDF、文件名重复查询后产生不一致。
     */
    public GeneratedReport generate(Long studentId, Long examId, boolean includeExcel, boolean includePdf) throws IOException {
        ReportData data = build(studentId, examId);
        return new GeneratedReport(
                fileBaseName(data),
                includeExcel ? exportExcel(data) : null,
                includePdf ? exportPdf(data) : null
        );
    }

    private String fileBaseName(ReportData data) {
        return sanitize(data.getClazzName()) + "_" + sanitize(data.getStudentCode()) + "_" + sanitize(data.getStudentName()) + "_" + sanitize(data.getCurrentExamName());
    }

    private ReportData build(Long studentId, Long requestedExamId) {
        SysStudent student = studentService.getById(studentId);
        if (student == null) throw new IllegalArgumentException("学生不存在");
        List<SysScore> studentScores = scoreService.list(new LambdaQueryWrapper<SysScore>().eq(SysScore::getStudentId, studentId));
        Set<Long> examIds = studentScores.stream().map(SysScore::getExamId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (requestedExamId != null) examIds.add(requestedExamId);
        List<SysExam> exams = examIds.isEmpty() ? List.of() : examService.listByIds(new ArrayList<>(examIds));
        SysExam currentExam = requestedExamId == null ? exams.stream().max(Comparator.comparing(SysExam::getExamDate, Comparator.nullsLast(Comparator.naturalOrder()))).orElse(null) : examService.getById(requestedExamId);
        if (currentExam == null) throw new IllegalArgumentException("学生没有可导出的考试成绩");

        Long clazzId = scoreService.getClazzIdByExamIdAndStudentId(currentExam.getId(), studentId);
        if (clazzId == null) clazzId = clazzStudentService.getByStudentId(studentId).stream().filter(x -> Objects.equals(x.getYear(), currentExam.getYear())).map(x -> x.getClazzId()).findFirst().orElse(null);
        SysClazz clazz = clazzId == null ? null : clazzService.getById(clazzId);
        Long gradeId = clazz == null ? null : clazz.getGradeId();
        SysGrade grade = gradeId == null ? null : gradeService.getById(gradeId);
        String clazzName = clazz == null ? "" : clazz.getName();
        String gradeName = grade == null ? "" : grade.getName();

        List<SysScore> currentClazzScores = clazzId == null ? List.of() : scoreService.getScoreListByExamIdAndClazzId(currentExam.getId(), clazzId);
        List<SysScore> currentGradeScores = gradeId == null ? currentClazzScores : scoreService.getScoreListByExamIdAndGradeId(currentExam.getId(), gradeId);
        Map<Long, SysScore> currentStudentScores = studentScores.stream().filter(s -> Objects.equals(s.getExamId(), currentExam.getId())).collect(Collectors.toMap(SysScore::getCourseId, x -> x, (a, b) -> b, LinkedHashMap::new));
        Map<Long, SysCourse> courses = courseService.list().stream().collect(Collectors.toMap(SysCourse::getId, x -> x, (a, b) -> a, LinkedHashMap::new));
        List<CourseConfig> config = loadCourseConfig(currentExam.getId(), currentStudentScores, courses);

        Map<Long, StudentScoreRankingBO> clazzRanks = clazzId == null ? Map.of() : scoreService.getStudentSummaryScoreRanking(currentExam.getId(), clazzId).stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, x -> x, (a, b) -> a));
        Map<Long, StudentScoreRankingBO> gradeRanks = gradeId == null ? Map.of() : scoreService.getGradeStudentSummaryScoreRanking(currentExam.getId(), gradeId).stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, x -> x, (a, b) -> a));
        Map<String, StudentScoreRankingBO> courseClazzRanks = clazzId == null ? Map.of() : scoreService.getStudentCourseScoreRanking(currentExam.getId(), clazzId).stream().collect(Collectors.toMap(x -> x.getStudentId() + ":" + x.getCourseId(), x -> x, (a, b) -> a));
        Map<String, StudentScoreRankingBO> courseGradeRanks = gradeId == null ? Map.of() : scoreService.getGradeStudentCourseScoreRanking(currentExam.getId(), gradeId).stream().collect(Collectors.toMap(x -> x.getStudentId() + ":" + x.getCourseId(), x -> x, (a, b) -> a));

        double fullScore = applicableFullScore(config, currentStudentScores);
        StudentScoreRankingBO clazzRank = clazzRanks.get(studentId);
        StudentScoreRankingBO gradeRank = gradeRanks.get(studentId);
        Map<Long, CourseConfig> configMap = config.stream().collect(Collectors.toMap(CourseConfig::getCourseId, x -> x, (a, b) -> a));
        List<SysScore> countedScores = currentStudentScores.values().stream()
                .filter(this::isNormalScore)
                .filter(score -> configMap.get(score.getCourseId()) != null && Integer.valueOf(1).equals(configMap.get(score.getCourseId()).getCountInTotal()))
                .toList();
        Double totalScore = countedScores.isEmpty() ? null : countedScores.stream().mapToDouble(SysScore::getScore).sum();
        Integer classRankValue = totalScore == null || clazzRank == null ? null : clazzRank.getStudentRank();
        Integer gradeRankValue = totalScore == null || gradeRank == null ? null : gradeRank.getStudentRank();
        Integer rankChange = calculateRankChange(studentId, currentExam, gradeId, gradeRankValue, exams);
        SysExam previousExam = previousExam(currentExam, exams);
        Map<Long, SysScore> previousStudentScores = previousExam == null ? Map.of() : studentScores.stream()
                .filter(score -> Objects.equals(score.getExamId(), previousExam.getId()))
                .collect(Collectors.toMap(SysScore::getCourseId, x -> x, (a, b) -> b, LinkedHashMap::new));

        List<SubjectRow> subjects = new ArrayList<>();
        for (CourseConfig c : config) {
            SysScore score = currentStudentScores.get(c.getCourseId());
            Double scoreValue = score == null ? null : score.getScore();
            String status = statusLabel(score);
            double rate = scoreValue == null || c.getFullScore() == null || c.getFullScore() == 0 ? Double.NaN : scoreValue / c.getFullScore();
            Double gradeAverage = averageForCourse(currentGradeScores, c.getCourseId());
            StudentScoreRankingBO cr = courseClazzRanks.get(studentId + ":" + c.getCourseId());
            StudentScoreRankingBO gr = courseGradeRanks.get(studentId + ":" + c.getCourseId());
            SysScore previousScore = previousStudentScores.get(c.getCourseId());
            Double previousValue = isNormalScore(previousScore) ? previousScore.getScore() : null;
            Double change = isNormalScore(score) && previousValue != null ? scoreValue - previousValue : null;
            subjects.add(new SubjectRow(c.getCourseId(), c.getName(), c.getFullScore(), scoreValue, status, rate, !isNormalScore(score) || cr == null ? null : cr.getStudentRank(), !isNormalScore(score) || gr == null ? null : gr.getStudentRank(), gradeAverage, Double.isNaN(rate) || gradeAverage == null ? null : scoreValue - gradeAverage, previousValue, change, degreeLabel(score, c.getFullScore())));
        }

        List<HistoryRow> history = buildHistory(studentId, currentExam, clazzId, gradeId, exams, courses, studentScores);
        ReportData data = new ReportData();
        data.setStudentId(studentId); data.setStudentCode(student.getCode()); data.setStudentName(student.getName()); data.setClazzName(clazzName); data.setGradeName(gradeName); data.setCurrentExamName(currentExam.getName()); data.setCurrentExamDate(currentExam.getExamDate()); data.setClassSize(clazzId == null ? 0 : Math.toIntExact(clazzStudentService.studentCountBy(clazzId, currentExam.getYear()))); data.setGradeSize(gradeId == null ? 0 : clazzStudentService.getStudentInfoListByGradeIdAndYear(gradeId, currentExam.getYear()).size()); data.setTotalScore(totalScore); data.setFullScore(fullScore); data.setScoreRate(totalScore == null || fullScore == 0 ? Double.NaN : totalScore / fullScore); data.setClassRank(classRankValue); data.setGradeRank(gradeRankValue); data.setRankChange(rankChange); data.setSubjects(subjects); data.setHistory(history); return data;
    }

    private List<HistoryRow> buildHistory(Long studentId, SysExam currentExam, Long clazzId, Long gradeId, List<SysExam> exams, Map<Long, SysCourse> courses, List<SysScore> studentScores) {
        List<SysExam> ordered = exams.stream().filter(x -> x.getExamDate() == null || currentExam.getExamDate() == null || !x.getExamDate().isAfter(currentExam.getExamDate())).sorted(Comparator.comparing(SysExam::getExamDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
        if (ordered.size() > 6) ordered = ordered.subList(ordered.size() - 6, ordered.size());
        List<HistoryRow> rows = new ArrayList<>();
        for (SysExam exam : ordered) {
            Long rowClazzId = Objects.equals(exam.getId(), currentExam.getId()) ? clazzId : scoreService.getClazzIdByExamIdAndStudentId(exam.getId(), studentId);
            List<SysScore> examStudentScores = studentScores.stream().filter(score -> Objects.equals(score.getExamId(), exam.getId())).toList();
            Long rowGradeId = examStudentScores.stream().map(SysScore::getGradeId).filter(Objects::nonNull).findFirst().orElse(gradeId);
            List<SysScore> gradeScope = rowGradeId == null ? List.of() : scoreService.getScoreListByExamIdAndGradeId(exam.getId(), rowGradeId);
            Map<Long, SysScore> studentScoreMap = examStudentScores.stream().collect(Collectors.toMap(SysScore::getCourseId, x -> x, (a, b) -> b, LinkedHashMap::new));
            List<CourseConfig> rowConfig = loadCourseConfig(exam.getId(), studentScoreMap, courses);
            Map<Long, StudentScoreRankingBO> cr = rowClazzId == null ? Map.of() : scoreService.getStudentSummaryScoreRanking(exam.getId(), rowClazzId).stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, x -> x, (a, b) -> a));
            Map<Long, StudentScoreRankingBO> gr = rowGradeId == null ? Map.of() : scoreService.getGradeStudentSummaryScoreRanking(exam.getId(), rowGradeId).stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, x -> x, (a, b) -> a));
            StudentScoreRankingBO c = cr.get(studentId); StudentScoreRankingBO g = gr.get(studentId);
            List<SysScore> countedScores = studentScoreMap.values().stream().filter(this::isNormalScore).filter(score -> rowConfig.stream().anyMatch(config -> Objects.equals(config.getCourseId(), score.getCourseId()) && Integer.valueOf(1).equals(config.getCountInTotal()))).toList();
            Double total = countedScores.isEmpty() ? null : countedScores.stream().mapToDouble(SysScore::getScore).sum();
            rows.add(new HistoryRow(exam.getId(), exam.getName(), total, averageTotal(gradeScope, rowConfig), total == null || c == null ? null : c.getStudentRank(), total == null || g == null ? null : g.getStudentRank(), exam.getExamDate(), applicableFullScore(rowConfig, studentScoreMap)));
        }
        return rows;
    }

    private Integer calculateRankChange(Long studentId, SysExam currentExam, Long gradeId, Integer currentRank, List<SysExam> exams) {
        SysExam previous = previousExam(currentExam, exams);
        if (previous == null || currentRank == null || currentRank == 0) return null;
        Long previousGradeId = scoreService.list(new LambdaQueryWrapper<SysScore>().eq(SysScore::getStudentId, studentId).eq(SysScore::getExamId, previous.getId())).stream().map(SysScore::getGradeId).filter(Objects::nonNull).findFirst().orElse(gradeId);
        Map<Long, StudentScoreRankingBO> ranks = previousGradeId == null ? Map.of() : scoreService.getGradeStudentSummaryScoreRanking(previous.getId(), previousGradeId).stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, x -> x, (a, b) -> a));
        StudentScoreRankingBO p = ranks.get(studentId);
        return p == null || p.getStudentRank() == null ? null : p.getStudentRank() - currentRank;
    }

    private Double averageForCourse(List<SysScore> scores, Long courseId) { return scores.stream().filter(x -> Objects.equals(x.getCourseId(), courseId) && isNormalScore(x)).mapToDouble(SysScore::getScore).average().stream().boxed().findFirst().orElse(null); }
    private Double averageTotal(List<SysScore> scores, List<CourseConfig> config) { Map<Long, CourseConfig> map = config.stream().collect(Collectors.toMap(CourseConfig::getCourseId, x -> x, (a, b) -> a)); return scores.stream().filter(this::isNormalScore).filter(x -> map.get(x.getCourseId()) != null && Integer.valueOf(1).equals(map.get(x.getCourseId()).getCountInTotal())).collect(Collectors.groupingBy(SysScore::getStudentId, Collectors.summingDouble(SysScore::getScore))).values().stream().mapToDouble(Double::doubleValue).average().stream().boxed().findFirst().orElse(null); }
    private boolean isNormalScore(SysScore score) { return score != null && score.getScore() != null && (score.getStatus() == null || ScoreStatus.NORMAL.equals(score.getStatus())); }
    private String statusLabel(SysScore score) { if (score == null) return "缺失"; if (ScoreStatus.ABSENT.equals(score.getStatus())) return "缺考"; if (ScoreStatus.NOT_SELECTED.equals(score.getStatus())) return "未选科"; return score.getScore() == null ? "缺失" : "正常"; }
    private String degreeLabel(SysScore score, Double fullScore) { if (!isNormalScore(score)) return "/"; if (score.getDegree() != null) return switch (score.getDegree()) { case 1 -> "A"; case 2 -> "B"; case 3 -> "C"; case 4 -> "D"; case 5 -> "E"; default -> String.valueOf(score.getDegree()); }; double rate = fullScore == null || fullScore == 0 ? Double.NaN : score.getScore() / fullScore; if (Double.isNaN(rate)) return "/"; if (rate >= .9) return "A"; if (rate >= .8) return "B"; if (rate >= .7) return "C"; if (rate >= .6) return "D"; return "E"; }

    private SysExam previousExam(SysExam currentExam, List<SysExam> exams) { return exams.stream().filter(x -> x.getExamDate() != null && currentExam.getExamDate() != null && x.getExamDate().isBefore(currentExam.getExamDate())).max(Comparator.comparing(SysExam::getExamDate)).orElse(null); }
    private List<CourseConfig> loadCourseConfig(Long examId, Map<Long, SysScore> studentScores, Map<Long, SysCourse> courses) { if (examCourseService.hasConfig(examId)) { List<CourseConfig> config = examCourseService.getConfig(examId).stream().filter(x -> Boolean.TRUE.equals(x.getSelected())).sorted(Comparator.comparing(x -> x.getSort() == null ? 0 : x.getSort())).map(x -> new CourseConfig(x.getCourseId(), x.getCourseName(), x.getFullScore(), x.getCountInTotal())).toList(); if (!config.isEmpty()) return config; } return studentScores.keySet().stream().map(id -> { SysCourse c = courses.get(id); return new CourseConfig(id, c == null ? String.valueOf(id) : c.getName(), c == null || c.getFullScore() == null ? null : c.getFullScore().doubleValue(), 1); }).toList(); }
    private double applicableFullScore(List<CourseConfig> config, Map<Long, SysScore> scores) { return config.stream().filter(x -> Integer.valueOf(1).equals(x.getCountInTotal())).filter(x -> { SysScore score = scores.get(x.getCourseId()); return score == null || !ScoreStatus.NOT_SELECTED.equals(score.getStatus()); }).mapToDouble(x -> x.getFullScore() == null ? 0D : x.getFullScore()).sum(); }

    private void fillSource(Workbook workbook, ReportData d) {
        Sheet source = workbook.getSheet("数据源_示例");
        int subjectSlots = Math.max(8, d.getSubjects().size());
        int historyStart = 23 + Math.max(0, d.getSubjects().size() - 8);
        set(source, "B4", d.getStudentName()); set(source, "B5", d.getClazzName()); set(source, "B6", d.getStudentCode()); set(source, "B7", d.getCurrentExamName()); set(source, "B8", d.getClassSize()); set(source, "B9", d.getGradeSize()); set(source, "E4", d.getTotalScore()); set(source, "E5", d.getFullScore()); set(source, "E6", d.getScoreRate()); set(source, "E7", d.getClassRank()); set(source, "E8", d.getGradeRank()); set(source, "E9", d.getRankChange());
        for (int i = 0; i < subjectSlots; i++) { int row = 12 + i; if (i < d.getSubjects().size()) { SubjectRow s = d.getSubjects().get(i); set(source, "A" + row, s.getName()); set(source, "B" + row, s.getFullScore()); set(source, "C" + row, display(s)); set(source, "D" + row, rankValue(s.getClassRank())); set(source, "E" + row, rankValue(s.getGradeRank())); set(source, "F" + row, s.getGradeAverage()); set(source, "G" + row, Double.isNaN(s.getRate()) ? null : s.getRate()); set(source, "H" + row, s.getDifference()); set(source, "I" + row, s.getDegree()); set(source, "J" + row, s.getPreviousScore()); set(source, "K" + row, s.getChange()); } else clearRow(source, row, 11); }
        for (int i = 0; i < 6; i++) { int row = historyStart + i; if (i < d.getHistory().size()) { HistoryRow h = d.getHistory().get(i); set(source, "A" + row, h.getExamName()); set(source, "B" + row, h.getTotalScore()); set(source, "C" + row, rankValue(h.getClassRank())); set(source, "D" + row, rankValue(h.getGradeRank())); set(source, "E" + row, h.getGradeAverage()); set(source, "F" + row, h.getExamDate() == null ? "" : h.getExamDate().format(EXAM_DATE)); } else clearRow(source, row, 6); }
    }

    private void fillNarratives(Workbook workbook, ReportData d) {
        int extra = Math.max(0, d.getSubjects().size() - 8);
        SubjectRow strongest = d.getSubjects().stream().filter(x -> !Double.isNaN(x.getRate())).max(Comparator.comparing(SubjectRow::getRate)).orElse(null); SubjectRow weakest = d.getSubjects().stream().filter(x -> !Double.isNaN(x.getRate())).min(Comparator.comparing(SubjectRow::getRate)).orElse(null);
        String summary = d.getTotalScore() == null
                ? "本次暂无可计入总分的有效成绩，排名和变化暂不评价。"
                : "本次总分 " + displayTotal(d.getTotalScore()) + " 分，年级排名" + displayRank(d.getGradeRank()) + "。" + rankChangeNarrative(d.getRankChange()) + "建议保持优势学科并优先关注薄弱学科。";
        if (strongest != null && weakest != null) summary += "优势学科：" + strongest.getName() + "；重点关注：" + weakest.getName() + "。";
        setMerged(workbook.getSheet("模板A_综合分析"), "A" + (30 + extra), summary); setMerged(workbook.getSheet("模板B_趋势变化"), "A19", "最近 " + d.getHistory().size() + " 次考试总分：" + d.getHistory().stream().map(x -> displayTotal(x.getTotalScore())).collect(Collectors.joining(" → ")) + "。建议结合总分、年级排名和薄弱学科连续观察。"); setMerged(workbook.getSheet("模板C_学科诊断"), "A" + (30 + extra), "优势学科保持稳定输出；薄弱学科先补基础概念和高频题型，再逐步增加综合题。每次考试后记录知识性失分、审题失分和计算表达失分。"); setMerged(workbook.getSheet("模板D_家长沟通"), "A" + (22 + extra), "优势：" + (strongest == null ? "暂无" : strongest.getName()) + " 当前表现较突出；\n关注：" + (weakest == null ? "暂无" : weakest.getName()) + " 得分率相对偏低。\n建议关注连续考试变化，不只看单次分数。");
    }

    private void normalizeReportCells(Workbook workbook, ReportData d) {
        Sheet summary = workbook.getSheet("模板A_综合分析");
        Sheet trend = workbook.getSheet("模板B_趋势变化");
        Sheet subject = workbook.getSheet("模板C_学科诊断");
        Sheet parent = workbook.getSheet("模板D_家长沟通");
        int extra = Math.max(0, d.getSubjects().size() - 8);
        int subjectSlots = Math.max(8, d.getSubjects().size());
        set(summary, "B3", d.getStudentName()); set(summary, "E3", d.getClazzName()); set(summary, "H3", d.getCurrentExamName());
        set(summary, "A5", "总分\n" + displayTotal(d.getTotalScore())); set(summary, "C5", "得分率\n" + percent(d.getScoreRate())); set(summary, "E5", "班级排名\n" + displayRank(d.getClassRank())); set(summary, "G5", "年级排名\n" + displayRank(d.getGradeRank()));
        set(trend, "B3", d.getStudentName()); set(trend, "E3", d.getClazzName()); set(trend, "H3", d.getCurrentExamName());
        set(trend, "A5", "当前总分\n" + displayTotal(d.getTotalScore())); set(trend, "C5", "年级排名\n" + displayRank(d.getGradeRank())); set(trend, "E5", "排名变化\n" + rankChangeText(d.getRankChange())); set(trend, "G5", "当前得分率\n" + percent(d.getScoreRate()));
        set(subject, "B3", d.getStudentName()); set(subject, "E3", d.getClazzName()); set(subject, "H3", d.getCurrentExamName());
        set(parent, "B3", d.getStudentName()); set(parent, "E3", d.getClazzName()); set(parent, "H3", d.getCurrentExamName());
        set(parent, "A5", "当前总分\n" + displayTotal(d.getTotalScore()) + (d.getTotalScore() == null ? "" : " 分")); set(parent, "D5", "年级位次\n" + displayRank(d.getGradeRank())); set(parent, "G5", "较上次排名\n" + rankChangeText(d.getRankChange()));
        for (int i = 0; i < subjectSlots; i++) {
            int summaryRow = 11 + i, subjectRow = 7 + i, parentRow = 12 + i;
            if (i >= d.getSubjects().size()) { clearRow(summary, summaryRow, 9); clearRow(subject, subjectRow, 9); clearRow(parent, parentRow, 9); continue; }
            SubjectRow s = d.getSubjects().get(i);
            set(summary, "A" + summaryRow, s.getName()); set(summary, "B" + summaryRow, s.getFullScore()); set(summary, "C" + summaryRow, display(s)); set(summary, "D" + summaryRow, Double.isNaN(s.getRate()) ? null : s.getRate()); set(summary, "E" + summaryRow, rankValue(s.getClassRank())); set(summary, "F" + summaryRow, rankValue(s.getGradeRank())); set(summary, "G" + summaryRow, s.getGradeAverage()); set(summary, "H" + summaryRow, s.getDifference()); set(summary, "I" + summaryRow, s.getDegree());
            set(subject, "A" + subjectRow, s.getName()); set(subject, "B" + subjectRow, display(s)); set(subject, "C" + subjectRow, s.getFullScore()); set(subject, "D" + subjectRow, Double.isNaN(s.getRate()) ? null : s.getRate()); set(subject, "E" + subjectRow, s.getGradeAverage()); set(subject, "F" + subjectRow, s.getDifference()); set(subject, "G" + subjectRow, rankValue(s.getGradeRank())); set(subject, "H" + subjectRow, s.getChange()); set(subject, "I" + subjectRow, diagnostic(s));
            set(parent, "A" + parentRow, s.getName()); set(parent, "B" + parentRow, display(s)); set(parent, "C" + parentRow, s.getFullScore()); set(parent, "D" + parentRow, Double.isNaN(s.getRate()) ? null : s.getRate()); set(parent, "E" + parentRow, rankValue(s.getClassRank())); set(parent, "F" + parentRow, rankValue(s.getGradeRank())); set(parent, "G" + parentRow, s.getChange()); set(parent, "H" + parentRow, diagnostic(s)); set(parent, "I" + parentRow, suggestion(s));
        }
        for (int i = 0; i < 6; i++) {
            int summaryRow = 22 + extra + i, trendRow = 11 + i;
            if (i >= d.getHistory().size()) { clearRow(summary, summaryRow, 5); clearRow(trend, trendRow, 6); continue; }
            HistoryRow h = d.getHistory().get(i);
            set(summary, "A" + summaryRow, h.getExamName()); set(summary, "B" + summaryRow, h.getTotalScore()); set(summary, "C" + summaryRow, rankValue(h.getClassRank())); set(summary, "D" + summaryRow, rankValue(h.getGradeRank())); set(summary, "E" + summaryRow, h.getGradeAverage());
            Double previousTotal = i == 0 ? null : d.getHistory().get(i - 1).getTotalScore();
            set(trend, "A" + trendRow, h.getExamName()); set(trend, "B" + trendRow, h.getTotalScore()); set(trend, "C" + trendRow, h.getGradeAverage()); set(trend, "D" + trendRow, rankValue(h.getClassRank())); set(trend, "E" + trendRow, rankValue(h.getGradeRank())); set(trend, "F" + trendRow, h.getTotalScore() == null || previousTotal == null ? "—" : h.getTotalScore() - previousTotal);
        }
        SubjectRow strongest = strongest(d.getSubjects()), weakest = weakest(d.getSubjects());
        set(summary, "F" + (21 + extra), "排名变化\n" + rankChangeText(d.getRankChange()));
        set(summary, "F" + (24 + extra), "优势学科：" + (strongest == null ? "暂无" : strongest.getName()) + "\n重点关注：" + (weakest == null ? "暂无" : weakest.getName()));
        set(trend, "B30", d.getTotalScore()); set(trend, "B31", rankValue(d.getGradeRank())); set(trend, "B32", weakest == null ? "暂无" : weakest.getName());
        fillSubjectInsights(subject, d, extra);
    }

    private void fillSubjectInsights(Sheet subject, ReportData d, int extra) {
        List<SubjectRow> valid = d.getSubjects().stream().filter(x -> !Double.isNaN(x.getRate())).toList();
        SubjectRow strongest = strongest(valid), weakest = weakest(valid);
        SubjectRow belowAverage = valid.stream().filter(x -> x.getDifference() != null && x.getDifference() < 0).min(Comparator.comparing(SubjectRow::getDifference)).orElse(null);
        SubjectRow bestRank = valid.stream().filter(x -> x.getGradeRank() != null && x.getGradeRank() > 0).min(Comparator.comparing(SubjectRow::getGradeRank)).orElse(null);
        SubjectRow progress = valid.stream().filter(x -> x.getChange() != null && x.getChange() > 0).max(Comparator.comparing(SubjectRow::getChange)).orElse(null);
        SubjectRow decline = valid.stream().filter(x -> x.getChange() != null && x.getChange() < 0).min(Comparator.comparing(SubjectRow::getChange)).orElse(null);
        int keyRow = 18 + extra;
        set(subject, "B" + keyRow, strongest == null ? null : strongest.getName()); set(subject, "C" + keyRow, strongest == null ? null : strongest.getRate()); set(subject, "F" + keyRow, belowAverage == null ? null : belowAverage.getName()); set(subject, "G" + keyRow, belowAverage == null ? null : belowAverage.getDifference());
        set(subject, "B" + (keyRow + 1), weakest == null ? null : weakest.getName()); set(subject, "C" + (keyRow + 1), weakest == null ? null : weakest.getRate()); set(subject, "F" + (keyRow + 1), bestRank == null ? null : bestRank.getName()); set(subject, "G" + (keyRow + 1), bestRank == null ? null : bestRank.getGradeRank());
        set(subject, "B" + (keyRow + 2), progress == null ? null : progress.getName()); set(subject, "C" + (keyRow + 2), progress == null ? null : progress.getChange()); set(subject, "F" + (keyRow + 2), decline == null ? null : decline.getName()); set(subject, "G" + (keyRow + 2), decline == null ? null : decline.getChange());
        List<SubjectRow> priorities = valid.stream().sorted(Comparator.comparing(SubjectRow::getRate)).limit(3).toList();
        int goalStart = 24 + extra;
        for (int i = 0; i < 3; i++) {
            int row = goalStart + i;
            if (i >= priorities.size()) { for (String col : new String[]{"B", "C", "D", "E", "F"}) set(subject, col + row, null); continue; }
            SubjectRow s = priorities.get(i); double target = Math.min(s.getFullScore() == null ? s.getScore() : s.getFullScore(), s.getScore() + Math.round((s.getFullScore() == null ? 100D : s.getFullScore()) * .05D));
            set(subject, "B" + row, s.getName()); set(subject, "C" + row, s.getScore()); set(subject, "D" + row, target); set(subject, "E" + row, target - s.getScore()); set(subject, "F" + row, suggestion(s));
        }
        set(subject, "G" + (23 + extra), null);
    }

    private void expandTemplate(Workbook workbook, int subjectCount) {
        int extra = Math.max(0, subjectCount - 8);
        if (extra == 0) return;
        insertRows(workbook.getSheet("数据源_示例"), 19, extra, 18, 11);
        insertRows(workbook.getSheet("模板A_综合分析"), 18, extra, 17, 9);
        insertRows(workbook.getSheet("模板C_学科诊断"), 14, extra, 13, 9);
        insertRows(workbook.getSheet("模板D_家长沟通"), 19, extra, 18, 9);
        cloneDataBarFormatting(workbook.getSheet("模板A_综合分析"), "D19:D" + (18 + extra));
        cloneColorScaleFormatting(workbook.getSheet("模板C_学科诊断"), "D15:D" + (14 + extra));
    }

    /** 复制模板原有的数据条到动态新增的学科行，避免第 9 科以后失去可视化。 */
    private void cloneDataBarFormatting(Sheet sheet, String range) {
        SheetConditionalFormatting formatting = sheet.getSheetConditionalFormatting();
        for (int index = 0; index < formatting.getNumConditionalFormattings(); index++) {
            ConditionalFormatting sourceFormatting = formatting.getConditionalFormattingAt(index);
            for (int ruleIndex = 0; ruleIndex < sourceFormatting.getNumberOfRules(); ruleIndex++) {
                DataBarFormatting source = sourceFormatting.getRule(ruleIndex).getDataBarFormatting();
                if (source == null || !(source.getColor() instanceof ExtendedColor color)) continue;
                ConditionalFormattingRule targetRule = formatting.createConditionalFormattingRule(color);
                DataBarFormatting target = targetRule.getDataBarFormatting();
                target.setIconOnly(source.isIconOnly());
                target.setLeftToRight(source.isLeftToRight());
                target.setWidthMin(source.getWidthMin());
                target.setWidthMax(source.getWidthMax());
                copyThreshold(source.getMinThreshold(), target.getMinThreshold());
                copyThreshold(source.getMaxThreshold(), target.getMaxThreshold());
                formatting.addConditionalFormatting(new CellRangeAddress[]{CellRangeAddress.valueOf(range)}, targetRule);
                return;
            }
        }
    }

    /** 复制模板原有的色阶到动态新增的学科行。 */
    private void cloneColorScaleFormatting(Sheet sheet, String range) {
        SheetConditionalFormatting formatting = sheet.getSheetConditionalFormatting();
        for (int index = 0; index < formatting.getNumConditionalFormattings(); index++) {
            ConditionalFormatting sourceFormatting = formatting.getConditionalFormattingAt(index);
            for (int ruleIndex = 0; ruleIndex < sourceFormatting.getNumberOfRules(); ruleIndex++) {
                ColorScaleFormatting source = sourceFormatting.getRule(ruleIndex).getColorScaleFormatting();
                if (source == null) continue;
                ConditionalFormattingRule targetRule = formatting.createConditionalFormattingColorScaleRule();
                ColorScaleFormatting target = targetRule.getColorScaleFormatting();
                target.setNumControlPoints(source.getNumControlPoints());
                target.setColors(source.getColors());
                ConditionalFormattingThreshold[] sourceThresholds = source.getThresholds();
                ConditionalFormattingThreshold[] targetThresholds = new ConditionalFormattingThreshold[sourceThresholds.length];
                for (int i = 0; i < sourceThresholds.length; i++) {
                    targetThresholds[i] = target.createThreshold();
                    copyThreshold(sourceThresholds[i], targetThresholds[i]);
                }
                target.setThresholds(targetThresholds);
                formatting.addConditionalFormatting(new CellRangeAddress[]{CellRangeAddress.valueOf(range)}, targetRule);
                return;
            }
        }
    }

    private void copyThreshold(ConditionalFormattingThreshold source, ConditionalFormattingThreshold target) {
        target.setRangeType(source.getRangeType());
        target.setFormula(source.getFormula());
        target.setValue(source.getValue());
    }

    private void insertRows(Sheet sheet, int startRow, int count, int styleSourceRow, int columns) {
        if (sheet == null || count <= 0) return;
        Row styleSource = sheet.getRow(styleSourceRow);
        sheet.shiftRows(startRow, sheet.getLastRowNum(), count, true, false);
        for (int index = 0; index < count; index++) {
            Row target = sheet.getRow(startRow + index);
            if (target == null) target = sheet.createRow(startRow + index);
            if (styleSource != null) target.setHeight(styleSource.getHeight());
            for (int column = 0; column < columns; column++) {
                Cell targetCell = target.getCell(column);
                if (targetCell == null) targetCell = target.createCell(column);
                Cell sourceCell = styleSource == null ? null : styleSource.getCell(column);
                CellStyle style = sourceCell == null ? null : sourceCell.getCellStyle();
                if (style != null) targetCell.setCellStyle(style);
            }
        }
    }

    private void materializeReportFormulas(Workbook workbook) { FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator(); for (int s = 1; s <= 4 && s < workbook.getNumberOfSheets(); s++) { Sheet sheet = workbook.getSheetAt(s); for (Row row : sheet) for (Cell cell : row) if (cell.getCellType() == CellType.FORMULA) { try { CellValue v = evaluator.evaluate(cell); cell.setCellFormula(null); if (v == null) { cell.setBlank(); continue; } switch (v.getCellType()) { case BOOLEAN -> cell.setCellValue(v.getBooleanValue()); case NUMERIC -> cell.setCellValue(v.getNumberValue()); case STRING -> cell.setCellValue(v.getStringValue()); default -> cell.setBlank(); } } catch (RuntimeException ex) { cell.setCellFormula(null); cell.setBlank(); } } } }
    private void configurePrint(Workbook workbook) { for (int i = 0; i < workbook.getNumberOfSheets(); i++) { Sheet sheet = workbook.getSheetAt(i); sheet.getPrintSetup().setPaperSize(org.apache.poi.ss.usermodel.PrintSetup.A4_PAPERSIZE); sheet.getPrintSetup().setLandscape(false); sheet.setAutobreaks(false); sheet.getPrintSetup().setFitWidth((short) 1); sheet.getPrintSetup().setFitHeight((short) 1); sheet.setFitToPage(true); Row first = sheet.getRow(0); int lastColumn = first == null || first.getLastCellNum() < 1 ? 0 : first.getLastCellNum() - 1; workbook.setPrintArea(i, 0, lastColumn, 0, Math.max(0, sheet.getLastRowNum())); } }
    private void removeSheet(Workbook workbook, String name) { int idx = workbook.getSheetIndex(name); if (idx >= 0) workbook.removeSheetAt(idx); }
    private void renameSheet(Workbook workbook, String from, String to) { int idx = workbook.getSheetIndex(from); if (idx >= 0) workbook.setSheetName(idx, to); }
    private void set(Sheet sheet, String address, Object value) { int row = Integer.parseInt(address.substring(1)) - 1; int col = address.charAt(0) - 'A'; Row r = sheet.getRow(row); if (r == null) r = sheet.createRow(row); Cell c = r.getCell(col); if (c == null) c = r.createCell(col); if (c.getCellType() == CellType.FORMULA) c.setCellFormula(null); if (value == null) c.setBlank(); else if (value instanceof Number n) c.setCellValue(n.doubleValue()); else c.setCellValue(String.valueOf(value)); }
    private void clearRow(Sheet sheet, int row, int count) { for (int c = 0; c < count; c++) set(sheet, String.valueOf((char) ('A' + c)) + row, null); }
    private void setMerged(Sheet sheet, String address, String value) { set(sheet, address, value); }
    private String display(SubjectRow s) { return s.getScore() == null ? s.getStatus() : format(s.getScore()); }
    private String format(Double value) { return value == null ? "" : String.format(java.util.Locale.ROOT, "%.2f", value).replaceAll("\\.00$", ""); }
    private String displayTotal(Double value) { return value == null ? "—" : format(value); }
    private String displayRank(Integer rank) { return rank == null || rank <= 0 ? "暂无" : "第 " + rank + " 名"; }
    private String sanitize(String value) { if (value == null || value.isBlank()) return "未命名"; return value.replaceAll("[\\\\/:*?\"<>|]", "_"); }
    private Object rankValue(Integer rank) { return rank == null || rank <= 0 ? null : rank; }
    private String rankChangeText(Integer change) { if (change == null) return "暂无"; if (change == 0) return "持平"; return change > 0 ? "提升 " + change + " 名" : "下降 " + Math.abs(change) + " 名"; }
    private String rankChangeNarrative(Integer change) { if (change == null) return "暂无可比较的上次排名，"; if (change > 0) return "较上次排名提升，"; if (change < 0) return "较上次排名下降，"; return "与上次相比排名持平，"; }
    private SubjectRow strongest(List<SubjectRow> subjects) { return subjects.stream().filter(x -> !Double.isNaN(x.getRate())).max(Comparator.comparing(SubjectRow::getRate)).orElse(null); }
    private SubjectRow weakest(List<SubjectRow> subjects) { return subjects.stream().filter(x -> !Double.isNaN(x.getRate())).min(Comparator.comparing(SubjectRow::getRate)).orElse(null); }

    private void addSummaryPage(Document doc, ReportData d, Font title, Font normal, Font small) throws Exception { addTitle(doc, "学生个人成绩分析报告｜综合分析型", d, title, normal); addSummaryTable(doc, d, normal); PdfPTable t = table(9); String[] headers = {"学科", "满分", "成绩", "得分率", "班级排名", "年级排名", "年级均分", "差值", "等级"}; for (String h : headers) cell(t, h, normal, true); for (SubjectRow s : d.getSubjects()) { cell(t, s.getName(), small, false); cell(t, value(s.getFullScore()), small, false); cell(t, display(s), small, false); cell(t, percent(s.getRate()), small, false); cell(t, value(s.getClassRank()), small, false); cell(t, value(s.getGradeRank()), small, false); cell(t, value(s.getGradeAverage()), small, false); cell(t, value(s.getDifference()), small, false); cell(t, s.getDegree(), small, false); } doc.add(t); doc.add(new Paragraph("阶段变化", normal)); addHistoryTable(doc, d, normal, small); doc.add(new Paragraph("教师诊断与后续建议：优势学科保持稳定输出，薄弱学科优先补基础并连续跟踪。", normal)); }
    private void addTrendPage(Document doc, ReportData d, Font title, Font normal, Font small) throws Exception { addTitle(doc, "学生个人成绩分析报告｜趋势变化型", d, title, normal); addSummaryTable(doc, d, normal); PdfPTable t = table(6); for (String h : new String[]{"考试", "总分", "年级均分", "班级排名", "年级排名", "较前次总分"}) cell(t, h, normal, true); Double prev = null; for (HistoryRow h : d.getHistory()) { cell(t, h.getExamName(), small, false); cell(t, value(h.getTotalScore()), small, false); cell(t, value(h.getGradeAverage()), small, false); cell(t, value(h.getClassRank()), small, false); cell(t, value(h.getGradeRank()), small, false); cell(t, prev == null || h.getTotalScore() == null ? "—" : value(h.getTotalScore() - prev), small, false); prev = h.getTotalScore(); } doc.add(t); doc.add(new Paragraph("趋势判断：建议连续观察总分、年级排名和薄弱学科得分率，避免只依据单次考试下结论。", normal)); }
    private void addSubjectPage(Document doc, ReportData d, Font title, Font normal, Font small) throws Exception { addTitle(doc, "学生个人成绩分析报告｜学科诊断型", d, title, normal); PdfPTable t = table(9); for (String h : new String[]{"学科", "成绩", "满分", "得分率", "年级均分", "差值", "年级排名", "较上次", "诊断"}) cell(t, h, normal, true); for (SubjectRow s : d.getSubjects()) { cell(t, s.getName(), small, false); cell(t, display(s), small, false); cell(t, value(s.getFullScore()), small, false); cell(t, percent(s.getRate()), small, false); cell(t, value(s.getGradeAverage()), small, false); cell(t, value(s.getDifference()), small, false); cell(t, value(s.getGradeRank()), small, false); cell(t, value(s.getChange()), small, false); cell(t, diagnostic(s), small, false); } doc.add(t); doc.add(new Paragraph("下一阶段目标：优先处理低得分率或低于年级均分的学科。", normal)); }
    private void addParentPage(Document doc, ReportData d, Font title, Font normal, Font small) throws Exception { addTitle(doc, "学生个人成绩分析报告｜家长沟通型", d, title, normal); addSummaryTable(doc, d, normal); PdfPTable t = table(9); for (String h : new String[]{"学科", "成绩", "满分", "得分率", "班级排名", "年级排名", "较上次", "评价", "建议"}) cell(t, h, normal, true); for (SubjectRow s : d.getSubjects()) { cell(t, s.getName(), small, false); cell(t, display(s), small, false); cell(t, value(s.getFullScore()), small, false); cell(t, percent(s.getRate()), small, false); cell(t, value(s.getClassRank()), small, false); cell(t, value(s.getGradeRank()), small, false); cell(t, value(s.getChange()), small, false); cell(t, diagnostic(s), small, false); cell(t, suggestion(s), small, false); } doc.add(t); doc.add(new Paragraph("家长沟通建议：关注学习节奏和完成质量，结合连续考试变化与老师共同分析原因。", normal)); }
    private void addTitle(Document doc, String text, ReportData d, Font title, Font normal) throws Exception { Paragraph p = new Paragraph(text.replace("型", ""), title); p.setAlignment(Element.ALIGN_CENTER); doc.add(p); doc.add(new Paragraph("姓名：" + d.getStudentName() + "    学号：" + d.getStudentCode() + "    班级：" + d.getClazzName() + "    本次考试：" + d.getCurrentExamName(), normal)); }
    private void addSummaryTable(Document doc, ReportData d, Font font) throws Exception { PdfPTable t = table(8); for (String h : new String[]{"总分", "满分", "得分率", "班级排名", "年级排名", "班级人数", "年级人数", "排名变化"}) cell(t, h, font, true); for (Object v : new Object[]{d.getTotalScore(), d.getFullScore(), percent(d.getScoreRate()), rankValue(d.getClassRank()), rankValue(d.getGradeRank()), d.getClassSize(), d.getGradeSize(), rankChangeText(d.getRankChange())}) cell(t, value(v), font, false); doc.add(t); }
    private void addHistoryTable(Document doc, ReportData d, Font font, Font small) throws Exception { PdfPTable t = table(5); for (String h : new String[]{"考试", "总分", "班级排名", "年级排名", "年级均分"}) cell(t, h, font, true); for (HistoryRow h : d.getHistory()) { cell(t, h.getExamName(), small, false); cell(t, value(h.getTotalScore()), small, false); cell(t, value(h.getClassRank()), small, false); cell(t, value(h.getGradeRank()), small, false); cell(t, value(h.getGradeAverage()), small, false); } doc.add(t); }
    private PdfPTable table(int cols) { PdfPTable t = new PdfPTable(cols); t.setWidthPercentage(100); return t; }
    private void cell(PdfPTable t, String text, Font font, boolean header) { PdfPCell c = new PdfPCell(new Paragraph(text == null ? "" : text, font)); c.setHorizontalAlignment(Element.ALIGN_CENTER); c.setVerticalAlignment(Element.ALIGN_MIDDLE); c.setPadding(3); if (header) c.setBackgroundColor(new BaseColor(224, 242, 254)); t.addCell(c); }
    private String value(Object value) { if (value == null) return "—"; if (value instanceof Double d) return format(d); return String.valueOf(value); }
    private String percent(double value) { return Double.isNaN(value) ? "-" : String.format(java.util.Locale.ROOT, "%.1f%%", value * 100); }
    private String diagnostic(SubjectRow s) { if ("缺考".equals(s.getStatus()) || "未选科".equals(s.getStatus()) || "缺失".equals(s.getStatus())) return s.getStatus(); if (!Double.isNaN(s.getRate()) && s.getRate() >= .9) return "优势"; if (s.getDifference() != null && s.getDifference() < 0) return "重点关注"; return "保持"; }
    private String suggestion(SubjectRow s) { if ("缺考".equals(s.getStatus())) return "确认情况"; if ("未选科".equals(s.getStatus())) return "不评价"; if ("缺失".equals(s.getStatus())) return "待确认"; return "优势".equals(diagnostic(s)) ? "保持节奏" : "补基础、查错因"; }

    @Data
    public static class ReportData { private Long studentId; private String studentCode, studentName, clazzName, gradeName, currentExamName; private LocalDateTime currentExamDate; private Integer classSize, gradeSize, classRank, gradeRank, rankChange; private Double totalScore, fullScore, scoreRate; private List<SubjectRow> subjects = List.of(); private List<HistoryRow> history = List.of(); }
    @Data
    public static class SubjectRow { private final Long courseId; private final String name; private final Double fullScore, score; private final String status; private final double rate; private final Integer classRank, gradeRank; private final Double gradeAverage, difference, previousScore, change; private final String degree; }
    @Data
    public static class HistoryRow { private final Long examId; private final String examName; private final Double totalScore, gradeAverage; private final Integer classRank, gradeRank; private final LocalDateTime examDate; private final Double fullScore; }
    @Data
    private static class CourseConfig { private final Long courseId; private final String name; private final Double fullScore; private final Integer countInTotal; }
    @Data
    public static class GeneratedReport { private final String fileBaseName; private final byte[] excel; private final byte[] pdf; }
}
