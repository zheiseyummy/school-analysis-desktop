package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.entity.SysScore;
import com.youlai.system.model.entity.SysScoreImportChangeLog;
import com.youlai.system.model.vo.ScoreImportBatchVO;

import java.util.List;

public interface SysScoreImportChangeLogService extends IService<SysScoreImportChangeLog> {
    List<ScoreImportBatchVO> listBatches(Long examId);

    SysScoreImportChangeLog saveChange(String batchId, Long examId, String fileName, int rowNumber,
                                       String studentCode, String studentName, String courseName,
                                       Long studentId, Long courseId, String action,
                                       SysScore before, SysScore after);

    int undoBatch(String batchId);
}
