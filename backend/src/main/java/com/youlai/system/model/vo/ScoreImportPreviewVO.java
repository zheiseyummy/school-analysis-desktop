package com.youlai.system.model.vo;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    /** 实际读取的工作表名称。 */
    private String sheetName;
    /** 识别到的表头行（从 1 开始）。 */
    private int headerRowNumber;
    /** 是否已识别出可安全导入的基础列和至少一个科目列。 */
    private boolean mappingComplete;
    /** 规范字段到原始表头的映射，供导入前人工核对。 */
    private Map<String, String> headerMappings = new LinkedHashMap<>();
    /** 规范字段到原始列下标（从 0 开始）的识别结果，供人工修正映射。 */
    private Map<String, Integer> detectedColumnIndexes = new LinkedHashMap<>();
    /** 可供人工映射的原始列，键为列下标（从 0 开始）。 */
    private Map<String, String> availableColumns = new LinkedHashMap<>();
    /** 表头缺失、重复或存在歧义时的提示。 */
    private List<String> mappingWarnings = new ArrayList<>();
    private List<ScoreImportChangeVO> changes = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
}
