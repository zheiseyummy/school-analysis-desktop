package com.youlai.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description ="课程分页对象")
@Data
public class CoursePageVO {

    @Schema(description="课程ID")
    private Long id;

    @Schema(description="课程名称")
    private String name;

    @Schema(description="课程编码")
    private String code;

    @Schema(description = "课程类型")
    private String subjectType;

    @Schema(description = "课程类型中文表示")
    private String subjectTypeLabel;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description="课程状态")
    private Integer status;

    @Schema(description="排序")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "人员数量")
    private Long userCount;

    @Schema(description = "人员列表")
    private String userList;
}
