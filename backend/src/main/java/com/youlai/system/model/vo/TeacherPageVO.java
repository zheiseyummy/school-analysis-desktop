package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description ="职工分页对象")
@Data
public class TeacherPageVO {
    @Schema(description="职工ID")
    private Long id;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别数字")
    private Integer sex;
    @Schema(description="性别中文")
    private String sexLabel;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @Schema(description = "年龄")
    private String age;
    @Schema(description = "入职年份")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;
    @Schema(description = "电话")
    private String phone;
    @Schema(description = "头像")
    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;
}
