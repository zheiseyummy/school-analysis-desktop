package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.system.mapper.SysScoreImportChangeLogMapper;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.entity.SysScoreImportChangeLog;
import com.youlai.system.model.vo.ScoreImportBatchVO;
import com.youlai.system.service.SysScoreImportChangeLogService;
import com.youlai.system.service.SysScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysScoreImportChangeLogServiceImpl extends ServiceImpl<SysScoreImportChangeLogMapper, SysScoreImportChangeLog>
        implements SysScoreImportChangeLogService {
    private final ObjectMapper objectMapper;
    private final SysScoreService scoreService;

    @Override
    public List<ScoreImportBatchVO> listBatches(Long examId) {
        LambdaQueryWrapper<SysScoreImportChangeLog> wrapper = new LambdaQueryWrapper<SysScoreImportChangeLog>()
                .orderByDesc(SysScoreImportChangeLog::getCreateTime);
        if (examId != null) wrapper.eq(SysScoreImportChangeLog::getExamId, examId);
        List<SysScoreImportChangeLog> logs = list(wrapper);
        Map<String, List<SysScoreImportChangeLog>> grouped = logs.stream().collect(Collectors.groupingBy(
                SysScoreImportChangeLog::getBatchId, LinkedHashMap::new, Collectors.toList()));
        return grouped.values().stream().map(batch -> {
            SysScoreImportChangeLog first = batch.get(0);
            ScoreImportBatchVO vo = new ScoreImportBatchVO();
            vo.setBatchId(first.getBatchId());
            vo.setExamId(first.getExamId());
            vo.setFileName(first.getFileName());
            vo.setCreatedAt(first.getCreateTime());
            vo.setChangeCount(batch.size());
            int undone = (int) batch.stream().filter(log -> Integer.valueOf(1).equals(log.getUndone())).count();
            vo.setUndoneCount(undone);
            vo.setCanUndo(undone < batch.size());
            return vo;
        }).limit(30).toList();
    }

    @Override
    public SysScoreImportChangeLog saveChange(String batchId, Long examId, String fileName, int rowNumber,
                                               String studentCode, String studentName, String courseName,
                                               Long studentId, Long courseId, String action,
                                               SysScore before, SysScore after) {
        SysScoreImportChangeLog log = new SysScoreImportChangeLog();
        log.setBatchId(batchId); log.setExamId(examId); log.setFileName(fileName); log.setRowNumber(rowNumber);
        log.setStudentCode(studentCode); log.setStudentName(studentName); log.setCourseName(courseName);
        log.setStudentId(studentId); log.setCourseId(courseId); log.setAction(action);
        log.setBeforeJson(toJson(before)); log.setAfterJson(toJson(after)); log.setUndone(0);
        save(log);
        return log;
    }

    @Override
    @Transactional
    public int undoBatch(String batchId) {
        if (batchId == null || batchId.isBlank()) throw new IllegalArgumentException("导入批次不能为空");
        List<SysScoreImportChangeLog> logs = list(new LambdaQueryWrapper<SysScoreImportChangeLog>()
                .eq(SysScoreImportChangeLog::getBatchId, batchId)
                .orderByAsc(SysScoreImportChangeLog::getId));
        if (logs.isEmpty()) throw new IllegalArgumentException("导入批次不存在");
        if (logs.stream().allMatch(log -> Integer.valueOf(1).equals(log.getUndone()))) {
            throw new IllegalArgumentException("该导入批次已经撤销");
        }
        for (SysScoreImportChangeLog log : logs) {
            if (Integer.valueOf(1).equals(log.getUndone())) continue;
            SysScore current = scoreService.getScoreByExamIdAndStudentIdAndCourseId(log.getExamId(), log.getStudentId(), log.getCourseId());
            SysScore expected = fromJson(log.getAfterJson());
            if (!sameScore(current, expected)) {
                throw new IllegalArgumentException("批次中存在已被其他操作修改的成绩，已停止撤销");
            }
        }
        int undone = 0;
        for (SysScoreImportChangeLog log : logs) {
            if (Integer.valueOf(1).equals(log.getUndone())) continue;
            SysScore before = fromJson(log.getBeforeJson());
            SysScore current = scoreService.getScoreByExamIdAndStudentIdAndCourseId(log.getExamId(), log.getStudentId(), log.getCourseId());
            if (before == null) {
                if (current != null) scoreService.removeById(current.getId());
            } else if (current == null) {
                before.setId(null);
                scoreService.save(before);
            } else {
                before.setId(current.getId());
                scoreService.updateById(before);
            }
            log.setUndone(1);
            log.setUpdateTime(LocalDateTime.now());
            updateById(log);
            undone++;
        }
        return undone;
    }

    private String toJson(SysScore score) {
        if (score == null) return null;
        try { return objectMapper.writeValueAsString(score); }
        catch (JsonProcessingException e) { throw new IllegalStateException("无法保存成绩变更快照", e); }
    }

    private SysScore fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try { return objectMapper.readValue(json, SysScore.class); }
        catch (JsonProcessingException e) { throw new IllegalStateException("成绩变更快照损坏", e); }
    }

    private boolean sameScore(SysScore left, SysScore right) {
        if (left == null || right == null) return left == right;
        return Objects.equals(left.getExamId(), right.getExamId())
                && Objects.equals(left.getStudentId(), right.getStudentId())
                && Objects.equals(left.getCourseId(), right.getCourseId())
                && Objects.equals(left.getScore(), right.getScore())
                && Objects.equals(left.getStatus(), right.getStatus())
                && Objects.equals(left.getDegree(), right.getDegree());
    }
}
