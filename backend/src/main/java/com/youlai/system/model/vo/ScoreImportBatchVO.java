package com.youlai.system.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/** 成绩导入批次摘要。 */
@Data
public class ScoreImportBatchVO {
    private String batchId;
    private Long examId;
    private String fileName;
    private LocalDateTime createdAt;
    private Integer changeCount;
    private Integer undoneCount;
    private Boolean canUndo;
}
