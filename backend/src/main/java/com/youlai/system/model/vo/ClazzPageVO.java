package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "班级分页对象")
@Data
public class ClazzPageVO {


    @Schema(description = "班级ID")
    private Long id;

    @Schema(description = "班级编号")
    private String code;

    @Schema(description = "班级名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "管理者")
    private Long managerId;

    @Schema(description = "管理者名称")
    private String managerName;

    @Schema(description="年级Id")
    private Long gradeId;

    @Schema(description="年级名称")
    private String gradeName;

    @Schema(description = "班级类型")
    private String clazzType;

    @Schema(description = "班级类型中文表示")
    private String clazzTypeLabel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "学生数量")
    private Long studentCount;

    @Schema(description = "学生姓名列表")
    private String studentNameList;

    @Schema(description = "教学安排数量")
    private Long arrangeCount;

    @Schema(description = "教学安排列表")
    private String arrangeNameList;

}
