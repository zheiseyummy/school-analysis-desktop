package com.youlai.system.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

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
    @ExcelProperty(value = "电话")
    private String phone;
}
