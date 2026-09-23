package com.youlai.system.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
@ColumnWidth(20)
public class TeacherExportVO {
    @ExcelProperty(value = "工号")
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


    @ExcelProperty(value = "入职年份")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;


    @ExcelProperty(value = "备注")
    private String remark;
}
