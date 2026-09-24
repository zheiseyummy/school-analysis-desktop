package com.youlai.system.model.vo;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩导入的暂存预览。预览不会写入成绩表，确认接口才会提交 changes。
 */
@Data
public class ScoreImportPreviewVO {
    private String token;
    private Long examId;
    private String fileName;
    private Instant createdAt;
    private String blankPolicy;
    private int totalRows;
    private int validRows;
    private int errorRows;
    private int changeCount;
    private int blankCellCount;
    private List<ScoreImportChangeVO> changes = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
}
