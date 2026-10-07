package com.youlai.system;

import com.youlai.system.model.bo.StudentScoreRankingBO;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.service.SysScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证数据库排名、单科排名和历史总分使用同一套考试科目配置。 */
@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:score_total_policy;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa", "spring.datasource.password="})
class ScoreTotalPolicyTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired SysScoreService scores;

    @BeforeEach
    void schema() {
        jdbc.execute("DROP TABLE IF EXISTS sys_score");
        jdbc.execute("DROP TABLE IF EXISTS sys_exam_course");
        jdbc.execute("""
                CREATE TABLE sys_score (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    exam_id BIGINT, grade_id BIGINT, grade_name VARCHAR(255),
                    clazz_id BIGINT, clazz_name VARCHAR(255), student_id BIGINT,
                    course_id BIGINT, teacher_id BIGINT, score DOUBLE, scaled_score DOUBLE,
                    status VARCHAR(32), degree INT, deleted INT DEFAULT 0,
                    create_time TIMESTAMP, update_time TIMESTAMP)
                """);
        jdbc.execute("""
                CREATE TABLE sys_exam_course (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY, exam_id BIGINT, course_id BIGINT,
                    full_score DOUBLE, count_in_total INT, score_mode VARCHAR(32),
                    scoring_rule_id BIGINT, sort INT)
                """);

        jdbc.update("INSERT INTO sys_exam_course(exam_id,course_id,full_score,count_in_total) VALUES(10,101,100,1)");
        jdbc.update("INSERT INTO sys_exam_course(exam_id,course_id,full_score,count_in_total) VALUES(10,102,50,0)");
        insert(10, 1, 11, 1, 101, 80D, "NORMAL", 0);
        insert(10, 1, 11, 1, 102, 50D, "NORMAL", 0);
        insert(10, 1, 11, 1, 103, 99D, "NORMAL", 0); // 已从考试配置移除的历史残留科目
        insert(10, 1, 11, 2, 101, 80D, "NORMAL", 0);
        insert(10, 1, 11, 2, 102, 0D, "NORMAL", 0);
        insert(10, 1, 11, 3, 101, null, "ABSENT", 0);
        insert(10, 1, 11, 3, 102, 50D, "NORMAL", 0);
        insert(10, 1, 11, 4, 101, 100D, "NORMAL", 1);

        // 旧考试没有科目配置：继续按实际存在的正常成绩兼容计算。
        insert(20, 1, 11, 1, 101, 50D, null, 0);
        insert(20, 1, 11, 1, 102, 50D, "NORMAL", 0);
    }

    @Test
    void totalRankingsExcludeNonCountedDisabledAbsentAndDeletedScores() {
        Map<Long, StudentScoreRankingBO> classRanks = scores.getStudentSummaryScoreRanking(10L, 11L).stream()
                .collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, Function.identity()));
        assertThat(classRanks).containsOnlyKeys(1L, 2L);
        assertThat(classRanks.get(1L).getStudentScore()).isEqualTo(80D);
        assertThat(classRanks.get(2L).getStudentScore()).isEqualTo(80D);
        assertThat(classRanks.get(1L).getStudentRank()).isEqualTo(1);
        assertThat(classRanks.get(2L).getStudentRank()).isEqualTo(1);

        assertThat(scores.getGradeStudentSummaryScoreRanking(10L, 1L))
                .extracting(StudentScoreRankingBO::getStudentId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(scores.getStudentSummaryScoreClazzRanking(10L, 1L))
                .extracting(StudentScoreRankingBO::getStudentId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void enabledNonCountedCourseKeepsIndependentRankingButDisabledCourseDoesNot() {
        List<StudentScoreRankingBO> rankings = scores.getStudentCourseScoreRanking(10L, 11L);
        assertThat(rankings).extracting(StudentScoreRankingBO::getCourseId).contains(101L, 102L).doesNotContain(103L);
        assertThat(rankings.stream().filter(row -> row.getCourseId().equals(102L)).toList())
                .extracting(StudentScoreRankingBO::getStudentId).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void legacyExamStillSumsItsActualNormalScores() {
        StudentScoreRankingBO ranking = scores.getStudentSummaryScoreRanking(20L, 11L).get(0);
        assertThat(ranking.getStudentScore()).isEqualTo(100D);

        Map<Long, Double> history = scores.getAllExamSumScoreList(1L).stream()
                .collect(Collectors.toMap(SysScore::getExamId, SysScore::getScore));
        assertThat(history).containsEntry(10L, 80D).containsEntry(20L, 100D);
    }

    private void insert(long examId, long gradeId, long clazzId, long studentId, long courseId,
                        Double score, String status, int deleted) {
        jdbc.update("INSERT INTO sys_score(exam_id,grade_id,clazz_id,student_id,course_id,score,status,degree,deleted) VALUES(?,?,?,?,?,?,?,?,?)",
                examId, gradeId, clazzId, studentId, courseId, score, status, score == null ? null : 2, deleted);
    }
}
