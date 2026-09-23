package com.youlai.system.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ScoreImportVO {

    @ExcelProperty(value = "年级")
    private String gradeName;

    @ExcelProperty(value = "班级")
    private String clazzName;

    @ExcelProperty(value = "学号")
    private String studentCode;

    @ExcelProperty(value = "姓名")
    private String studentName;

    @ExcelProperty(value = "语文")
    private Double chineseScore;

    @ExcelProperty(value = "数学")
    private Double mathScore;

    @ExcelProperty(value = "英语")
    private Double EnglishScore;

    @ExcelProperty(value = "物理")
    private Double physicsScore;

    @ExcelProperty(value = "化学")
    private Double chemistryScore;

    @ExcelProperty(value = "生物")
    private Double organismScore;

    @ExcelProperty(value = "地理")
    private Double geographyScore;
    @ExcelProperty(value = "历史")
    private Double historyScore;
    @ExcelProperty(value = "政治")
    private Double politicsScore;




}
