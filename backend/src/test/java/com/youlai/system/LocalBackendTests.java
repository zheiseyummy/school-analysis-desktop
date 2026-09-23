package com.youlai.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.vo.TeacherImportVO;
import com.youlai.system.plugin.easyexcel.TeacherImportListener;
import com.youlai.system.service.SysClazzStudentService;
import com.youlai.system.service.SysStudentService;
import com.youlai.system.service.SysTeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
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
