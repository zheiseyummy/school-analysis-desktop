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
    private String chineseScore;

    @ExcelProperty(value = "数学")
    private String mathScore;

    @ExcelProperty(value = "英语")
    private String EnglishScore;

    @ExcelProperty(value = "物理")
    private String physicsScore;

    @ExcelProperty(value = "化学")
    private String chemistryScore;

    @ExcelProperty(value = "生物")
    private String organismScore;

    @ExcelProperty(value = "地理")
    private String geographyScore;
    @ExcelProperty(value = "历史")
    private String historyScore;
    @ExcelProperty(value = "政治")
    private String politicsScore;




}
