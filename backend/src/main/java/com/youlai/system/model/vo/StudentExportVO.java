package com.youlai.system.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentExportVO {

    @ExcelProperty(value = "入学年份")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;

    @ExcelProperty(value = "年度")
    private Integer clazzYear;

    @ExcelProperty(value = "年级")
    private String gradeName;

    @ExcelProperty(value = "班级")
    private String clazzName;
    @ExcelProperty(value = "学号")
    private String code;

    @ExcelProperty(value = "姓名")
    private String name;


//    private Integer sex;

    @ExcelProperty(value = "性别")
    private String sexLabel;

    @ExcelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @ExcelProperty(value = "电话")
    private String phone;

    @ExcelProperty(value = "备注")
    private String remark;
}
