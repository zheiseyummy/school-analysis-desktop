package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description ="考试分页对象")
@Data
public class ExamPageVO {
    @Schema(description="考试ID")
    private Long id;

    @Schema(description="考试名称")
    private String name;

    @Schema(description="考试编码")
    private String code;

    @Schema(description = "考试年度")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;

    @Schema(description = "考试类型")
    private String examType;
    @Schema(description = "考试类型中文")
    private String examTypeStr;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "学期中文显示")
    private String semesterStr;

    @Schema(description = "考试日期")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examDate;

    @Schema(description="考试状态")
    private Integer status;

    @Schema(description="排序")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "年级班级数量")
    private String gradeClazzCount;

    @Schema(description = "年级班级列表")
    private String gradeClazzList;

    @Schema(description = "样本数")
    private Long sampleCount;
}
