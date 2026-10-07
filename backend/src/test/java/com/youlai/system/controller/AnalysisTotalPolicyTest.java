package com.youlai.system.controller;

import com.youlai.system.common.constant.ScoreStatus;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.vo.ExamCourseConfigVO;
import com.youlai.system.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** 验证控制器内自行计算的诊断与进退步也遵循考试级计总分配置。 */
@ExtendWith(MockitoExtension.class)
class AnalysisTotalPolicyTest {
    @Mock BusinessService businessService;
    @Mock SysExamBodyService examBodyService;
    @Mock SysScoreService scoreService;
    @Mock SysCourseService courseService;
    @Mock SysClazzService clazzService;
    @Mock SysClazzStudentService clazzStudentService;
    @Mock SysExamService examService;
    @Mock SysExamCourseService examCourseService;
    @Mock SysArrangeService arrangeService;
    @Mock SysTeacherService teacherService;
    @Mock SysStudentService studentService;
    @Mock StudentPersonalReportService studentPersonalReportService;

    @InjectMocks AnalysisController controller;

    @Test
    @SuppressWarnings("unchecked")
    void gradeInsightsUseOnlyConfiguredCountedNormalScoresAndConfiguredFullScore() {
        long examId = 10L;
        List<SysScore> scores = List.of(
                score(examId, 1, 101, 80D, ScoreStatus.NORMAL),
                score(examId, 1, 102, 50D, ScoreStatus.NORMAL),
                score(examId, 1, 103, 99D, ScoreStatus.NORMAL),
                score(examId, 2, 101, 0D, ScoreStatus.NORMAL),
                score(examId, 2, 102, 50D, ScoreStatus.NORMAL),
                score(examId, 3, 101, null, ScoreStatus.ABSENT)
        );
        when(scoreService.getScoreListByExamIdAndGradeId(examId, 1L)).thenReturn(scores);
        when(examCourseService.hasConfig(examId)).thenReturn(true);
        when(examCourseService.getConfig(examId)).thenReturn(List.of(
                config(101, 100D, 1, true),
                config(102, 50D, 0, true),
                config(103, 100D, 1, false)
        ));

        Result<Map<String, Object>> result = controller.gradeInsights(1L, examId, null, null);
        assertThat(result.getData().get("fullScore")).isEqualTo(100D);
        assertThat(result.getData().get("studentCount")).isEqualTo(2);
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.getData().get("studentRows");
        assertThat(rows).extracting(row -> row.get("totalScore")).containsExactly(80D, 0D);
    }

    @Test
    void progressRankingIgnoresNonCountedCourseChangesAndKeepsRealZero() {
        when(scoreService.getScoreListByExamIdAndClazzId(10L, 11L)).thenReturn(List.of(
                score(10, 1, 101, 80D, ScoreStatus.NORMAL), score(10, 1, 102, 0D, ScoreStatus.NORMAL),
                score(10, 2, 101, 0D, ScoreStatus.NORMAL), score(10, 2, 102, 50D, ScoreStatus.NORMAL)));
        when(scoreService.getScoreListByExamIdAndClazzId(9L, 11L)).thenReturn(List.of(
                score(9, 1, 101, 70D, ScoreStatus.NORMAL), score(9, 1, 102, 50D, ScoreStatus.NORMAL),
                score(9, 2, 101, 0D, ScoreStatus.NORMAL), score(9, 2, 102, 0D, ScoreStatus.NORMAL)));
        when(examCourseService.hasConfig(10L)).thenReturn(true);
        when(examCourseService.hasConfig(9L)).thenReturn(true);
        when(examCourseService.getConfig(10L)).thenReturn(List.of(config(101, 100D, 1, true), config(102, 50D, 0, true)));
        when(examCourseService.getConfig(9L)).thenReturn(List.of(config(101, 100D, 1, true), config(102, 50D, 0, true)));

        List<Map<String, Object>> rows = controller.studentProgressRanking(11L, 10L, 9L).getData();
        assertThat(rows).hasSize(2);
        Map<String, Object> first = rows.stream().filter(row -> row.get("studentId").equals(1L)).findFirst().orElseThrow();
        Map<String, Object> second = rows.stream().filter(row -> row.get("studentId").equals(2L)).findFirst().orElseThrow();
        assertThat(first.get("currentScore")).isEqualTo(80D);
        assertThat(first.get("previousScore")).isEqualTo(70D);
        assertThat(first.get("scoreChange")).isEqualTo(10D);
        assertThat(second.get("currentScore")).isEqualTo(0D);
        assertThat(second.get("previousScore")).isEqualTo(0D);
    }

    private SysScore score(long examId, long studentId, long courseId, Double value, String status) {
        SysScore score = new SysScore();
        score.setExamId(examId); score.setGradeId(1L); score.setClazzId(11L);
        score.setStudentId(studentId); score.setCourseId(courseId); score.setScore(value); score.setStatus(status);
        return score;
    }

    private ExamCourseConfigVO config(long courseId, double fullScore, int countInTotal, boolean selected) {
        ExamCourseConfigVO config = new ExamCourseConfigVO();
        config.setCourseId(courseId); config.setFullScore(fullScore); config.setCountInTotal(countInTotal); config.setSelected(selected);
        return config;
    }
}
