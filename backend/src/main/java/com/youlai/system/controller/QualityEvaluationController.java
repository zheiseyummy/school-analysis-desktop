package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.common.util.QualityScoring;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/quality")
@RequiredArgsConstructor
public class QualityEvaluationController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        return Result.success(Map.of("dimensions", QualityScoring.DIMENSIONS, "semesters", QualityScoring.SEMESTERS, "semesterMaximums", QualityScoring.semesterMaximums(), "entryLevels", List.of("A", "B", "C", "N/A"), "finalLevels", List.of("A", "B", "C")));
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
}
