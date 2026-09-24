package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "年级分页对象")
@Data
public class GradePageVO {


    @Schema(description = "年级ID")
    private Long id;

    @Schema(description = "年级编号")
    private String code;

    @Schema(description = "年级名称")
    private String name;

    @Schema(description = "学段")
    private String stage;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "管理者ID")
    private Long managerId;

    @Schema(description = "管理者姓名")
    private String managerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "班级数量")
    private Long clazzCount;

    @Schema(description = "班级名称列表")
    private String clazzNameList;

}
