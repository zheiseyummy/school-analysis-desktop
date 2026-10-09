package com.youlai.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.system.model.form.ClazzForm;
import com.youlai.system.model.query.ClazzPageQuery;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.query.ExamPageQuery;
import com.youlai.system.model.vo.TeacherImportVO;
import com.youlai.system.plugin.easyexcel.TeacherImportListener;
import com.youlai.system.controller.LocalScoreAnalysisController;
import com.youlai.system.controller.StandaloneQualityEvaluationController;
import com.youlai.system.service.SysClazzStudentService;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysStudentService;
import com.youlai.system.service.SysTeacherService;
import com.youlai.system.service.SysExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real application context with an isolated in-memory database, no MySQL/Redis service. */
@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:local_backend;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa", "spring.datasource.password="})
@AutoConfigureMockMvc
class LocalBackendTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @Autowired SysStudentService students;
    @Autowired SysTeacherService teachers;
    @Autowired SysClazzStudentService memberships;
    @Autowired SysClazzService clazzes;
    @Autowired SysExamService exams;
    @Autowired LocalScoreAnalysisController localScoreAnalysis;
    @Autowired StandaloneQualityEvaluationController standaloneQuality;

    @BeforeEach
    void schema() {
        // Only test data is created. Production migration/SQL scripts are never run.
        for (String table : new String[]{"sys_student", "sys_teacher"}) {
            jdbc.execute("CREATE TABLE IF NOT EXISTS " + table + """
                    (id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     account VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL,
                     code VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL,
                     sex INT NOT NULL, status INT NOT NULL, birth_day DATE,
                     year INT, phone VARCHAR(255), avatar VARCHAR(255), deleted INT DEFAULT 0,
                     create_time TIMESTAMP, update_time TIMESTAMP, remark VARCHAR(255))
                    """);
            jdbc.execute("DELETE FROM " + table);
        }
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS sys_clazz_student
                (id BIGINT AUTO_INCREMENT PRIMARY KEY, student_id BIGINT, clazz_id BIGINT, year INT)
                """);
        jdbc.execute("DELETE FROM sys_clazz_student");
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS sys_clazz
                (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(255) NOT NULL,
                 name VARCHAR(255) NOT NULL, sort INT, status INT, manager_id BIGINT,
                 grade_id BIGINT, clazz_type VARCHAR(32), deleted INT DEFAULT 0,
                 create_time TIMESTAMP, update_time TIMESTAMP)
                """);
        jdbc.execute("DELETE FROM sys_clazz");
        jdbc.execute("CREATE TABLE IF NOT EXISTS sys_grade (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(255), name VARCHAR(255), sort INT, status INT, manager_id BIGINT, stage VARCHAR(32), deleted INT DEFAULT 0, create_time TIMESTAMP, update_time TIMESTAMP)");
        jdbc.execute("DELETE FROM sys_grade");
        jdbc.execute("CREATE TABLE IF NOT EXISTS sys_exam (id BIGINT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255),code VARCHAR(255),year INT,semester INT,exam_type VARCHAR(64),exam_date TIMESTAMP,sort INT,status INT,deleted INT DEFAULT 0)");
        jdbc.execute("DELETE FROM sys_exam");
        jdbc.execute("CREATE TABLE IF NOT EXISTS sys_exam_body (id BIGINT AUTO_INCREMENT PRIMARY KEY,exam_id BIGINT,grade_clazz_id BIGINT,g_or_c VARCHAR(8))");
        jdbc.execute("DELETE FROM sys_exam_body");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_score_dataset (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) UNIQUE, stage VARCHAR(64), grade_name VARCHAR(128), class_name VARCHAR(128), created_at VARCHAR(64))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_score_exam (id BIGINT AUTO_INCREMENT PRIMARY KEY, dataset_id BIGINT, name VARCHAR(255), source_file VARCHAR(255), imported_at VARCHAR(64), UNIQUE(dataset_id,name))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_score_student (id BIGINT AUTO_INCREMENT PRIMARY KEY, dataset_id BIGINT, student_code VARCHAR(64), name VARCHAR(128), created_at VARCHAR(64))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_score_value (id BIGINT AUTO_INCREMENT PRIMARY KEY, exam_id BIGINT, student_id BIGINT, subject VARCHAR(64), score DOUBLE, status VARCHAR(32), UNIQUE(exam_id,student_id,subject))");
        jdbc.execute("DELETE FROM local_score_value");
        jdbc.execute("DELETE FROM local_score_student");
        jdbc.execute("DELETE FROM local_score_exam");
        jdbc.execute("DELETE FROM local_score_dataset");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_dataset (id BIGINT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255) UNIQUE,source_file VARCHAR(255),created_at VARCHAR(64),is_locked INT DEFAULT 0,generated_at VARCHAR(64),locked_at VARCHAR(64),a_ratio DOUBLE DEFAULT 0.6,b_ratio DOUBLE DEFAULT 0.35,c_ratio DOUBLE DEFAULT 0.05)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_student (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,source_code VARCHAR(64),name VARCHAR(128),UNIQUE(dataset_id,source_code))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_record (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,student_id BIGINT,semester VARCHAR(64),dimension VARCHAR(64),level_or_score VARCHAR(8),UNIQUE(dataset_id,student_id,semester,dimension))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_roster_entry (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,student_id BIGINT,semester VARCHAR(64),UNIQUE(dataset_id,student_id,semester))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_final_scope (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,student_id BIGINT,source_sheet VARCHAR(64),UNIQUE(dataset_id,student_id))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_missing_review (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,student_id BIGINT,semester VARCHAR(64),status VARCHAR(64),missing_dimensions VARCHAR(255),remark VARCHAR(255),updated_at VARCHAR(64),UNIQUE(dataset_id,student_id,semester))");
        jdbc.execute("CREATE TABLE IF NOT EXISTS local_quality_final_result (id BIGINT AUTO_INCREMENT PRIMARY KEY,dataset_id BIGINT,student_id BIGINT,dimension VARCHAR(64),cumulative_score DOUBLE,class_rank INT,automatic_level VARCHAR(16),final_level VARCHAR(16),is_manually_adjusted INT DEFAULT 0,available_terms INT DEFAULT 0,contains_na INT DEFAULT 0,UNIQUE(dataset_id,student_id,dimension))");
        for (String table : new String[]{"local_quality_final_result", "local_quality_missing_review", "local_quality_final_scope", "local_quality_roster_entry", "local_quality_record", "local_quality_student", "local_quality_dataset"}) jdbc.execute("DELETE FROM " + table);
    }

    @Test
    void newClassUsesNumericLegacyTypeCodeAndCanBeReadBack() {
        ClazzForm form = new ClazzForm();
        form.setCode("6");
        form.setGradeId(1L);

        assertThat(clazzes.saveClazz(form)).isTrue();
        assertThat(form.getClazzType()).isEqualTo("1");

        ClazzPageQuery query = new ClazzPageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        assertThat(clazzes.getClazzPage(query).getRecords())
                .singleElement()
                .satisfies(row -> assertThat(row.getClazzTypeLabel()).isEqualTo("普通班"));
    }

    @Test
    void classListRemainsReadableWhenAnOldRowContainsTheVisibleTypeLabel() {
        jdbc.update("INSERT INTO sys_clazz(code,name,grade_id,clazz_type,deleted) VALUES(?,?,?,?,0)",
                "7", "7班", 1L, "普通班");

        ClazzPageQuery query = new ClazzPageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        assertThat(clazzes.getClazzPage(query).getRecords())
                .singleElement()
                .satisfies(row -> assertThat(row.getClazzTypeLabel()).isEqualTo("普通班"));
    }

    @Test
    void classCreationAcceptsYearAndClassCodeAsTypedValues() {
        jdbc.update("INSERT INTO sys_grade(id,code,name,stage,deleted) VALUES(?,?,?,?,0)", 2026L, "2026", "2026", "初中");
        ClazzForm form = new ClazzForm();
        form.setGradeName("2026");
        form.setCode("01");

        assertThat(clazzes.saveClazz(form)).isTrue();
        assertThat(form.getGradeId()).isEqualTo(2026L);
        assertThat(jdbc.queryForObject("SELECT code FROM sys_clazz WHERE grade_id=2026", String.class)).isEqualTo("01");
        assertThat(jdbc.queryForObject("SELECT name FROM sys_clazz WHERE grade_id=2026", String.class)).isEqualTo("01班");
    }

    @Test
    void examListCanBeFilteredByStageAndRejectsMismatchedGradeOrClass() {
        jdbc.update("INSERT INTO sys_grade(id,code,name,stage,deleted) VALUES(1,'2026','2026','初中',0)");
        jdbc.update("INSERT INTO sys_grade(id,code,name,stage,deleted) VALUES(2,'2026','2026','高中',0)");
        jdbc.update("INSERT INTO sys_clazz(id,code,name,grade_id,clazz_type,deleted) VALUES(11,'01','01班',1,'1',0)");
        jdbc.update("INSERT INTO sys_clazz(id,code,name,grade_id,clazz_type,deleted) VALUES(22,'01','01班',2,'1',0)");
        jdbc.update("INSERT INTO sys_exam(id,name,code,year,semester,exam_type,status,deleted) VALUES(101,'初中考试','E101',2026,1,'考试',1,0)");
        jdbc.update("INSERT INTO sys_exam(id,name,code,year,semester,exam_type,status,deleted) VALUES(202,'高中考试','E202',2026,1,'考试',1,0)");
        jdbc.update("INSERT INTO sys_exam_body(exam_id,grade_clazz_id,g_or_c) VALUES(101,11,'C'),(202,22,'C')");

        ExamPageQuery byStage = new ExamPageQuery();
        byStage.setPageNum(1); byStage.setPageSize(10); byStage.setStage("初中");
        assertThat(exams.getExamPage(byStage).getRecords()).extracting(row -> row.getId()).containsExactly(101L);

        byStage.setGradeId(2L);
        assertThat(exams.getExamPage(byStage).getRecords()).isEmpty();
        byStage.setGradeId(null); byStage.setClazzId(22L);
        assertThat(exams.getExamPage(byStage).getRecords()).isEmpty();
    }

    @Test
    void standaloneScoreImportAcceptsMergedHeadersAndKeepsZeroAbsentAndBlankDistinct() throws Exception {
        byte[] bytes = workbookBytes(true);
        MockMultipartFile file = new MockMultipartFile("file", "fixture.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);
        Map<String, Object> preview = localScoreAnalysis.preview("独立测试集", "初中", "初三", "6班", "一模", file).getData();
        assertThat(preview.get("rowCount")).isEqualTo(2);
        assertThat(preview.get("studentCodePresent")).isEqualTo(true);
        assertThat(preview.get("subjects")).asList().containsExactlyInAnyOrder("数学", "语文");
        assertThat(preview.get("confirmable")).isEqualTo(true);

        localScoreAnalysis.confirm(Map.of("token", preview.get("token").toString()));
        Long examId = jdbc.queryForObject("SELECT id FROM local_score_exam WHERE name='一模'", Long.class);
        assertThat(jdbc.queryForObject("SELECT score FROM local_score_value WHERE exam_id=? AND subject='数学' AND status='NORMAL'", Double.class, examId)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_value WHERE exam_id=? AND status='ABSENT'", Integer.class, examId)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_value WHERE exam_id=? AND status='MISSING'", Integer.class, examId)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_student", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_clazz", Integer.class)).isZero();
    }

    @Test
    void standaloneScoreImportAllowsNoStudentCodeAndIgnoresRankColumns() throws Exception {
        byte[] bytes = workbookBytes(false);
        MockMultipartFile file = new MockMultipartFile("file", "fixture-no-code.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes);
        Map<String, Object> preview = localScoreAnalysis.preview("无学号测试集", "初中", "初三", "6班", "二模", file).getData();
        assertThat(preview.get("studentCodePresent")).isEqualTo(false);
        assertThat(preview.get("subjects")).asList().containsExactlyInAnyOrder("数学", "语文");
        assertThat(preview.get("confirmable")).isEqualTo(true);
        localScoreAnalysis.confirm(Map.of("token", preview.get("token").toString()));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_student WHERE dataset_id=(SELECT id FROM local_score_dataset WHERE name='无学号测试集')", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_value WHERE subject IN ('班级','年级','总分')", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_student", Integer.class)).isZero();
    }

    @Test
    void importsTheThreeProvidedJuniorScoreWorkbooksWhenLocalFixtureDirectoryIsConfigured() throws Exception {
        String fixtureDirectory = System.getProperty("score.fixture.dir");
        assumeTrue(fixtureDirectory != null && !fixtureDirectory.isBlank(), "local workbook fixtures were not configured");
        String[] fileNames = {"2026.3市一模.xlsx", "2026.5市二模.xlsx", "2026.5校一模.xlsx"};
        int[] expectedRows = {49, 48, 50};
        for (int index = 0; index < fileNames.length; index++) {
            Path path = Path.of(fixtureDirectory).resolve(fileNames[index]);
            MockMultipartFile file = new MockMultipartFile("file", fileNames[index],
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Files.readAllBytes(path));
            Map<String, Object> preview = localScoreAnalysis.preview("本机虚拟联调集", "初中", "初三", "测试班", "模拟考试" + (index + 1), file).getData();
            assertThat(preview.get("rowCount")).isEqualTo(expectedRows[index]);
            assertThat(preview.get("confirmable")).isEqualTo(true);
            assertThat(preview.get("subjects")).asList().hasSize(7);
            localScoreAnalysis.confirm(Map.of("token", preview.get("token").toString()));
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_exam", Integer.class)).isEqualTo(3);
        // The three workbook rosters are not identical; by code/name their union contains 51 students.
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_student", Integer.class)).isEqualTo(51);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_score_value", Integer.class)).isEqualTo(49 * 7 + 48 * 7 + 50 * 7);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_student", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_clazz", Integer.class)).isZero();
    }

    @Test
    void comprehensiveQualityUploadUsesWorkbookRosterAndMarksMissingTransferTermsAsNA() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "独立评价测试.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", qualityWorkbookBytes());
        Map<String, Object> imported = standaloneQuality.importWorkbook(file).getData();
        Long datasetId = ((Number) imported.get("datasetId")).longValue();
        assertThat(imported.get("baselineStudentCount")).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_quality_student WHERE dataset_id=?", Integer.class, datasetId)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_student", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_clazz", Integer.class)).isZero();

        List<Map<String, Object>> reviews = standaloneQuality.missingReviews(datasetId).getData();
        assertThat(reviews).anySatisfy(row -> {
            assertThat(row.get("name")).isEqualTo("测试学生乙");
            assertThat(row.get("semester")).isEqualTo("junior_1_1");
            assertThat(row.get("status")).isEqualTo("PENDING");
        });
        standaloneQuality.updateMissingReview(datasetId, findLocalQualityStudentId(datasetId, "Q002"), "junior_1_1",
                Map.of("status", "CONFIRMED_TRANSFER_IN"));
        standaloneQuality.generateFinal(datasetId);
        List<Map<String, Object>> finalRows = standaloneQuality.finalResults(datasetId).getData().get("rows") instanceof List<?> rows
                ? (List<Map<String, Object>>) rows : List.of();
        assertThat(finalRows).hasSize(10);
        assertThat(finalRows).filteredOn(row -> "Q002".equals(row.get("code")))
                .allSatisfy(row -> assertThat(row.get("finalLevel")).isEqualTo("N/A"));
        assertThat(standaloneQuality.finalResults(datasetId).getData().get("scoredStudentCount")).isEqualTo(1);
    }

    @Test
    void importsTheProvidedQualityWorkbookDirectlyWhenLocalFixturePathIsConfigured() throws Exception {
        String fixturePath = System.getProperty("quality.fixture.path");
        assumeTrue(fixturePath != null && !fixturePath.isBlank(), "local quality workbook fixture was not configured");
        Path path = Path.of(fixturePath);
        MockMultipartFile file = new MockMultipartFile("file", path.getFileName().toString(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Files.readAllBytes(path));
        Map<String, Object> imported = standaloneQuality.importWorkbook(file).getData();
        assertThat(imported.get("totalRows")).isEqualTo(258);
        assertThat(imported.get("baselineStudentCount")).isEqualTo(44);
        assertThat(((Map<?, ?>) imported.get("semesterSheets"))).hasSize(6);
        Long datasetId = ((Number) imported.get("datasetId")).longValue();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM local_quality_student WHERE dataset_id=?", Integer.class, datasetId)).isEqualTo(44);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_student", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_clazz", Integer.class)).isZero();
    }

    private static byte[] workbookBytes(boolean withCodes) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("成绩");
            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("成绩表");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));
            Row header = sheet.createRow(1);
            int col = 0;
            if (withCodes) header.createCell(col++).setCellValue("学号");
            header.createCell(col++).setCellValue("姓名");
            header.createCell(col++).setCellValue("总分");
            header.createCell(col++).setCellValue("班级");
            header.createCell(col++).setCellValue("年级");
            int chineseCol = col++;
            header.createCell(chineseCol).setCellValue("语文");
            int mathCol = col;
            header.createCell(mathCol).setCellValue("数学");
            Row first = sheet.createRow(2);
            col = 0;
            if (withCodes) first.createCell(col++).setCellValue("001");
            first.createCell(col++).setCellValue("测试甲");
            first.createCell(col++).setCellValue(100);
            first.createCell(col++).setCellValue(1);
            first.createCell(col++).setCellValue(1);
            first.createCell(col++).setCellValue(80);
            first.createCell(col).setCellValue(0);
            Row second = sheet.createRow(3);
            col = 0;
            if (withCodes) second.createCell(col++).setCellValue("002");
            second.createCell(col++).setCellValue("测试乙");
            second.createCell(col++).setCellValue(90);
            second.createCell(col++).setCellValue(2);
            second.createCell(col++).setCellValue(2);
            col++;
            second.createCell(col).setCellValue("缺考");
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private static byte[] qualityWorkbookBytes() throws Exception {
        String[] sheets = {"七上", "七下", "八上", "八下", "九上", "九下"};
        String[] dimensions = {"思想品德", "学业水平", "身心健康", "艺术素养", "实践与创新"};
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (int semester = 0; semester < sheets.length; semester++) {
                Sheet sheet = workbook.createSheet(sheets[semester]);
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("学号"); header.createCell(1).setCellValue("姓名");
                for (int i = 0; i < dimensions.length; i++) header.createCell(i + 2).setCellValue(dimensions[i]);
                int rowIndex = 1;
                for (int student = semester == 0 ? 0 : 0; student < 2; student++) {
                    if (semester == 0 && student == 1) continue;
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(student == 0 ? "Q001" : "Q002");
                    row.createCell(1).setCellValue(student == 0 ? "测试学生甲" : "测试学生乙");
                    for (int i = 0; i < dimensions.length; i++) row.createCell(i + 2).setCellValue(student == 0 ? "A" : "B");
                }
            }
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private long findLocalQualityStudentId(long datasetId, String code) {
        return jdbc.queryForObject("SELECT id FROM local_quality_student WHERE dataset_id=? AND source_code=?", Long.class, datasetId, code);
    }

    private StudentForm student(String code) {
        StudentForm form = new StudentForm();
        form.setCode(code); form.setName("测试学生"); form.setSex(1); form.setStatus(1); form.setYear(2026);
        return form;
    }

    @Test
    void savesWithoutAccountsAndProtectsTransitionalColumnsOnUpdate() {
        StudentForm form = student("001");
        assertThat(students.saveStudent(form)).isTrue();
        Long id = form.getId();
        String internalAccount = jdbc.queryForObject("SELECT account FROM sys_student WHERE id=?", String.class, id);
        assertThat(internalAccount).startsWith("local_");
        assertThat(jdbc.queryForObject("SELECT password FROM sys_student WHERE id=?", String.class, id)).isEqualTo("!LOCAL_ONLY!");
        StudentForm edit = student("001");
        edit.setId(999999L); edit.setName("修改姓名");
        assertThat(students.updateStudent(id, edit)).isTrue();
        assertThat(students.getById(id).getName()).isEqualTo("修改姓名");
        assertThat(students.getById(id).getAccount()).isNull();
        assertThat(jdbc.queryForObject("SELECT account FROM sys_student WHERE id=?", String.class, id)).isEqualTo(internalAccount);
    }

    @Test
    void keepsTeacherProfilesWithoutLogins() {
        TeacherForm form = new TeacherForm();
        form.setCode("T001"); form.setName("测试教师"); form.setSex(2); form.setStatus(1);
        assertThat(teachers.saveTeacher(form)).isTrue();
        assertThat(form.getId()).isNotNull();
        assertThat(teachers.getByCode("T001").getName()).isEqualTo("测试教师");
        assertThat(teachers.listTeacherOptions()).hasSize(1);
    }

    @Test
    void teacherImportUsesCodeAndAllowsMissingBirthDate() {
        TeacherImportListener listener = new TeacherImportListener();
        TeacherImportVO row = new TeacherImportVO();
        row.setCode("T002"); row.setName("导入教师"); row.setSexLabel("女");
        listener.invoke(row, null);
        row.setName("更正教师姓名");
        listener.invoke(row, null);
        assertThat(teachers.count()).isEqualTo(1);
        assertThat(teachers.getByCode("T002").getName()).isEqualTo("更正教师姓名");
        assertThat(teachers.getByCode("T002").getBirthDay()).isNull();
        assertThat(listener.getMsg()).contains("成功2条");
    }

    @Test
    void doesNotOverwritePreviousYearInSameClass() {
        assertThat(memberships.saveOrUpdateClazzStudent(1L, 10L, 2025)).isTrue();
        assertThat(memberships.saveOrUpdateClazzStudent(1L, 10L, 2026)).isTrue();
        assertThat(memberships.saveOrUpdateClazzStudent(1L, 10L, 2026)).isTrue();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys_clazz_student", Integer.class)).isEqualTo(2);
    }

    @Test
    void localApiWorksWithoutTokenAndBirthDate() throws Exception {
        mvc.perform(post("/api/v1/students").header("Origin", "http://127.0.0.1:3000")
                .contentType("application/json").content("""
                {"code":"002","name":"无生日学生","sex":1,"status":1,"year":2026,"clazzList":[]}
                """)).andExpect(status().isOk());
        SysStudent saved = students.getByCode("002");
        assertThat(saved).isNotNull();
        assertThat(saved.getBirthDay()).isNull();
        mvc.perform(get("/api/v1/students/" + saved.getId() + "/form"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.clazzList").isEmpty())
                .andExpect(jsonPath("$.data.account").doesNotExist());
    }

    @Test
    void omittedMembershipListPreservesExistingHistory() throws Exception {
        StudentForm form = student("003"); students.saveStudent(form);
        memberships.saveOrUpdateClazzStudent(form.getId(), 10L, 2025);
        mvc.perform(put("/api/v1/students/" + form.getId()).contentType("application/json").content("""
                {"code":"003","name":"修改资料","sex":1,"status":1,"year":2026}
                """)).andExpect(status().isOk());
        assertThat(memberships.getByStudentId(form.getId())).hasSize(1);
    }

    @Test
    void invalidMembershipRollsBackStudentChange() throws Exception {
        mvc.perform(post("/api/v1/students").contentType("application/json").content("""
                {"code":"004","name":"无效归属","sex":1,"status":1,"year":2026,"clazzList":[{}]}
                """)).andExpect(status().isBadRequest());
        assertThat(students.getByCode("004")).isNull();
    }

    @Test
    void requiresSexBeforeWritingToLegacyNotNullColumn() throws Exception {
        mvc.perform(post("/api/v1/students").contentType("application/json")
                .content("{\"code\":\"005\",\"name\":\"无性别\",\"status\":1}"))
                .andExpect(status().isBadRequest());
        assertThat(students.count()).isZero();
    }

    @Test
    void neverSerializesInternalCompatibilityCredentials() throws Exception {
        SysStudent entity = new SysStudent(); entity.setAccount("internal"); entity.setPassword("internal");
        String json = new ObjectMapper().writeValueAsString(entity);
        assertThat(json).doesNotContain("account", "password", "internal");
    }

    @Test
    void blocksCrossSiteSimplePostBeforeBusinessCode() throws Exception {
        mvc.perform(post("/api/v1/students").header("Origin", "https://outside.example")
                .contentType("application/x-www-form-urlencoded").content("code=bad"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/students").header("Origin", "null").contentType("text/plain"))
                .andExpect(status().isForbidden());
        assertThat(students.count()).isZero();
    }

    @Test
    void blocksDnsRebindingAndAllowsLocalPreview() throws Exception {
        mvc.perform(get("/api/v1/teachers/options").with(request -> {
            request.setServerName("outside.example"); return request;
        })).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/teachers/options").header("Origin", "http://127.0.0.1:4173"))
                .andExpect(status().isOk());
    }
}
