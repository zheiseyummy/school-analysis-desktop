package com.youlai.system.service;

import com.itextpdf.text.pdf.PdfReader;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.youlai.system.common.constant.ScoreStatus;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.vo.ExamCourseConfigVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentPersonalReportServiceTest {
    private static final long STUDENT_ID = 1L;
    private static final long EXAM_ID = 10L;
    private static final long CLAZZ_ID = 100L;
    private static final long GRADE_ID = 200L;

    @Mock private SysStudentService studentService;
    @Mock private SysScoreService scoreService;
    @Mock private SysCourseService courseService;
    @Mock private SysExamService examService;
    @Mock private SysExamCourseService examCourseService;
    @Mock private SysClazzService clazzService;
    @Mock private SysGradeService gradeService;
    @Mock private SysClazzStudentService clazzStudentService;

    private StudentPersonalReportService service;
    private SysExam exam;

    @BeforeEach
    void setUp() {
        service = new StudentPersonalReportService(studentService, scoreService, courseService, examService,
                examCourseService, clazzService, gradeService, clazzStudentService);

        SysStudent student = new SysStudent();
        student.setId(STUDENT_ID); student.setCode("S001"); student.setName("测试学生");
        exam = new SysExam();
        exam.setId(EXAM_ID); exam.setName("期末考试"); exam.setYear(2026); exam.setExamDate(LocalDateTime.of(2026, 6, 20, 9, 0));
        SysClazz clazz = new SysClazz();
        clazz.setId(CLAZZ_ID); clazz.setName("测试班"); clazz.setGradeId(GRADE_ID);
        SysGrade grade = new SysGrade();
        grade.setId(GRADE_ID); grade.setName("测试年级"); grade.setStage("初中");

        when(studentService.getById(STUDENT_ID)).thenReturn(student);
        when(examService.getById(EXAM_ID)).thenReturn(exam);
        when(examService.listByIds(any())).thenReturn(List.of(exam));
        when(scoreService.getClazzIdByExamIdAndStudentId(EXAM_ID, STUDENT_ID)).thenReturn(CLAZZ_ID);
        when(clazzService.getById(CLAZZ_ID)).thenReturn(clazz);
        when(gradeService.getById(GRADE_ID)).thenReturn(grade);
        when(clazzStudentService.studentCountBy(CLAZZ_ID, 2026)).thenReturn(1L);
        when(clazzStudentService.getStudentInfoListByGradeIdAndYear(GRADE_ID, 2026)).thenReturn(List.of());
        when(scoreService.getStudentSummaryScoreRanking(anyLong(), anyLong())).thenReturn(List.of());
        when(scoreService.getGradeStudentSummaryScoreRanking(anyLong(), anyLong())).thenReturn(List.of());
        when(scoreService.getStudentCourseScoreRanking(anyLong(), anyLong())).thenReturn(List.of());
        when(scoreService.getGradeStudentCourseScoreRanking(anyLong(), anyLong())).thenReturn(List.of());
    }

    @Test
    void shouldGenerateFourSheetExcelAndFourPagePdfForTenSubjects() throws Exception {
        List<SysCourse> courses = new ArrayList<>();
        List<SysScore> scores = new ArrayList<>();
        List<ExamCourseConfigVO> config = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            SysCourse course = course(i, "学科" + i);
            courses.add(course);
            SysScore score = score(i, i == 10 ? null : 80D, i == 10 ? ScoreStatus.ABSENT : ScoreStatus.NORMAL);
            scores.add(score);
            ExamCourseConfigVO item = new ExamCourseConfigVO();
            item.setCourseId((long) i); item.setCourseName(course.getName()); item.setFullScore(100D);
            item.setCountInTotal(1); item.setSort(i); item.setSelected(true);
            config.add(item);
        }
        stubScores(courses, scores);
        when(examCourseService.hasConfig(EXAM_ID)).thenReturn(true);
        when(examCourseService.getConfig(EXAM_ID)).thenReturn(config);

        StudentPersonalReportService.GeneratedReport report = service.generate(STUDENT_ID, EXAM_ID, true, true);
        writeQaArtifacts(report);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(report.getExcel()))) {
            assertEquals(List.of("综合分析", "趋势变化", "学科诊断", "家长沟通"),
                    List.of(workbook.getSheetName(0), workbook.getSheetName(1), workbook.getSheetName(2), workbook.getSheetName(3)));
            assertEquals("学科9", workbook.getSheet("综合分析").getRow(18).getCell(0).getStringCellValue());
            assertEquals("缺考", workbook.getSheet("综合分析").getRow(19).getCell(2).getStringCellValue());
            assertEquals(0, countFormulaCells(workbook));
            assertTrue(hasConditionalFormattingRange(workbook.getSheet("综合分析"), "D19:D20"));
            assertTrue(hasConditionalFormattingRange(workbook.getSheet("学科诊断"), "D15:D16"));
        }
        PdfReader reader = new PdfReader(report.getPdf());
        try {
            assertEquals(4, reader.getNumberOfPages());
        } finally {
            reader.close();
        }
    }

    @Test
    void shouldKeepMissingTotalSeparateFromRealZero() throws Exception {
        SysCourse course = course(1, "语文");
        SysScore score = score(1, null, ScoreStatus.ABSENT);
        stubScores(List.of(course), List.of(score));
        when(examCourseService.hasConfig(EXAM_ID)).thenReturn(false);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(service.exportExcel(STUDENT_ID, EXAM_ID)))) {
            assertEquals("总分\n—", workbook.getSheet("综合分析").getRow(4).getCell(0).getStringCellValue());
        }

        score.setStatus(ScoreStatus.NORMAL);
        score.setScore(0D);
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(service.exportExcel(STUDENT_ID, EXAM_ID)))) {
            assertEquals("总分\n0", workbook.getSheet("综合分析").getRow(4).getCell(0).getStringCellValue());
        }
    }

    @Test
    void shouldUseOnlyActualCoursesWhenOldExamHasNoCourseConfig() throws Exception {
        SysCourse used = course(1, "语文");
        SysCourse unused = course(2, "全局但未参考科目");
        stubScores(List.of(used, unused), List.of(score(1, 88D, ScoreStatus.NORMAL)));
        when(examCourseService.hasConfig(EXAM_ID)).thenReturn(false);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(service.exportExcel(STUDENT_ID, EXAM_ID)))) {
            Sheet summary = workbook.getSheet("综合分析");
            assertEquals("语文", summary.getRow(10).getCell(0).getStringCellValue());
            assertTrue(summary.getRow(11).getCell(0).getCellType() == CellType.BLANK
                    || summary.getRow(11).getCell(0).getStringCellValue().isBlank());
        }
        verify(examCourseService, never()).getConfig(eq(EXAM_ID));
    }

    @SuppressWarnings("unchecked")
    private void stubScores(List<SysCourse> courses, List<SysScore> scores) {
        when(scoreService.list(any(Wrapper.class))).thenReturn(scores);
        when(courseService.list()).thenReturn(courses);
        when(scoreService.getScoreListByExamIdAndClazzId(EXAM_ID, CLAZZ_ID)).thenReturn(scores);
        when(scoreService.getScoreListByExamIdAndGradeId(EXAM_ID, GRADE_ID)).thenReturn(scores);
    }

    private SysCourse course(long id, String name) {
        SysCourse course = new SysCourse();
        course.setId(id); course.setName(name); course.setFullScore(100); course.setSort((int) id);
        return course;
    }

    private SysScore score(long courseId, Double value, String status) {
        SysScore score = new SysScore();
        score.setExamId(EXAM_ID); score.setGradeId(GRADE_ID); score.setClazzId(CLAZZ_ID);
        score.setStudentId(STUDENT_ID); score.setCourseId(courseId); score.setScore(value); score.setStatus(status);
        return score;
    }

    private int countFormulaCells(Workbook workbook) {
        int count = 0;
        for (Sheet sheet : workbook) for (Row row : sheet) for (Cell cell : row)
            if (cell.getCellType() == CellType.FORMULA) count++;
        return count;
    }

    private boolean hasConditionalFormattingRange(Sheet sheet, String expected) {
        for (int i = 0; i < sheet.getSheetConditionalFormatting().getNumConditionalFormattings(); i++) {
            ConditionalFormatting formatting = sheet.getSheetConditionalFormatting().getConditionalFormattingAt(i);
            if (List.of(formatting.getFormattingRanges()).stream().anyMatch(range -> expected.equals(range.formatAsString()))) return true;
        }
        return false;
    }

    private void writeQaArtifacts(StudentPersonalReportService.GeneratedReport report) throws Exception {
        String target = System.getProperty("report.qa.dir");
        if (target == null || target.isBlank()) return;
        Path directory = Path.of(target).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        Files.write(directory.resolve("学生个人成绩分析报告_虚拟数据.xlsx"), report.getExcel());
        Files.write(directory.resolve("学生个人成绩分析报告_虚拟数据.pdf"), report.getPdf());
    }
}
